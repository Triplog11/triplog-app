package triplog.backend.attractionvisitlog.service;

/**
 * 일반 관광지 방문 로그 생성 및 조회 기능을 정의하는 도메인 서비스입니다.
 */
public interface AttractionVisitLogService {

    /**
     * 사용자의 일반 관광지 방문 기록 존재 여부를 확인합니다.
     *
     * @param usersId      사용자 식별자
     * @param attractionId 일반 관광지 식별자
     * @return 방문 기록이 존재하면 true
     */
    boolean hasVisited(String usersId, Long attractionId);

    /**
     * 일반 관광지 방문 로그를 저장합니다.
     *
     * @param usersId      사용자 식별자
     * @param attractionId 일반 관광지 식별자
     */
    void createLog(String usersId, Long attractionId);
}
