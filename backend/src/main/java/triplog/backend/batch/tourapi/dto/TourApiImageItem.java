package triplog.backend.batch.tourapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import triplog.backend.tourismcontent.service.TourismContentImageSyncData;

/**
 * TourAPI 이미지 항목을 관광 콘텐츠 이미지 동기화 입력값으로 변환합니다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiImageItem(
        @JsonProperty("serialnum") String serialNumber,
        @JsonProperty("imgname") String imageName,
        @JsonProperty("originimgurl") String originalImageUrl,
        @JsonProperty("smallimageurl") String thumbnailImageUrl,
        @JsonProperty("cpyrhtDivCd") @JsonAlias("cpyrhtdivcd") String copyrightType
) {

    /**
     * 이미지 저장 서비스가 사용하는 입력값으로 변환합니다.
     *
     * @return 관광 콘텐츠 이미지 동기화 입력값
     */
    public TourismContentImageSyncData toSyncData() {
        return new TourismContentImageSyncData(
                serialNumber,
                imageName,
                originalImageUrl,
                thumbnailImageUrl,
                copyrightType
        );
    }
}
