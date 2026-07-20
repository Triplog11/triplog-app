package triplog.backend.batch.tourapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
}
