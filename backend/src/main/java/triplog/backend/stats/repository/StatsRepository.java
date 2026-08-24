package triplog.backend.stats.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import triplog.backend.stats.entity.Stats;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 사용자 통계(Stats) 엔티티의 데이터 접근을 담당하는 JPA Repository입니다.
 * <p>
 * Spring Data JPA를 기반으로 CRUD 기능을 제공하며,
 * 메서드 이름 기반 쿼리, JPQL, Query Method 등을 통해 사용자 통계 데이터를 조회하고 관리합니다.
 */
@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    String RANKING_METRIC_JOINS = """
            FROM stats s
            LEFT JOIN (
                SELECT users_id, COUNT(DISTINCT landmark_id) AS landmark_count
                FROM landmark_visit_log
                WHERE (:periodStart IS NULL OR visited_at >= :periodStart)
                GROUP BY users_id
            ) landmark_metric ON landmark_metric.users_id = s.users_id
            LEFT JOIN (
                SELECT users_id, COUNT(DISTINCT attraction_id) AS attraction_count
                FROM attraction_visit_log
                WHERE (:periodStart IS NULL OR visited_at >= :periodStart)
                GROUP BY users_id
            ) attraction_metric ON attraction_metric.users_id = s.users_id
            LEFT JOIN (
                SELECT current_log.users_id,
                       COUNT(DISTINCT current_log.region_id) AS region_count
                FROM region_visit_log current_log
                WHERE (:periodStart IS NULL OR current_log.visited_at >= :periodStart)
                  AND NOT EXISTS (
                      SELECT 1
                      FROM region_visit_log previous_log
                      WHERE previous_log.users_id = current_log.users_id
                        AND previous_log.region_id = current_log.region_id
                        AND (
                            previous_log.visited_at < current_log.visited_at
                            OR (
                                previous_log.visited_at = current_log.visited_at
                                AND previous_log.region_visit_log_id
                                    < current_log.region_visit_log_id
                            )
                        )
                  )
                GROUP BY current_log.users_id
            ) region_metric ON region_metric.users_id = s.users_id
            LEFT JOIN (
                SELECT users_id, COUNT(*) AS conquest_count
                FROM users_region
                WHERE users_region_conquered = TRUE
                  AND (:periodStart IS NULL OR users_region_conquered_at >= :periodStart)
                GROUP BY users_id
            ) conquest_metric ON conquest_metric.users_id = s.users_id
            """;

    String RANKING_ORDER = """
            ORDER BY
                CASE
                    WHEN :rankingType = 'TOTAL' THEN s.overall_score
                    WHEN :rankingType = 'MONTHLY' THEN s.month_score
                END DESC,
                COALESCE(landmark_metric.landmark_count, 0) DESC,
                COALESCE(attraction_metric.attraction_count, 0) DESC,
                COALESCE(region_metric.region_count, 0) DESC,
                COALESCE(conquest_metric.conquest_count, 0) DESC,
                CASE WHEN (
                    CASE
                        WHEN :rankingType = 'TOTAL' THEN s.overall_score_achieved_at
                        WHEN :rankingType = 'MONTHLY' THEN s.month_score_achieved_at
                    END
                ) IS NULL THEN 1 ELSE 0 END ASC,
                CASE
                    WHEN :rankingType = 'TOTAL' THEN s.overall_score_achieved_at
                    WHEN :rankingType = 'MONTHLY' THEN s.month_score_achieved_at
                END ASC,
                s.stats_id ASC
            """;

    /**
     * 월간 Score가 남아 있는 모든 사용자의 월간 Score만 0으로 초기화합니다.
     *
     * @return 초기화된 사용자 통계 행 수
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Stats s
            set s.monthScore = 0,
                s.monthScoreAchievedAt = null
            where s.monthScore <> 0 or s.monthScoreAchievedAt is not null
            """)
    int resetMonthlyScores();

    /**
     * 사용자 ID로 통계 정보를 조회합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 사용자 통계 정보, 존재하지 않으면 빈 값
     */
    Optional<Stats> findByUsersUsersId(String usersId);

    /**
     * 동일 사용자의 동시 보상 지급과 회수를 직렬화하기 위해 통계 행을 잠금 조회합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stats s where s.users.usersId = :usersId")
    Optional<Stats> findByUsersUsersIdForUpdate(@Param("usersId") String usersId);

    /**
     * 사용자 주소 프로필 정보를 수정합니다.
     * <p>
     * 요청에서 전달되지 않은 필드는 {@code null}로 들어오며 기존 값을 유지합니다.
     *
     * @param usersId 수정할 사용자 ID
     * @param addressSi 변경할 시
     * @param addressDoGun 변경할 도/군
     * @param addressGu 변경할 구
     * @return 수정된 행 수
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Stats s
            set s.addressSi = coalesce(:addressSi, s.addressSi),
                s.addressDoGun = coalesce(:addressDoGun, s.addressDoGun),
                s.addressGu = coalesce(:addressGu, s.addressGu)
            where s.users.usersId = :usersId
            """)
    int updateProfileAddress(
            @Param("usersId") String usersId,
            @Param("addressSi") String addressSi,
            @Param("addressDoGun") String addressDoGun,
            @Param("addressGu") String addressGu
    );

    /**
     * Score와 활동 동점 기준을 모두 적용하여 랭킹 목록을 조회합니다.
     *
     * @param rankingType TOTAL 또는 MONTHLY
     * @param periodStart 월간 랭킹 집계 시작 시각. 전체 랭킹은 {@code null}
     * @param pageable 페이지 정보
     * @return 동점 기준이 적용된 랭킹 페이지
     */
    @Query(
            value = "SELECT s.* " + RANKING_METRIC_JOINS + RANKING_ORDER,
            countQuery = "SELECT COUNT(*) FROM stats s WHERE :rankingType IS NOT NULL",
            nativeQuery = true
    )
    Page<Stats> findRankings(
            @Param("rankingType") String rankingType,
            @Param("periodStart") LocalDateTime periodStart,
            Pageable pageable
    );

    /**
     * 목록과 동일한 정렬 기준으로 사용자의 현재 순위를 계산합니다.
     *
     * @param usersId 사용자 식별자
     * @param rankingType TOTAL 또는 MONTHLY
     * @param periodStart 월간 랭킹 집계 시작 시각. 전체 랭킹은 {@code null}
     * @return 1부터 시작하는 순위
     */
    @Query(value = """
            SELECT ranked.ranking_position
            FROM (
                SELECT s.users_id,
                       ROW_NUMBER() OVER (
            """ + RANKING_ORDER + """
                       ) AS ranking_position
            """ + RANKING_METRIC_JOINS + """
            ) ranked
            WHERE ranked.users_id = :usersId
            """, nativeQuery = true)
    Optional<Long> findRankingPosition(
            @Param("usersId") String usersId,
            @Param("rankingType") String rankingType,
            @Param("periodStart") LocalDateTime periodStart
    );

    /**
     * 사용자에게 XP와 Score를 추가합니다.
     *
     * @param usersId 사용자 ID
     * @param xp      추가할 경험치
     * @param score   추가할 점수
     * @return 수정된 행 수
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Stats s
            set s.statsXp = s.statsXp + :xp,
                s.overallScore = s.overallScore + :score,
                s.monthScore = s.monthScore + :score,
                s.overallScoreAchievedAt = case
                    when :score > 0 then :achievedAt
                    else s.overallScoreAchievedAt
                end,
                s.monthScoreAchievedAt = case
                    when :score > 0 then :achievedAt
                    else s.monthScoreAchievedAt
                end
            where s.users.usersId = :usersId
            """)
    int addXpAndScore(
            @Param("usersId") String usersId,
            @Param("xp") int xp,
            @Param("score") int score,
            @Param("achievedAt") LocalDateTime achievedAt
    );

    /**
     * 회수 대상 기간을 반영하여 XP와 누적·월간 Score를 각각 조정합니다.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Stats s
            set s.statsXp = s.statsXp + :xpDelta,
                s.overallScore = s.overallScore + :overallScoreDelta,
                s.monthScore = s.monthScore + :monthScoreDelta,
                s.overallScoreAchievedAt = case
                    when :overallScoreDelta <> 0 then :adjustedAt
                    else s.overallScoreAchievedAt
                end,
                s.monthScoreAchievedAt = case
                    when :monthScoreDelta <> 0 then :adjustedAt
                    else s.monthScoreAchievedAt
                end
            where s.users.usersId = :usersId
            """)
    int adjustRewards(
            @Param("usersId") String usersId,
            @Param("xpDelta") int xpDelta,
            @Param("overallScoreDelta") int overallScoreDelta,
            @Param("monthScoreDelta") int monthScoreDelta,
            @Param("adjustedAt") LocalDateTime adjustedAt
    );

    /**
     * 사용자의 현재 레벨과 티어를 갱신합니다.
     *
     * @param usersId 사용자 식별자
     * @param level   재계산된 레벨
     * @param tier    재계산된 티어
     * @return 수정된 행 수
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Stats s
            set s.statsLevel = :level,
                s.currentTier = :tier
            where s.users.usersId = :usersId
            """)
    int updateGrowth(
            @Param("usersId") String usersId,
            @Param("level") int level,
            @Param("tier") String tier
    );
}
