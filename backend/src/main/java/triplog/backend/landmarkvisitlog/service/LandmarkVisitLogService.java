package triplog.backend.landmarkvisitlog.service;

/**
 * 랜드마크 방문 로그 생성 기능을 정의하는 도메인 서비스입니다.
 */
public interface LandmarkVisitLogService {

    /**
     * 사용자의 랜드마크 방문 기록 존재 여부를 확인합니다.
     *
     * @param usersId   사용자 식별자
     * @param landmarkId 랜드마크 식별자
     * @return 방문 기록이 존재하면 true
     */
    boolean hasVisited(String usersId, Long landmarkId);

    /**
     * 랜드마크 방문 로그를 저장합니다.
     *
     * @param usersId    사용자 식별자
     * @param landmarkId 랜드마크 식별자
     */
    void createLog(String usersId, Long landmarkId);
}
