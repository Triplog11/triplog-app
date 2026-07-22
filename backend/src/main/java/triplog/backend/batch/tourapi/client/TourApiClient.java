package triplog.backend.batch.tourapi.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import triplog.backend.batch.tourapi.config.TourApiProperties;
import triplog.backend.batch.tourapi.config.TourApiRetryProperties;
import triplog.backend.batch.tourapi.dto.TourApiCommonItem;
import triplog.backend.batch.tourapi.dto.TourApiChangedContentItem;
import triplog.backend.batch.tourapi.dto.TourApiFestivalItem;
import triplog.backend.batch.tourapi.dto.TourApiEventIntroItem;
import triplog.backend.batch.tourapi.dto.TourApiImageItem;
import triplog.backend.batch.tourapi.dto.TourApiLegalDistrictItem;
import triplog.backend.batch.tourapi.dto.TourApiPage;
import triplog.backend.batch.tourapi.dto.TourApiResponse;
import triplog.backend.batch.tourapi.exception.TourApiErrorCode;
import triplog.backend.batch.tourapi.exception.TourApiException;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private static final String LEGAL_DISTRICT_PATH = "/ldongCode2";
    private static final String AREA_BASED_SYNC_PATH = "/areaBasedSyncList2";
    private static final String FESTIVAL_SEARCH_PATH = "/searchFestival2";
    private static final String DETAIL_INTRO_PATH = "/detailIntro2";
    private static final String DETAIL_IMAGE_PATH = "/detailImage2";
    private static final DateTimeFormatter SYNC_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private final RestClient restClient;
    private final TourApiProperties properties;
    private final ObjectMapper objectMapper;
    private final TourApiRetryProperties retryProperties;

    /**
     * TourAPI 호출에 사용할 HTTP 클라이언트와 설정, JSON 변환기를 주입받습니다.
     *
     * @param restClient TourAPI 전용 HTTP 클라이언트
     * @param properties TourAPI 연동 설정
     * @param objectMapper JSON 응답 변환기
     */
    @Autowired
    public TourApiClient(
            @Qualifier("tourApiRestClient") RestClient restClient,
            TourApiProperties properties,
            ObjectMapper objectMapper,
            TourApiRetryProperties retryProperties
    ) {
        this.restClient = restClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.retryProperties = retryProperties;
    }

    /**
     * 별도 재시도 설정을 구성하지 않는 단위 테스트를 위한 호환 생성자입니다.
     *
     * @param restClient TourAPI 전용 HTTP 클라이언트
     * @param properties TourAPI 연동 설정
     * @param objectMapper JSON 응답 변환기
     */
    public TourApiClient(RestClient restClient, TourApiProperties properties, ObjectMapper objectMapper) {
        this(restClient, properties, objectMapper,
                new TourApiRetryProperties(0, List.of(Duration.ZERO)));
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

        String responseBody = requestWithRetry(contentId);
        TourApiCommonItem commonItem = parseCommonDetail(responseBody, contentId);

        log.info("TourAPI 공통정보 조회 완료: contentId={}", contentId);
        return commonItem;
    }

    /**
     * TourAPI가 제공하는 법정동 시도 코드 페이지를 조회합니다.
     *
     * @param pageNumber 조회할 페이지 번호
     * @param pageSize 페이지당 항목 수
     * @return 법정동 시도 코드 페이지
     */
    public TourApiPage<TourApiLegalDistrictItem> getLegalRegions(int pageNumber, int pageSize) {
        return getLegalDistricts(null, pageNumber, pageSize);
    }

    /**
     * 지정한 법정동 시도 코드에 속한 시군구 코드 페이지를 조회합니다.
     *
     * @param legalRegionCode 법정동 시도 코드
     * @param pageNumber 조회할 페이지 번호
     * @param pageSize 페이지당 항목 수
     * @return 법정동 시군구 코드 페이지
     */
    public TourApiPage<TourApiLegalDistrictItem> getLegalDistricts(
            String legalRegionCode,
            int pageNumber,
            int pageSize
    ) {
        validateListRequest(pageNumber, pageSize);
        String target = StringUtils.hasText(legalRegionCode) ? legalRegionCode : "regions";
        String responseBody = requestWithRetry(
                "법정동 코드",
                target,
                buildLegalDistrictUri(legalRegionCode, pageNumber, pageSize)
        );
        return parsePage(responseBody, TourApiLegalDistrictItem.class);
    }

    /**
     * 지정한 날짜 이후 변경된 관광 콘텐츠 페이지를 조회합니다.
     * 신규·수정·삭제 상태를 모두 받도록 {@code syncStatus=all}을 사용합니다.
     *
     * @param modifiedDate 변경 조회 기준일
     * @param pageNumber 조회할 페이지 번호
     * @param pageSize 페이지당 항목 수
     * @return 변경된 관광 콘텐츠 페이지
     */
    public TourApiPage<TourApiChangedContentItem> getChangedContents(
            LocalDate modifiedDate,
            int pageNumber,
            int pageSize
    ) {
        if (modifiedDate == null) {
            throw new TourApiException(RESPONSE_INVALID);
        }
        validateListRequest(pageNumber, pageSize);
        String query = commonQuery()
                + "&pageNo=" + pageNumber
                + "&numOfRows=" + pageSize
                + "&syncStatus=all"
                + "&syncModifiedTime=" + modifiedDate.format(SYNC_DATE);
        String responseBody = requestWithRetry(
                "변경 목록",
                modifiedDate.toString(),
                buildUri(AREA_BASED_SYNC_PATH, query)
        );
        return parsePage(responseBody, TourApiChangedContentItem.class);
    }

    /**
     * 지정한 행사 기간과 겹치는 전국 축제 페이지를 조회합니다.
     *
     * @param startDate 검색 시작일
     * @param endDate 검색 종료일
     * @param pageNumber 조회할 페이지 번호
     * @param pageSize 페이지당 항목 수
     * @return 축제 검색 결과 페이지
     */
    public TourApiPage<TourApiFestivalItem> searchFestivals(
            LocalDate startDate,
            LocalDate endDate,
            int pageNumber,
            int pageSize
    ) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new TourApiException(RESPONSE_INVALID);
        }
        validateListRequest(pageNumber, pageSize);
        String query = commonQuery()
                + "&pageNo=" + pageNumber
                + "&numOfRows=" + pageSize
                + "&arrange=A"
                + "&eventStartDate=" + startDate.format(SYNC_DATE)
                + "&eventEndDate=" + endDate.format(SYNC_DATE);
        String responseBody = requestWithRetry(
                "축제 검색",
                startDate + "~" + endDate,
                buildUri(FESTIVAL_SEARCH_PATH, query)
        );
        return parsePage(responseBody, TourApiFestivalItem.class);
    }

    /**
     * 축제 콘텐츠의 소개정보를 조회합니다.
     *
     * @param contentId 콘텐츠 식별자
     * @param contentTypeId 콘텐츠 유형 식별자
     * @return 축제 소개정보
     */
    public TourApiEventIntroItem getIntroDetail(String contentId, String contentTypeId) {
        validateRequest(contentId);
        if (!StringUtils.hasText(contentTypeId)) {
            throw new TourApiException(RESPONSE_INVALID);
        }
        String query = commonQuery()
                + "&contentId=" + encodeQueryParameter(contentId)
                + "&contentTypeId=" + encodeQueryParameter(contentTypeId);
        String responseBody = requestWithRetry(
                "소개정보",
                contentId,
                buildUri(DETAIL_INTRO_PATH, query)
        );
        return parseFirstItem(responseBody, TourApiEventIntroItem.class);
    }

    /**
     * 콘텐츠의 원본·썸네일 이미지 페이지를 조회합니다.
     *
     * @param contentId 콘텐츠 식별자
     * @param pageNumber 조회할 페이지 번호
     * @param pageSize 페이지당 항목 수
     * @return 이미지 페이지
     */
    public TourApiPage<TourApiImageItem> getImages(String contentId, int pageNumber, int pageSize) {
        validateRequest(contentId);
        validateListRequest(pageNumber, pageSize);
        String query = commonQuery()
                + "&contentId=" + encodeQueryParameter(contentId)
                + "&imageYN=Y&subImageYN=Y"
                + "&pageNo=" + pageNumber
                + "&numOfRows=" + pageSize;
        String responseBody = requestWithRetry(
                "이미지",
                contentId,
                buildUri(DETAIL_IMAGE_PATH, query)
        );
        return parsePageAllowEmpty(responseBody, TourApiImageItem.class);
    }

    /**
     * 공통정보 요청을 재시도 정책에 따라 반복 실행합니다.
     *
     * @param contentId 조회할 TourAPI contentId
     * @return TourAPI 원본 응답 본문
     */
    private String requestWithRetry(String contentId) {
        int attempt = 0;
        while (true) {
            try {
                return requestCommonDetail(contentId);
            } catch (TourApiException exception) {
                if (!isRetryable(exception) || attempt >= retryProperties.maxRetries()) {
                    throw exception;
                }
                waitBeforeRetry(resolveRetryDelay(exception, attempt));
                attempt++;
            }
        }
    }

    /**
     * 지정한 TourAPI 요청을 재시도 정책에 따라 반복 실행합니다.
     *
     * @param operation 로그에 사용할 API 작업명
     * @param target 로그에 사용할 요청 대상
     * @param uri 호출할 전체 URI
     * @return TourAPI 원본 응답 본문
     */
    private String requestWithRetry(String operation, String target, URI uri) {
        int attempt = 0;
        while (true) {
            try {
                return request(operation, target, uri);
            } catch (TourApiException exception) {
                if (!isRetryable(exception) || attempt >= retryProperties.maxRetries()) {
                    throw exception;
                }
                waitBeforeRetry(resolveRetryDelay(exception, attempt));
                attempt++;
            }
        }
    }

    /**
     * 발생한 TourAPI 예외가 재시도 가능한 일시적 오류인지 판별합니다.
     *
     * @param exception 판별할 TourAPI 예외
     * @return 재시도 가능한 오류이면 {@code true}
     */
    private boolean isRetryable(TourApiException exception) {
        return exception.getErrorCode() == HTTP_REQUEST_FAILED
                || exception.getErrorCode() == REQUEST_LIMIT_EXCEEDED
                || exception.getErrorCode() == SERVICE_UNAVAILABLE;
    }

    /**
     * 제공자 Retry-After 값 또는 설정된 백오프 정책으로 재시도 대기시간을 결정합니다.
     *
     * @param exception 재시도 원인이 된 예외
     * @param retryIndex 현재 재시도 순번
     * @return 적용할 재시도 대기시간
     */
    private Duration resolveRetryDelay(TourApiException exception, int retryIndex) {
        return exception.getRetryAfter() == null
                ? retryProperties.delayFor(retryIndex)
                : exception.getRetryAfter();
    }

    /**
     * 다음 요청을 실행하기 전에 지정된 시간만큼 현재 스레드를 대기시킵니다.
     *
     * @param delay 대기할 시간
     */
    private void waitBeforeRetry(Duration delay) {
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new TourApiException(HTTP_REQUEST_FAILED, exception);
        }
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
                        throw createHttpStatusException(response.getStatusCode(), response.getHeaders());
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
     * 지정한 URI로 GET 요청을 보내고 HTTP 오류를 TourAPI 예외로 변환합니다.
     *
     * @param operation 로그에 사용할 API 작업명
     * @param target 로그에 사용할 요청 대상
     * @param uri 호출할 전체 URI
     * @return TourAPI 원본 응답 본문
     */
    private String request(String operation, String target, URI uri) {
        try {
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw createHttpStatusException(response.getStatusCode(), response.getHeaders());
                    })
                    .body(String.class);
        } catch (TourApiException exception) {
            throw exception;
        } catch (RestClientException exception) {
            log.warn("TourAPI {} HTTP 요청 실패: target={}", operation, target);
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
     * @param headers TourAPI가 반환한 HTTP 헤더
     * @return 상태 코드에 대응하는 TourAPI 예외
     */
    private TourApiException createHttpStatusException(HttpStatusCode statusCode, HttpHeaders headers) {
        TourApiErrorCode errorCode;
        if (statusCode.value() == 429) {
            errorCode = REQUEST_LIMIT_EXCEEDED;
        } else if (statusCode.is5xxServerError()) {
            errorCode = SERVICE_UNAVAILABLE;
        } else {
            errorCode = HTTP_CLIENT_ERROR;
        }
        return new TourApiException(
                errorCode,
                "HTTP_" + statusCode.value(),
                parseRetryAfter(headers.getFirst(HttpHeaders.RETRY_AFTER))
        );
    }

    /**
     * HTTP Retry-After 헤더를 대기시간으로 변환합니다.
     *
     * @param value 초 단위 숫자 또는 RFC 1123 형식의 일시
     * @return 계산한 대기시간 또는 유효한 값이 없으면 {@code null}
     */
    private Duration parseRetryAfter(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            long seconds = Long.parseLong(value.trim());
            return seconds < 0 ? null : Duration.ofSeconds(seconds);
        } catch (NumberFormatException ignored) {
            try {
                ZonedDateTime retryAt = ZonedDateTime.parse(
                        value,
                        DateTimeFormatter.RFC_1123_DATE_TIME
                );
                Duration delay = Duration.between(ZonedDateTime.now(ZoneOffset.UTC), retryAt);
                return delay.isNegative() ? Duration.ZERO : delay;
            } catch (java.time.format.DateTimeParseException invalidDate) {
                return null;
            }
        }
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
     * 법정동 지역 목록 조회에 필요한 URI를 생성합니다.
     *
     * @param legalRegionCode 시군구 조회 시 사용할 시도 코드
     * @param pageNumber 조회할 페이지 번호
     * @param pageSize 페이지당 항목 수
     * @return 법정동 지역 목록 요청 URI
     */
    private URI buildLegalDistrictUri(String legalRegionCode, int pageNumber, int pageSize) {
        String query = commonQuery()
                + "&pageNo=" + pageNumber
                + "&numOfRows=" + pageSize;
        if (StringUtils.hasText(legalRegionCode)) {
            query += "&lDongRegnCd=" + encodeQueryParameter(legalRegionCode);
        }
        return buildUri(LEGAL_DISTRICT_PATH, query);
    }

    /**
     * 모든 TourAPI 요청에 공통으로 필요한 인증 및 애플리케이션 쿼리를 생성합니다.
     *
     * @return 인코딩된 공통 쿼리 문자열
     */
    private String commonQuery() {
        return "serviceKey=" + encodeQueryParameter(normalizeServiceKey(properties.serviceKey()))
                + "&MobileOS=" + encodeQueryParameter(properties.mobileOs())
                + "&MobileApp=" + encodeQueryParameter(properties.mobileApp())
                + "&_type=" + encodeQueryParameter(JSON_RESPONSE_TYPE);
    }

    /**
     * 설정된 기본 URL에 API 경로와 쿼리를 결합합니다.
     *
     * @param path 호출할 API 경로
     * @param query 인코딩된 쿼리 문자열
     * @return 호출 가능한 전체 URI
     */
    private URI buildUri(String path, String query) {
        String baseUrl = properties.baseUrl().endsWith("/")
                ? properties.baseUrl().substring(0, properties.baseUrl().length() - 1)
                : properties.baseUrl();
        URI baseUri = URI.create(baseUrl);
        if (baseUri.getHost() == null
                || !("http".equalsIgnoreCase(baseUri.getScheme())
                || "https".equalsIgnoreCase(baseUri.getScheme()))) {
            throw new IllegalArgumentException("TourAPI baseUrl 형식이 올바르지 않습니다.");
        }
        return URI.create(baseUrl + path + "?" + query);
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
     * 항목이 반드시 존재해야 하는 TourAPI 페이지 응답을 변환합니다.
     *
     * @param responseBody TourAPI 원본 응답 본문
     * @param itemType 항목을 변환할 DTO 타입
     * @param <T> 페이지 항목 타입
     * @return 변환한 페이지 응답
     */
    private <T> TourApiPage<T> parsePage(String responseBody, Class<T> itemType) {
        return parsePage(responseBody, itemType, false);
    }

    /**
     * 조회 항목이 없어도 정상으로 처리하는 TourAPI 페이지 응답을 변환합니다.
     *
     * @param responseBody TourAPI 원본 응답 본문
     * @param itemType 항목을 변환할 DTO 타입
     * @param <T> 페이지 항목 타입
     * @return 변환한 페이지 응답
     */
    private <T> TourApiPage<T> parsePageAllowEmpty(String responseBody, Class<T> itemType) {
        return parsePage(responseBody, itemType, true);
    }

    /**
     * TourAPI 원본 JSON을 페이지 DTO로 변환하고 응답 구조와 페이지 값을 검증합니다.
     *
     * @param responseBody TourAPI 원본 응답 본문
     * @param itemType 항목을 변환할 DTO 타입
     * @param allowEmptyItems 빈 항목 목록을 정상 응답으로 허용할지 여부
     * @param <T> 페이지 항목 타입
     * @return 변환과 검증을 마친 페이지 응답
     */
    private <T> TourApiPage<T> parsePage(
            String responseBody,
            Class<T> itemType,
            boolean allowEmptyItems
    ) {
        if (!StringUtils.hasText(responseBody)) {
            throw new TourApiException(RESPONSE_INVALID);
        }
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            validateTopLevelError(rootNode);
            TourApiResponse response = objectMapper.treeToValue(rootNode, TourApiResponse.class);
            if (allowEmptyItems) {
                validatePageResponse(response);
            } else {
                validateResponse(response);
            }
            TourApiResponse.Body body = response.response().body();
            JsonNode itemNode = body.items() == null ? null : body.items().get("item");
            List<T> items = new java.util.ArrayList<>();
            if (itemNode != null && itemNode.isArray()) {
                for (JsonNode node : itemNode) {
                    items.add(objectMapper.treeToValue(node, itemType));
                }
            } else if (itemNode != null && itemNode.isObject()) {
                items.add(objectMapper.treeToValue(itemNode, itemType));
            }
            return new TourApiPage<>(
                    List.copyOf(items),
                    requiredPageValue(body.pageNo()),
                    requiredPageValue(body.numOfRows()),
                    requiredPageValue(body.totalCount())
            );
        } catch (TourApiException exception) {
            throw exception;
        } catch (JacksonException exception) {
            throw new TourApiException(RESPONSE_INVALID, exception);
        }
    }

    /**
     * TourAPI 페이지 응답에서 첫 번째 항목을 변환해 반환합니다.
     *
     * @param responseBody TourAPI 원본 응답 본문
     * @param itemType 항목을 변환할 DTO 타입
     * @param <T> 반환할 항목 타입
     * @return 조회된 첫 번째 항목
     */
    private <T> T parseFirstItem(String responseBody, Class<T> itemType) {
        TourApiPage<T> page = parsePage(responseBody, itemType);
        if (page.items().isEmpty()) {
            throw new TourApiException(CONTENT_NOT_FOUND);
        }
        return page.items().getFirst();
    }

    /**
     * 빈 항목을 허용하는 페이지 응답의 헤더와 본문 구조를 검증합니다.
     *
     * @param tourApiResponse 변환된 TourAPI 표준 응답
     */
    private void validatePageResponse(TourApiResponse tourApiResponse) {
        if (tourApiResponse == null
                || tourApiResponse.response() == null
                || tourApiResponse.response().header() == null
                || tourApiResponse.response().body() == null) {
            throw new TourApiException(RESPONSE_INVALID);
        }
        String resultCode = tourApiResponse.response().header().resultCode();
        if (!SUCCESS_RESULT_CODE.equals(resultCode)) {
            throw new TourApiException(resolveProviderErrorCode(resultCode), resultCode);
        }
    }

    /**
     * TourAPI 페이지 숫자가 누락되지 않았고 음수가 아닌지 검증합니다.
     *
     * @param value 검증할 페이지 관련 값
     * @return 검증을 통과한 기본형 정수
     */
    private int requiredPageValue(Integer value) {
        if (value == null || value < 0) {
            throw new TourApiException(RESPONSE_INVALID);
        }
        return value;
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

    /**
     * 목록 요청의 페이지 값과 필수 TourAPI 설정을 검증합니다.
     *
     * @param pageNumber 조회할 페이지 번호
     * @param pageSize 페이지당 항목 수
     */
    private void validateListRequest(int pageNumber, int pageSize) {
        if (pageNumber < 1 || pageSize < 1) {
            throw new TourApiException(RESPONSE_INVALID);
        }
        if (!StringUtils.hasText(properties.baseUrl())
                || !StringUtils.hasText(properties.serviceKey())
                || !StringUtils.hasText(properties.mobileOs())
                || !StringUtils.hasText(properties.mobileApp())) {
            throw new TourApiException(CONFIGURATION_INVALID);
        }
    }
}
