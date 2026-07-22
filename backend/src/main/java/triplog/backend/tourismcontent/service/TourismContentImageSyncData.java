package triplog.backend.tourismcontent.service;

/**
 * TourAPI 이미지 응답을 관광 콘텐츠 이미지 저장 값으로 변환한 입력 데이터입니다.
 *
 * @param externalSerialNumber TourAPI 이미지 일련번호
 * @param imageName 이미지명
 * @param originalImageUrl 원본 이미지 URL
 * @param thumbnailImageUrl 썸네일 이미지 URL
 * @param copyrightType 저작권 유형
 */
public record TourismContentImageSyncData(
        String externalSerialNumber,
        String imageName,
        String originalImageUrl,
        String thumbnailImageUrl,
        String copyrightType
) {
}
