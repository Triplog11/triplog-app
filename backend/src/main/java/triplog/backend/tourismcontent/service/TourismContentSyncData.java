package triplog.backend.tourismcontent.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TourAPI 공통정보를 TourismContent 저장 값으로 변환한 입력 데이터입니다.
 *
 * @param externalContentId TourAPI contentId
 * @param contentTypeId TourAPI 콘텐츠 타입
 * @param title 콘텐츠 제목
 * @param overview 콘텐츠 개요
 * @param address 기본 주소
 * @param detailAddress 상세 주소
 * @param postalCode 우편번호
 * @param telephone 전화번호
 * @param homepage 홈페이지 원문
 * @param longitude 경도
 * @param latitude 위도
 * @param mapLevel 지도 레벨
 * @param legalRegionCode 법정동 시도 코드
 * @param legalDistrictCode 법정동 시군구 코드
 * @param classificationDepth1 분류체계 1Depth
 * @param classificationDepth2 분류체계 2Depth
 * @param classificationDepth3 분류체계 3Depth
 * @param primaryImageUrl 대표 이미지 URL
 * @param thumbnailImageUrl 대표 썸네일 URL
 * @param copyrightType 저작권 유형
 * @param providerCreatedAt TourAPI 등록 시각
 * @param providerModifiedAt TourAPI 수정 시각
 */
public record TourismContentSyncData(
        String externalContentId,
        String contentTypeId,
        String title,
        String overview,
        String address,
        String detailAddress,
        String postalCode,
        String telephone,
        String homepage,
        BigDecimal longitude,
        BigDecimal latitude,
        Integer mapLevel,
        String legalRegionCode,
        String legalDistrictCode,
        String classificationDepth1,
        String classificationDepth2,
        String classificationDepth3,
        String primaryImageUrl,
        String thumbnailImageUrl,
        String copyrightType,
        LocalDateTime providerCreatedAt,
        LocalDateTime providerModifiedAt
) {
}
