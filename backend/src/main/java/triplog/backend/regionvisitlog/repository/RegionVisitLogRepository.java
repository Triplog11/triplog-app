package triplog.backend.regionvisitlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.regionvisitlog.entity.RegionVisitLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RegionVisitLog 영속성 처리를 담당하는 Repository입니다.
 */
public interface RegionVisitLogRepository extends JpaRepository<RegionVisitLog, Long> {

    /**
     * 지정 기간에 처음 방문한 지역 수를 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param start   집계 시작 시각
     * @param end     집계 종료 시각
     * @return 처음 방문한 지역 수
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM region_visit_log current_log
            WHERE current_log.users_id = :usersId
              AND current_log.visited_at BETWEEN :start AND :end
              AND NOT EXISTS (
                  SELECT 1 FROM region_visit_log previous_log
                  WHERE previous_log.users_id = current_log.users_id
                    AND previous_log.region_id = current_log.region_id
                    AND (previous_log.visited_at < current_log.visited_at
                      OR (previous_log.visited_at = current_log.visited_at
                        AND previous_log.region_visit_log_id < current_log.region_visit_log_id))
              )
            """, nativeQuery = true)
    long countFirstVisits(
            @Param("usersId") String usersId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /** 사용자의 지역 방문 로그를 오래된 순서로 조회합니다. */
    List<RegionVisitLog> findByUsersIdOrderByVisitedAtAscRegionVisitLogIdAsc(String usersId);
}
