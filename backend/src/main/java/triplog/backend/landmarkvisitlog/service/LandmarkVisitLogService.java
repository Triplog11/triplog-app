package triplog.backend.landmarkvisitlog.service;

import java.time.LocalDateTime;

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

    /** 사용자가 특정 랜드마크를 방문한 서로 다른 날짜 수를 조회합니다. */
    long countDistinctVisitDates(String usersId, Long landmarkId);

    /** 사용자의 주말 랜드마크 방문 인증 수를 조회합니다. */
    long countWeekendVisits(String usersId);

    /**
     * 지정 기간의 랜드마크 방문 횟수를 방문 유형에 따라 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param start 집계 시작 시각
     * @param end 집계 종료 시각
     * @param visitType 방문 유형({@code ANY}, {@code FIRST}, {@code REVISIT})
     * @return 조건에 맞는 방문 횟수
     */
    long countVisits(String usersId, LocalDateTime start, LocalDateTime end, String visitType);
}
