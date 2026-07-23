package triplog.backend.batch.tourapi.seed;

/**
 * 랜드마크 CSV 한 행을 표현하는 검증 완료 값 객체입니다.
 *
 * @param contentId TourAPI 콘텐츠 식별자
 * @param displayName 서비스 표시명
 * @param expectedContentTypeId 예상 콘텐츠 유형
 * @param legalRegionCode 예상 법정동 시도 코드
 * @param legalDistrictCode 예상 법정동 시군구 코드
 * @param active 동기화 대상 여부
 */
public record LandmarkSeed(
        String contentId,
        String displayName,
        String expectedContentTypeId,
        String legalRegionCode,
        String legalDistrictCode,
        boolean active
) {
}
