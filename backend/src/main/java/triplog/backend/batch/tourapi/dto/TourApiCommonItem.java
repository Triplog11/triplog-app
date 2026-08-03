package triplog.backend.batch.tourapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import triplog.backend.tourismcontent.service.TourismContentSyncData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TourAPI 공통정보 조회(detailCommon2)의 관광 콘텐츠 항목입니다.
 *
 * @param contentId TourAPI 콘텐츠 식별자
 * @param contentTypeId TourAPI 콘텐츠 유형 식별자
 * @param title 콘텐츠 제목
 * @param createdTime 제공처 생성 일시 문자열
 * @param modifiedTime 제공처 수정 일시 문자열
 * @param telephone 문의 전화번호
 * @param telephoneName 전화번호 안내명
 * @param homepage 홈페이지 정보
 * @param originalImageUrl 대표 원본 이미지 URL
 * @param thumbnailImageUrl 대표 썸네일 이미지 URL
 * @param copyrightType 이미지 저작권 유형
 * @param address 기본 주소
 * @param detailAddress 상세 주소
 * @param zipcode 우편번호
 * @param longitude 경도 문자열
 * @param latitude 위도 문자열
 * @param mapLevel 지도 확대 수준 문자열
 * @param overview 콘텐츠 개요
 * @param legalRegionCode 법정동 시도 코드
 * @param legalDistrictCode 법정동 시군구 코드
 * @param classificationDepth1 관광 분류 대분류 코드
 * @param classificationDepth2 관광 분류 중분류 코드
 * @param classificationDepth3 관광 분류 소분류 코드
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiCommonItem(
        @JsonProperty("contentid") String contentId,
        @JsonProperty("contenttypeid") String contentTypeId,
        String title,
        @JsonProperty("createdtime") String createdTime,
        @JsonProperty("modifiedtime") String modifiedTime,
        @JsonProperty("tel") String telephone,
        @JsonProperty("telname") String telephoneName,
        String homepage,
        @JsonProperty("firstimage") String originalImageUrl,
        @JsonProperty("firstimage2") String thumbnailImageUrl,
        @JsonProperty("cpyrhtDivCd") @JsonAlias("cpyrhtdivcd") String copyrightType,
        @JsonProperty("addr1") String address,
        @JsonProperty("addr2") String detailAddress,
        String zipcode,
        @JsonProperty("mapx") String longitude,
        @JsonProperty("mapy") String latitude,
        @JsonProperty("mlevel") String mapLevel,
        String overview,
        @JsonProperty("lDongRegnCd") @JsonAlias("ldongregncd") String legalRegionCode,
        @JsonProperty("lDongSignguCd") @JsonAlias("ldongsigngucd") String legalDistrictCode,
        @JsonProperty("lclsSystm1") @JsonAlias("lclssystm1") String classificationDepth1,
        @JsonProperty("lclsSystm2") @JsonAlias("lclssystm2") String classificationDepth2,
        @JsonProperty("lclsSystm3") @JsonAlias("lclssystm3") String classificationDepth3
) {
    private static final DateTimeFormatter PROVIDER_DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 외부 응답 문자열을 관광 콘텐츠 도메인의 동기화 입력값으로 변환합니다.
     *
     * @return 형식 변환이 끝난 관광 콘텐츠 동기화 입력값
     * @throws IllegalArgumentException 숫자 또는 날짜 형식이 올바르지 않은 경우
     */
    public TourismContentSyncData toSyncData() {
        return new TourismContentSyncData(
                contentId,
                contentTypeId,
                title,
                overview,
                address,
                detailAddress,
                zipcode,
                telephone,
                homepage,
                decimal(longitude),
                decimal(latitude),
                integer(mapLevel),
                legalRegionCode,
                legalDistrictCode,
                classificationDepth1,
                classificationDepth2,
                classificationDepth3,
                originalImageUrl,
                thumbnailImageUrl,
                copyrightType,
                dateTime(createdTime),
                dateTime(modifiedTime)
        );
    }

    /**
     * 문자열을 소수 값으로 변환합니다.
     *
     * @param value 변환할 문자열
     * @return 변환한 소수 또는 입력값이 비어 있으면 {@code null}
     */
    private BigDecimal decimal(String value) {
        return value == null || value.isBlank() ? null : new BigDecimal(value);
    }

    /**
     * 문자열을 정수 값으로 변환합니다.
     *
     * @param value 변환할 문자열
     * @return 변환한 정수 또는 입력값이 비어 있으면 {@code null}
     */
    private Integer integer(String value) {
        return value == null || value.isBlank() ? null : Integer.valueOf(value);
    }

    /**
     * TourAPI 일시 문자열을 날짜·시간 객체로 변환합니다.
     *
     * @param value yyyyMMddHHmmss 형식의 일시 문자열
     * @return 변환한 날짜·시간 또는 입력값이 비어 있으면 {@code null}
     */
    private LocalDateTime dateTime(String value) {
        return value == null || value.isBlank() ? null : LocalDateTime.parse(value, PROVIDER_DATE_TIME);
    }
}
