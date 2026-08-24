package triplog.backend.stats.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.stats.entity.UsersRewardLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 보상 지급을 원자적으로 기록하고 회수 대상 이력을 잠금 조회합니다.
 */
public interface UsersRewardLogRepository extends JpaRepository<UsersRewardLog, Long> {

    /**
     * 사용자별 이벤트 키가 처음 등록되는 경우에만 지급 이력을 저장합니다.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            INSERT IGNORE INTO users_reward_log (
                users_id,
                policy_id,
                event_key,
                request_key,
                source_type,
                source_id,
                reward_xp,
                reward_score,
                reward_status,
                awarded_at
            ) VALUES (
                :usersId,
                :policyId,
                :eventKey,
                :requestKey,
                :sourceType,
                :sourceId,
                :rewardXp,
                :rewardScore,
                'GRANTED',
                :awardedAt
            )
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("usersId") String usersId,
            @Param("policyId") String policyId,
            @Param("eventKey") String eventKey,
            @Param("requestKey") String requestKey,
            @Param("sourceType") String sourceType,
            @Param("sourceId") String sourceId,
            @Param("rewardXp") int rewardXp,
            @Param("rewardScore") int rewardScore,
            @Param("awardedAt") LocalDateTime awardedAt
    );

    /**
     * 원본 활동에서 아직 회수하지 않은 보상 이력을 잠금 조회합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select reward
            from UsersRewardLog reward
            where reward.usersId = :usersId
              and reward.sourceType = :sourceType
              and reward.sourceId = :sourceId
              and reward.rewardStatus = 'GRANTED'
            order by reward.usersRewardLogId asc
            """)
    List<UsersRewardLog> findGrantedBySourceForUpdate(
            @Param("usersId") String usersId,
            @Param("sourceType") String sourceType,
            @Param("sourceId") String sourceId
    );
}
