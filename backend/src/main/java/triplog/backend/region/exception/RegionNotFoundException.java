package triplog.backend.region.exception;

/**
 * 요청한 법정동 코드에 해당하는 Region이 없을 때 발생하는 예외입니다.
 */
public class RegionNotFoundException extends RuntimeException {

    /**
     * 조회에 사용한 법정동 코드로 예외 메시지를 생성합니다.
     *
     * @param legalRegionCode 법정동 시도 코드
     * @param legalDistrictCode 법정동 시군구 코드
     */
    public RegionNotFoundException(String legalRegionCode, String legalDistrictCode) {
        super("Region을 찾을 수 없습니다: " + legalRegionCode + "/" + legalDistrictCode);
    }
}
