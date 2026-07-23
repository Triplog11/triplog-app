package triplog.backend.batch.tourapi.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;
import triplog.backend.batch.tourapi.config.TourApiProperties;
import triplog.backend.batch.tourapi.dto.TourApiCommonItem;
import triplog.backend.batch.tourapi.dto.TourApiChangedContentItem;
import triplog.backend.batch.tourapi.dto.TourApiFestivalItem;
import triplog.backend.batch.tourapi.dto.TourApiEventIntroItem;
import triplog.backend.batch.tourapi.dto.TourApiImageItem;
import triplog.backend.batch.tourapi.dto.TourApiLegalDistrictItem;
import triplog.backend.batch.tourapi.dto.TourApiPage;
import triplog.backend.batch.tourapi.exception.TourApiException;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static triplog.backend.batch.tourapi.exception.TourApiErrorCode.*;

/**
 * TourApiClient의 HTTP 요청 및 응답 검증 기능을 테스트합니다.
 */
class TourApiClientTest {

    private static final String BASE_URL = "https://apis.data.go.kr/B551011/KorService2";
    private static final String SERVICE_KEY = "encoded%2Fservice%2Bkey%3D%3D";
    private static final String DECODED_SERVICE_KEY = "encoded/service+key==";
    private static final String CONTENT_ID = "264432";

    private MockRestServiceServer mockServer;
    private TourApiClient tourApiClient;

    @BeforeEach
    void setUp() {
        configureClient(BASE_URL, SERVICE_KEY);
    }

    private void configureClient(String configuredBaseUrl, String serviceKey) {
        RestClient.Builder restClientBuilder = RestClient.builder().baseUrl(BASE_URL);
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();

        TourApiProperties properties = new TourApiProperties(
                configuredBaseUrl,
                serviceKey,
                "ETC",
                "Triplog",
                Duration.ofSeconds(3),
                Duration.ofSeconds(10)
        );
        tourApiClient = new TourApiClient(
                restClientBuilder.build(),
                properties,
                new ObjectMapper()
        );
    }

    @Test
    @DisplayName("contentId로 TourAPI 공통정보를 조회한다")
    void contentId로_TourAPI_공통정보를_조회한다() {
        // Given
        String responseBody = """
                {
                  "response": {
                    "header": {
                      "resultCode": "0000",
                      "resultMsg": "OK"
                    },
                    "body": {
                      "items": {
                        "item": [{
                          "contentid": "264432",
                          "contenttypeid": "12",
                          "title": "경복궁",
                          "addr1": "서울특별시 종로구 사직로 161",
                          "mapx": "126.9769930325",
                          "mapy": "37.5788222356",
                          "lDongRegnCd": "11",
                          "lDongSignguCd": "110"
                        }]
                      },
                      "numOfRows": 10,
                      "pageNo": 1,
                      "totalCount": 1
                    }
                  }
                }
                """;

        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // When
        TourApiCommonItem result = tourApiClient.getCommonDetail(CONTENT_ID);

        // Then
        assertThat(result.contentId()).isEqualTo(CONTENT_ID);
        assertThat(result.contentTypeId()).isEqualTo("12");
        assertThat(result.title()).isEqualTo("경복궁");
        assertThat(result.legalRegionCode()).isEqualTo("11");
        assertThat(result.legalDistrictCode()).isEqualTo("110");
        mockServer.verify();
    }

