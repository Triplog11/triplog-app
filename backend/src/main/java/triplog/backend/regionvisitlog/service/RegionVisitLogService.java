package triplog.backend.regionvisitlog.service;

import java.time.LocalDateTime;

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

    /** 최근 방문이 연속으로 새로운 지역이었던 횟수를 조회합니다. */
    int countConsecutiveNewRegionVisits(String usersId);

    /**
     * 지정 기간에 사용자가 처음 방문한 지역 수를 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param start 집계 시작 시각
     * @param end 집계 종료 시각
     * @return 처음 방문한 지역 수
     */
    long countFirstVisits(String usersId, LocalDateTime start, LocalDateTime end);
}
