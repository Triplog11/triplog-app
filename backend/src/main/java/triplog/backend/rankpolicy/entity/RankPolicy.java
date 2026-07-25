package triplog.backend.rankpolicy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 티어별 달성 조건을 관리하는 랭크 정책 엔티티입니다.
 * <p>
 * 데이터베이스의 {@code rank_policy} 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "rank_policy")
public class RankPolicy {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rank_policy_id", nullable = false, unique = true)
    private Long rankPolicyId;

    @Column(name = "rank_policy_tier", nullable = false, length = 10)
    private String rankPolicyTier;

    @Column(name = "rank_policy_condition", nullable = false)
    private int rankPolicyCondition;

    /**
     * 티어와 달성 점수 조건으로 랭크 정책을 생성합니다.
     *
     * @param rankPolicyTier 랭크 정책 티어
     * @param rankPolicyCondition 티어 달성 점수 조건
     */
    public RankPolicy(String rankPolicyTier, int rankPolicyCondition) {
        this.rankPolicyTier = rankPolicyTier;
        this.rankPolicyCondition = rankPolicyCondition;
    }
}