    @Test
    @DisplayName("법정동 시도 코드 페이지를 조회한다")
    void 법정동_시도_코드_페이지를_조회한다() {
        // Given
        String responseBody = createLegalDistrictResponse("""
                [{"code":"11","name":"서울특별시"},{"code":"26","name":"부산광역시"}]
                """, 2);
        mockServer.expect(request -> assertLegalDistrictRequest(request.getURI(), null))
                .andExpect(method(GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // When
        TourApiPage<TourApiLegalDistrictItem> result = tourApiClient.getLegalRegions(1, 100);

        // Then
        assertThat(result.items()).extracting(TourApiLegalDistrictItem::code)
                .containsExactly("11", "26");
        assertThat(result.isLastPage()).isTrue();
        mockServer.verify();
    }

    @Test
    @DisplayName("법정동 시도 코드로 시군구 코드 페이지를 조회한다")
    void 법정동_시도_코드로_시군구_코드_페이지를_조회한다() {
        // Given
        String responseBody = createLegalDistrictResponse(
                "{\"code\":\"110\",\"name\":\"종로구\"}",
                1
        );
        mockServer.expect(request -> assertLegalDistrictRequest(request.getURI(), "11"))
                .andExpect(method(GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // When
        TourApiPage<TourApiLegalDistrictItem> result = tourApiClient.getLegalDistricts("11", 1, 100);

        // Then
        assertThat(result.items()).containsExactly(new TourApiLegalDistrictItem("110", "종로구"));
        mockServer.verify();
    }

    @Test
    @DisplayName("기준일 이후 변경된 관광 콘텐츠 페이지를 조회한다")
    void 기준일_이후_변경된_관광_콘텐츠_페이지를_조회한다() {
        // Given
        String responseBody = createLegalDistrictResponse("""
                [{"contentid":"126508","contenttypeid":"12","modifiedtime":"20260721090000","showflag":"1"}]
                """, 1);
        mockServer.expect(request -> {
                    URI uri = request.getURI();
                    assertThat(uri.getPath()).isEqualTo("/B551011/KorService2/areaBasedSyncList2");
                    assertThat(uri.getRawQuery())
                            .contains("syncStatus=all")
                            .contains("syncModifiedTime=20260720");
                })
                .andExpect(method(GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // When
        TourApiPage<TourApiChangedContentItem> result = tourApiClient.getChangedContents(
                LocalDate.of(2026, 7, 20),
                1,
                100
        );

        // Then
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().contentId()).isEqualTo("126508");
        assertThat(result.items().getFirst().hidden()).isFalse();
        mockServer.verify();
    }

    @Test
    @DisplayName("기간 내 축제와 소개정보 및 이미지를 조회한다")
    void 기간_내_축제와_소개정보_및_이미지를_조회한다() {
        // Given
        mockServer.expect(request -> assertThat(request.getURI().getRawQuery())
                        .contains("eventStartDate=20260621")
                        .contains("eventEndDate=20270721"))
                .andExpect(method(GET))
                .andRespond(withSuccess(createLegalDistrictResponse("""
                        [{"contentid":"300001","contenttypeid":"15","eventstartdate":"20260720","eventenddate":"20260725"}]
                        """, 1), MediaType.APPLICATION_JSON));
        mockServer.expect(request -> assertThat(request.getURI().getPath()).endsWith("/detailIntro2"))
                .andExpect(method(GET))
                .andRespond(withSuccess(createLegalDistrictResponse("""
                        {"contentid":"300001","contenttypeid":"15","eventstartdate":"20260720","eventenddate":"20260725","eventplace":"광장"}
                        """, 1), MediaType.APPLICATION_JSON));
        mockServer.expect(request -> assertThat(request.getURI().getPath()).endsWith("/detailImage2"))
                .andExpect(method(GET))
                .andRespond(withSuccess(createLegalDistrictResponse("""
                        [{"serialnum":"1","imgname":"대표","originimgurl":"https://example.com/original.jpg","smallimageurl":"https://example.com/thumb.jpg"}]
                        """, 1), MediaType.APPLICATION_JSON));

        // When
        TourApiPage<TourApiFestivalItem> festivals = tourApiClient.searchFestivals(
                LocalDate.of(2026, 6, 21),
                LocalDate.of(2027, 7, 21),
                1,
                100
        );
        TourApiEventIntroItem intro = tourApiClient.getIntroDetail("300001", "15");
        TourApiPage<TourApiImageItem> images = tourApiClient.getImages("300001", 1, 100);

        // Then
        assertThat(festivals.items().getFirst().contentTypeId()).isEqualTo("15");
        assertThat(intro.toSyncData().eventPlace()).isEqualTo("광장");
        assertThat(images.items().getFirst().toSyncData().externalSerialNumber()).isEqualTo("1");
        mockServer.verify();
    }

    @Test
    @DisplayName("TourAPI가 HTTP 429를 반환하면 요청 제한 예외가 발생한다")
    void TourAPI가_HTTP_429를_반환하면_요청_제한_예외가_발생한다() {
        // Given
        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .header(HttpHeaders.RETRY_AFTER, "7"));

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode", "providerCode", "retryAfter")
                .containsExactly(REQUEST_LIMIT_EXCEEDED, "HTTP_429", Duration.ofSeconds(7));
        mockServer.verify();
    }

    @Test
    @DisplayName("TourAPI가 HTTP 400을 반환하면 클라이언트 오류 예외가 발생한다")
    void TourAPI가_HTTP_400을_반환하면_클라이언트_오류_예외가_발생한다() {
        // Given
        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode", "providerCode")
                .containsExactly(HTTP_CLIENT_ERROR, "HTTP_400");
        mockServer.verify();
    }

    @Test
    @DisplayName("TourAPI 결과가 빈 배열이면 콘텐츠 없음 예외가 발생한다")
    void TourAPI_결과가_빈_배열이면_콘텐츠_없음_예외가_발생한다() {
        // Given
        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(withSuccess(createCommonResponse("[]"), MediaType.APPLICATION_JSON));

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode")
                .isEqualTo(CONTENT_NOT_FOUND);
        mockServer.verify();
    }

    @Test
    @DisplayName("TourAPI 응답에 items가 없으면 콘텐츠 없음 예외가 발생한다")
    void TourAPI_응답에_items가_없으면_콘텐츠_없음_예외가_발생한다() {
        // Given
        String responseBody = """
                {
                  "response": {
                    "header": {
                      "resultCode": "0000",
                      "resultMsg": "OK"
                    },
                    "body": {
                      "numOfRows": 10,
                      "pageNo": 1,
                      "totalCount": 0
                    }
                  }
                }
                """;
        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode")
                .isEqualTo(CONTENT_NOT_FOUND);
        mockServer.verify();
    }

    @Test
    @DisplayName("TourAPI 응답 contentId가 요청과 다르면 응답 형식 예외가 발생한다")
    void TourAPI_응답_contentId가_요청과_다르면_응답_형식_예외가_발생한다() {
        // Given
        String differentItem = """
                {
                  "contentid": "999999",
                  "contenttypeid": "12",
                  "title": "다른 관광지"
                }
                """;
        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(withSuccess(createCommonResponse(differentItem), MediaType.APPLICATION_JSON));

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode")
                .isEqualTo(RESPONSE_INVALID);
        mockServer.verify();
    }

    @Test
    @DisplayName("디코딩 서비스 키도 한 번 인코딩하여 요청한다")
    void 디코딩_서비스_키도_한_번_인코딩하여_요청한다() {
        // Given
        configureClient(BASE_URL, DECODED_SERVICE_KEY);
        String item = """
                {
                  "contentid": "264432",
                  "contenttypeid": "12",
                  "title": "경복궁"
                }
                """;
        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(withSuccess(createCommonResponse(item), MediaType.APPLICATION_JSON));

        // When
        TourApiCommonItem result = tourApiClient.getCommonDetail(CONTENT_ID);

        // Then
        assertThat(result.contentId()).isEqualTo(CONTENT_ID);
        mockServer.verify();
    }

    @Test
    @DisplayName("서비스 키 URL 인코딩이 잘못되면 설정 예외가 발생한다")
    void 서비스_키_URL_인코딩이_잘못되면_설정_예외가_발생한다() {
        // Given
        configureClient(BASE_URL, "invalid%key");

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode")
                .isEqualTo(CONFIGURATION_INVALID);
        mockServer.verify();
    }

    @Test
    @DisplayName("TourAPI 기본 URL 형식이 잘못되면 설정 예외가 발생한다")
    void TourAPI_기본_URL_형식이_잘못되면_설정_예외가_발생한다() {
        // Given
        configureClient("invalid-base-url", SERVICE_KEY);

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode")
                .isEqualTo(CONFIGURATION_INVALID);
        mockServer.verify();
    }

    @Test
    @DisplayName("TourAPI 결과 코드가 실패이면 연동 예외가 발생한다")
    void TourAPI_결과_코드가_실패이면_연동_예외가_발생한다() {
        // Given
        String responseBody = """
                {
                  "response": {
                    "header": {
                      "resultCode": "22",
                      "resultMsg": "LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR"
                    },
                    "body": {}
                  }
                }
                """;

        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode", "providerCode")
                .containsExactly(REQUEST_LIMIT_EXCEEDED, "22");
        mockServer.verify();
    }

    @Test
    @DisplayName("TourAPI가 최상위 오류 응답을 반환하면 연동 예외가 발생한다")
    void TourAPI가_최상위_오류_응답을_반환하면_연동_예외가_발생한다() {
        // Given
        String responseBody = """
                {
                  "responseTime": "2026-07-18T10:00:00",
                  "resultCode": "10",
                  "resultMsg": "INVALID_REQUEST_PARAMETER_ERROR"
                }
                """;

        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode", "providerCode")
                .containsExactly(API_REQUEST_FAILED, "10");
        mockServer.verify();
    }

    @Test
    @DisplayName("TourAPI가 JSON이 아닌 응답을 반환하면 응답 형식 예외가 발생한다")
    void TourAPI가_JSON이_아닌_응답을_반환하면_응답_형식_예외가_발생한다() {
        // Given
        String responseBody = "<OpenAPI_ServiceResponse><cmmMsgHeader/></OpenAPI_ServiceResponse>";

        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_XML));

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode")
                .isEqualTo(RESPONSE_INVALID);
        mockServer.verify();
    }

    @Test
    @DisplayName("TourAPI가 HTTP 500을 반환하면 서비스 일시 장애 예외가 발생한다")
    void TourAPI가_HTTP_500을_반환하면_서비스_일시_장애_예외가_발생한다() {
        // Given
        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(withServerError());

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode", "providerCode")
                .containsExactly(SERVICE_UNAVAILABLE, "HTTP_500");
        mockServer.verify();
    }

    @Test
    @DisplayName("TourAPI 전송 중 입출력 오류가 발생하면 HTTP 요청 예외가 발생한다")
    void TourAPI_전송_중_입출력_오류가_발생하면_HTTP_요청_예외가_발생한다() {
        // Given
        mockServer.expect(request -> assertCommonDetailRequest(request.getURI()))
                .andExpect(method(GET))
                .andRespond(request -> {
                    throw new IOException("connection reset");
                });

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(CONTENT_ID))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode")
                .isEqualTo(HTTP_REQUEST_FAILED);
        mockServer.verify();
    }

    @Test
    @DisplayName("contentId가 비어 있으면 외부 요청을 보내지 않고 예외가 발생한다")
    void contentId가_비어_있으면_외부_요청을_보내지_않고_예외가_발생한다() {
        // Given
        String emptyContentId = " ";

        // When
        // Then
        assertThatThrownBy(() -> tourApiClient.getCommonDetail(emptyContentId))
                .isInstanceOf(TourApiException.class)
                .extracting("errorCode")
                .isEqualTo(CONTENT_ID_REQUIRED);
        mockServer.verify();
    }

    private void assertCommonDetailRequest(URI uri) {
        assertThat(uri.getPath()).isEqualTo("/B551011/KorService2/detailCommon2");

        Map<String, String> queryParameters = Arrays.stream(uri.getRawQuery().split("&"))
                .map(parameter -> parameter.split("=", 2))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));

