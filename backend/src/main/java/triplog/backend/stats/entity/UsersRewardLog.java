package triplog.backend.stats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자에게 실제로 지급하거나 회수한 XP와 Score 이력을 관리합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users_reward_log")
public class UsersRewardLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_reward_log_id", nullable = false)
    private Long usersRewardLogId;

    @Column(name = "users_id", nullable = false, length = 36)
    private String usersId;

    @Column(name = "policy_id", nullable = false, length = 36)
    private String policyId;

    @Column(name = "event_key", nullable = false, length = 255)
    private String eventKey;

    @Column(name = "request_key", length = 100)
    private String requestKey;

    @Column(name = "source_type", nullable = false, length = 50)
    private String sourceType;

    @Column(name = "source_id", nullable = false, length = 100)
    private String sourceId;

    @Column(name = "reward_xp", nullable = false)
    private int rewardXp;

    @Column(name = "reward_score", nullable = false)
    private int rewardScore;

    @Column(name = "reward_status", nullable = false, length = 20)
    private String rewardStatus;

    @Column(name = "awarded_at", nullable = false)
    private LocalDateTime awardedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revocation_reason", length = 500)
    private String revocationReason;

    /**
     * 지급 상태의 보상을 회수 상태로 변경합니다.
     *
     * @param reason 회수 사유
     * @param revokedAt 회수 시각
     */
    public void revoke(String reason, LocalDateTime revokedAt) {
        this.rewardStatus = "REVOKED";
        this.revocationReason = reason;
        this.revokedAt = revokedAt;
    }
}
