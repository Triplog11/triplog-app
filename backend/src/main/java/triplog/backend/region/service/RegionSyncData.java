package triplog.backend.region.service;

/**
 * TourAPI 법정동 응답을 Region 저장에 필요한 값으로 변환한 입력 데이터입니다.
 *
 * @param regionName 시군구 표시명
 * @param legalRegionCode 법정동 시도 코드
 * @param legalDistrictCode 법정동 시군구 코드
 */
public record RegionSyncData(
        String regionName,
        String legalRegionCode,
        String legalDistrictCode
) {
}