        assertThat(queryParameters)
                .containsEntry("serviceKey", SERVICE_KEY)
                .containsEntry("MobileOS", "ETC")
                .containsEntry("MobileApp", "Triplog")
                .containsEntry("_type", "json")
                .containsEntry("contentId", CONTENT_ID);
    }

    private void assertLegalDistrictRequest(URI uri, String legalRegionCode) {
        assertThat(uri.getPath()).isEqualTo("/B551011/KorService2/ldongCode2");
        Map<String, String> queryParameters = Arrays.stream(uri.getRawQuery().split("&"))
                .map(parameter -> parameter.split("=", 2))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
        assertThat(queryParameters)
                .containsEntry("serviceKey", SERVICE_KEY)
                .containsEntry("pageNo", "1")
                .containsEntry("numOfRows", "100");
        if (legalRegionCode == null) {
            assertThat(queryParameters).doesNotContainKey("lDongRegnCd");
        } else {
            assertThat(queryParameters).containsEntry("lDongRegnCd", legalRegionCode);
        }
    }

    private String createCommonResponse(String itemJson) {
        return """
                {
                  "response": {
                    "header": {
                      "resultCode": "0000",
                      "resultMsg": "OK"
                    },
                    "body": {
                      "items": {
                        "item": %s
                      },
                      "numOfRows": 10,
                      "pageNo": 1,
                      "totalCount": 1
                    }
                  }
                }
                """.formatted(itemJson);
    }

    private String createLegalDistrictResponse(String itemJson, int totalCount) {
        return """
                {
                  "response": {
                    "header": {"resultCode": "0000", "resultMsg": "OK"},
                    "body": {
                      "items": {"item": %s},
                      "numOfRows": 100,
                      "pageNo": 1,
                      "totalCount": %d
                    }
                  }
                }
                """.formatted(itemJson, totalCount);
    }
}
