package triplog.backend.batch.tourapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TourAPI 관광정보 변경 목록의 콘텐츠 식별자와 노출 상태입니다.
 *
 * @param contentId TourAPI 콘텐츠 식별자
 * @param contentTypeId 콘텐츠 유형 식별자
 * @param modifiedTime 제공기관 최종 수정시각
 * @param showFlag 콘텐츠 노출 여부
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiChangedContentItem(
        @JsonProperty("contentid") String contentId,
        @JsonProperty("contenttypeid") String contentTypeId,
        @JsonProperty("modifiedtime") String modifiedTime,
        @JsonProperty("showflag") @JsonAlias("showFlag") String showFlag
) {

    /**
     * 변경 목록에서 삭제 또는 비표출 상태로 전달되었는지 확인합니다.
     *
     * @return 비표출 상태이면 true
     */
    public boolean hidden() {
        return "0".equals(showFlag) || "N".equalsIgnoreCase(showFlag);
    }
}
