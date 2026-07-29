package triplog.backend.regionvisitlog.service;

/**
 * 지역 방문 로그 생성 기능을 정의하는 도메인 서비스입니다.
 */
public interface RegionVisitLogService {

    /**
     * 지역 방문 로그를 저장합니다.
     *
     * @param usersId  사용자 식별자
     * @param regionId 지역 식별자
     */
    void createLog(String usersId, Long regionId);
}
