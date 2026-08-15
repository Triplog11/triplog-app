package triplog.backend.activitypolicy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 활동별 XP와 Score 지급 기준을 나타내는 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "activity_policy")
public class ActivityPolicy {

    @Id
    @Column(name = "activity_policy_id", length = 36, nullable = false)
    private String activityPolicyId;

    @Column(name = "policy_xp", nullable = false)
    private int policyXp;

    @Column(name = "policy_score", nullable = false)
    private int policyScore;

    @Column(name = "policy_description", length = 2048, nullable = false)
    private String policyDescription;
}
