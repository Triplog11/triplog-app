package triplog.backend.attractionvisitlog.service;

import java.time.LocalDateTime;

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

    /** 사용자가 특정 일반 관광지를 방문한 서로 다른 날짜 수를 조회합니다. */
    long countDistinctVisitDates(String usersId, Long attractionId);

    /** 사용자의 주말 일반 관광지 방문 인증 수를 조회합니다. */
    long countWeekendVisits(String usersId);

    /**
     * 지정 기간의 일반 관광지 방문 횟수를 방문 유형에 따라 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param start 집계 시작 시각
     * @param end 집계 종료 시각
     * @param visitType 방문 유형({@code ANY}, {@code FIRST}, {@code REVISIT})
     * @return 조건에 맞는 방문 횟수
     */
    long countVisits(String usersId, LocalDateTime start, LocalDateTime end, String visitType);
}
