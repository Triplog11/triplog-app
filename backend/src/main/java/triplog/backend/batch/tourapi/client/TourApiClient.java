package triplog.backend.batch.tourapi.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import triplog.backend.batch.tourapi.config.TourApiProperties;
import triplog.backend.batch.tourapi.dto.TourApiCommonItem;
import triplog.backend.batch.tourapi.dto.TourApiResponse;
import triplog.backend.batch.tourapi.exception.TourApiErrorCode;
import triplog.backend.batch.tourapi.exception.TourApiException;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static triplog.backend.batch.tourapi.exception.TourApiErrorCode.*;

/**
 * 한국관광공사 국문 관광정보 서비스(KorService2)를 호출하는 Client입니다.
 */
@Component
@Slf4j
public class TourApiClient {

    private static final String SUCCESS_RESULT_CODE = "0000";
    private static final String JSON_RESPONSE_TYPE = "json";
    private static final String DETAIL_COMMON_PATH = "/detailCommon2";

    private final RestClient restClient;
    private final TourApiProperties properties;
    private final ObjectMapper objectMapper;

    /**
     * TourAPI 호출에 사용할 HTTP 클라이언트와 설정, JSON 변환기를 주입받습니다.
     *
     * @param restClient TourAPI 전용 HTTP 클라이언트
     * @param properties TourAPI 연동 설정
     * @param objectMapper JSON 응답 변환기
     */
    public TourApiClient(
            @Qualifier("tourApiRestClient") RestClient restClient,
            TourApiProperties properties,
            ObjectMapper objectMapper
    ) {
        this.restClient = restClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * 지정된 contentId의 공통 관광정보를 조회합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @return TourAPI 공통 관광정보
     */
    public TourApiCommonItem getCommonDetail(String contentId) {
        validateRequest(contentId);
        log.info("TourAPI 공통정보 조회 시작: contentId={}", contentId);

        String responseBody = requestCommonDetail(contentId);
        TourApiCommonItem commonItem = parseCommonDetail(responseBody, contentId);

        log.info("TourAPI 공통정보 조회 완료: contentId={}", contentId);
        return commonItem;
    }

    /**
     * 공통정보 조회 API를 호출하고 원본 응답 본문을 반환합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @return TourAPI 원본 응답 본문
     * @throws TourApiException HTTP 요청 또는 설정 검증에 실패한 경우
     */
    private String requestCommonDetail(String contentId) {
        try {
            return restClient.get()
                    .uri(buildCommonDetailUri(contentId))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw createHttpStatusException(response.getStatusCode());
                    })
                    .body(String.class);
        } catch (TourApiException exception) {
            throw exception;
        } catch (RestClientException exception) {
            log.warn("TourAPI HTTP 요청 실패: contentId={}", contentId);
            throw new TourApiException(HTTP_REQUEST_FAILED, exception);
        } catch (IllegalArgumentException exception) {
            log.warn("TourAPI URL 또는 서비스 키 설정이 올바르지 않습니다.");
            throw new TourApiException(CONFIGURATION_INVALID, exception);
        }
    }

    /**
     * TourAPI의 HTTP 상태 코드를 애플리케이션 오류 코드로 변환합니다.
     *
     * @param statusCode TourAPI가 반환한 HTTP 상태 코드
     * @return 상태 코드에 대응하는 TourAPI 예외
     */
    private TourApiException createHttpStatusException(HttpStatusCode statusCode) {
        TourApiErrorCode errorCode;
        if (statusCode.value() == 429) {
            errorCode = REQUEST_LIMIT_EXCEEDED;
        } else if (statusCode.is5xxServerError()) {
            errorCode = SERVICE_UNAVAILABLE;
        } else {
            errorCode = HTTP_CLIENT_ERROR;
        }
        return new TourApiException(errorCode, "HTTP_" + statusCode.value());
    }

    /**
     * 공통정보 조회에 필요한 경로와 쿼리 파라미터를 조합합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @return 공통정보 조회 요청 URI
     * @throws IllegalArgumentException 기본 URL 형식이 올바르지 않은 경우
     */
    private URI buildCommonDetailUri(String contentId) {
        String baseUrl = properties.baseUrl().endsWith("/")
                ? properties.baseUrl().substring(0, properties.baseUrl().length() - 1)
                : properties.baseUrl();
        URI baseUri = URI.create(baseUrl);
        if (baseUri.getHost() == null
                || !("http".equalsIgnoreCase(baseUri.getScheme())
                || "https".equalsIgnoreCase(baseUri.getScheme()))) {
            throw new IllegalArgumentException("TourAPI baseUrl 형식이 올바르지 않습니다.");
        }
        String query = "serviceKey=" + encodeQueryParameter(normalizeServiceKey(properties.serviceKey()))
                + "&MobileOS=" + encodeQueryParameter(properties.mobileOs())
                + "&MobileApp=" + encodeQueryParameter(properties.mobileApp())
                + "&_type=" + encodeQueryParameter(JSON_RESPONSE_TYPE)
                + "&contentId=" + encodeQueryParameter(contentId);
        return URI.create(baseUrl + DETAIL_COMMON_PATH + "?" + query);
    }

    /**
     * 인코딩된 서비스 키는 한 번 디코딩하여 쿼리 파라미터의 중복 인코딩을 방지합니다.
     *
     * @param serviceKey 설정에 등록된 인코딩 또는 디코딩 서비스 키
     * @return 쿼리 파라미터 인코딩 전 서비스 키
     */
    private String normalizeServiceKey(String serviceKey) {
        if (!serviceKey.contains("%")) {
            return serviceKey;
        }
        return URLDecoder.decode(serviceKey, StandardCharsets.UTF_8);
    }

    /**
     * 쿼리 파라미터 값을 UTF-8로 인코딩합니다.
     *
     * @param value 인코딩할 값
     * @return UTF-8로 인코딩된 값
     */
    private String encodeQueryParameter(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * 원본 JSON 응답을 공통 관광정보 DTO로 변환하고 응답 구조를 검증합니다.
     *
     * @param responseBody TourAPI 원본 응답 본문
     * @param requestedContentId 요청에 사용한 콘텐츠 식별자
     * @return 변환과 검증이 완료된 공통 관광정보
     * @throws TourApiException 응답이 비어 있거나 형식이 올바르지 않은 경우
     */
    private TourApiCommonItem parseCommonDetail(String responseBody, String requestedContentId) {
        if (!StringUtils.hasText(responseBody)) {
            throw new TourApiException(RESPONSE_INVALID);
        }

        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            validateTopLevelError(rootNode);

            TourApiResponse tourApiResponse = objectMapper.treeToValue(rootNode, TourApiResponse.class);
            validateResponse(tourApiResponse);

            JsonNode itemNode = extractFirstItem(tourApiResponse.response().body().items());
            TourApiCommonItem commonItem = objectMapper.treeToValue(itemNode, TourApiCommonItem.class);
            validateCommonItem(commonItem, requestedContentId);
            return commonItem;
        } catch (TourApiException exception) {
            throw exception;
        } catch (JacksonException exception) {
            log.warn("TourAPI JSON 응답 변환 실패");
            throw new TourApiException(RESPONSE_INVALID, exception);
        }
    }

    /**
     * 변환된 관광정보가 요청한 콘텐츠와 일치하는지 검증합니다.
     *
     * @param commonItem 변환된 공통 관광정보
     * @param requestedContentId 요청에 사용한 콘텐츠 식별자
     * @throws TourApiException 콘텐츠 식별자가 없거나 요청값과 다른 경우
     */
    private void validateCommonItem(TourApiCommonItem commonItem, String requestedContentId) {
        if (commonItem == null
                || !StringUtils.hasText(commonItem.contentId())
                || !requestedContentId.equals(commonItem.contentId())) {
            throw new TourApiException(RESPONSE_INVALID);
        }
    }

    /**
     * 표준 응답 구조 밖에 반환된 TourAPI 오류 응답을 확인합니다.
     *
     * @param rootNode 원본 JSON의 최상위 노드
     * @throws TourApiException 최상위 결과 코드가 성공이 아닌 경우
     */
    private void validateTopLevelError(JsonNode rootNode) {
        JsonNode resultCodeNode = rootNode.get("resultCode");
        if (resultCodeNode == null) {
            return;
        }

        String resultCode = resultCodeNode.asString();
        if (!SUCCESS_RESULT_CODE.equals(resultCode)) {
            log.warn("TourAPI 요청 처리 실패: providerCode={}", resultCode);
            throw new TourApiException(resolveProviderErrorCode(resultCode), resultCode);
        }
    }

    /**
     * 표준 TourAPI 응답의 헤더와 본문이 정상적으로 구성되었는지 검증합니다.
     *
     * @param tourApiResponse 변환된 TourAPI 표준 응답
     * @throws TourApiException 실패 결과이거나 조회 항목이 없는 경우
     */
    private void validateResponse(TourApiResponse tourApiResponse) {
        if (tourApiResponse == null
                || tourApiResponse.response() == null
                || tourApiResponse.response().header() == null) {
            throw new TourApiException(RESPONSE_INVALID);
        }

        String resultCode = tourApiResponse.response().header().resultCode();
        if (!SUCCESS_RESULT_CODE.equals(resultCode)) {
            log.warn("TourAPI 요청 처리 실패: providerCode={}", resultCode);
            throw new TourApiException(resolveProviderErrorCode(resultCode), resultCode);
        }

        if (tourApiResponse.response().body() == null
                || tourApiResponse.response().body().items() == null) {
            throw new TourApiException(CONTENT_NOT_FOUND);
        }
    }

    /**
     * TourAPI 제공자 결과 코드를 애플리케이션 오류 코드로 변환합니다.
     *
     * @param resultCode TourAPI 제공자 결과 코드
     * @return 제공자 결과 코드에 대응하는 오류 코드
     */
    private TourApiErrorCode resolveProviderErrorCode(String resultCode) {
        return switch (resultCode) {
            case "22" -> REQUEST_LIMIT_EXCEEDED;
            case "1", "01", "2", "02", "4", "04", "5", "05", "21" -> SERVICE_UNAVAILABLE;
            default -> API_REQUEST_FAILED;
        };
    }

    /**
     * 응답의 item이 배열 또는 단일 객체인 경우 첫 번째 관광정보 노드를 추출합니다.
     *
     * @param itemsNode TourAPI items 노드
     * @return 첫 번째 관광정보 JSON 노드
     * @throws TourApiException 조회 결과가 없거나 item 형식이 올바르지 않은 경우
     */
    private JsonNode extractFirstItem(JsonNode itemsNode) {
        JsonNode itemNode = itemsNode.get("item");
        if (itemNode == null || itemNode.isNull()) {
            throw new TourApiException(CONTENT_NOT_FOUND);
        }

        if (itemNode.isArray()) {
            if (itemNode.isEmpty()) {
                throw new TourApiException(CONTENT_NOT_FOUND);
            }
            return itemNode.get(0);
        }

        if (!itemNode.isObject()) {
            throw new TourApiException(RESPONSE_INVALID);
        }
        return itemNode;
    }

    /**
     * 필수 요청값과 TourAPI 연동 설정이 입력되었는지 검증합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @throws TourApiException 콘텐츠 식별자 또는 필수 설정이 비어 있는 경우
     */
    private void validateRequest(String contentId) {
        if (!StringUtils.hasText(contentId)) {
            throw new TourApiException(CONTENT_ID_REQUIRED);
        }

        if (!StringUtils.hasText(properties.baseUrl())
                || !StringUtils.hasText(properties.serviceKey())
                || !StringUtils.hasText(properties.mobileOs())
                || !StringUtils.hasText(properties.mobileApp())) {
            throw new TourApiException(CONFIGURATION_INVALID);
        }
    }
}
