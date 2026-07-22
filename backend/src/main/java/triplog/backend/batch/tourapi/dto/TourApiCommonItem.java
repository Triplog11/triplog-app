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

    private BigDecimal decimal(String value) {
        return value == null || value.isBlank() ? null : new BigDecimal(value);
    }

    private Integer integer(String value) {
        return value == null || value.isBlank() ? null : Integer.valueOf(value);
    }

    private LocalDateTime dateTime(String value) {
        return value == null || value.isBlank() ? null : LocalDateTime.parse(value, PROVIDER_DATE_TIME);
    }
}
