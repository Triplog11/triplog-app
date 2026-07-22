package triplog.backend.batch.tourapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TourAPI 축제 검색 결과에서 상세 동기화 대상을 판별하는 항목입니다.
 *
 * @param contentId 콘텐츠 식별자
 * @param contentTypeId 콘텐츠 유형 식별자
 * @param modifiedTime 제공기관 최종 수정시각
 * @param eventStartDate 행사 시작일 문자열
 * @param eventEndDate 행사 종료일 문자열
 * @param legalRegionCode 법정동 시도 코드
 * @param legalDistrictCode 법정동 시군구 코드
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiFestivalItem(
        @JsonProperty("contentid") String contentId,
        @JsonProperty("contenttypeid") String contentTypeId,
        @JsonProperty("modifiedtime") String modifiedTime,
        @JsonProperty("eventstartdate") String eventStartDate,
        @JsonProperty("eventenddate") String eventEndDate,
        @JsonProperty("lDongRegnCd") @JsonAlias("ldongregncd") String legalRegionCode,
        @JsonProperty("lDongSignguCd") @JsonAlias("ldongsigngucd") String legalDistrictCode
) {
}
