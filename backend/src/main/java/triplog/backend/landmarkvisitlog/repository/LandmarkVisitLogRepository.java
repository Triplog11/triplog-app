package triplog.backend.landmarkvisitlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.landmarkvisitlog.entity.LandmarkVisitLog;

import java.time.LocalDateTime;

/**
 * LandmarkVisitLog 영속성 처리를 담당하는 Repository입니다.
 */
public interface LandmarkVisitLogRepository extends JpaRepository<LandmarkVisitLog, Long> {

    /**
     * 사용자의 랜드마크 방문 기록 존재 여부를 확인합니다.
     *
     * @param usersId   사용자 식별자
     * @param landmarkId 랜드마크 식별자
     * @return 방문 기록이 존재하면 true
     */
    boolean existsByUsersIdAndLandmarkId(String usersId, Long landmarkId);

    /**
     * 지정 기간의 랜드마크 방문 횟수를 방문 유형에 따라 집계합니다.
     *
     * @param usersId  사용자 식별자
     * @param start    집계 시작 시각
     * @param end      집계 종료 시각
     * @param visitType 방문 유형
     * @return 조건에 맞는 방문 횟수
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM landmark_visit_log current_log
            WHERE current_log.users_id = :usersId
              AND current_log.visited_at BETWEEN :start AND :end
              AND (
                  :visitType = 'ANY'
                  OR (:visitType = 'FIRST' AND NOT EXISTS (
                      SELECT 1 FROM landmark_visit_log previous_log
                      WHERE previous_log.users_id = current_log.users_id
                        AND previous_log.landmark_id = current_log.landmark_id
                        AND (previous_log.visited_at < current_log.visited_at
                          OR (previous_log.visited_at = current_log.visited_at
                            AND previous_log.landmark_visit_log_id < current_log.landmark_visit_log_id))
                  ))
                  OR (:visitType = 'REVISIT' AND EXISTS (
                      SELECT 1 FROM landmark_visit_log previous_log
                      WHERE previous_log.users_id = current_log.users_id
                        AND previous_log.landmark_id = current_log.landmark_id
                        AND (previous_log.visited_at < current_log.visited_at
                          OR (previous_log.visited_at = current_log.visited_at
                            AND previous_log.landmark_visit_log_id < current_log.landmark_visit_log_id))
                  ))
              )
            """, nativeQuery = true)
    long countVisits(
            @Param("usersId") String usersId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("visitType") String visitType
    );

    /** 사용자가 특정 랜드마크를 방문한 서로 다른 날짜 수를 조회합니다. */
    @Query(value = """
            SELECT COUNT(DISTINCT DATE(visited_at))
            FROM landmark_visit_log
            WHERE users_id = :usersId AND landmark_id = :landmarkId
            """, nativeQuery = true)
    long countDistinctVisitDates(
            @Param("usersId") String usersId,
            @Param("landmarkId") Long landmarkId
    );

    /** 사용자의 토요일·일요일 랜드마크 방문 인증 수를 조회합니다. */
    @Query(value = """
            SELECT COUNT(*)
            FROM landmark_visit_log
            WHERE users_id = :usersId AND DAYOFWEEK(visited_at) IN (1, 7)
            """, nativeQuery = true)
    long countWeekendVisits(@Param("usersId") String usersId);
}
