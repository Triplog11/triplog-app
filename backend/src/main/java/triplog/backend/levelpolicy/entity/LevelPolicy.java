package triplog.backend.levelpolicy.entity;

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
 * 레벨별 달성 조건을 관리하는 레벨 정책 엔티티입니다.
 * <p>
 * 데이터베이스의 {@code level_policy} 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "level_policy")
public class LevelPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_policy_id", nullable = false, unique = true)
    private Long levelPolicyId;

    @Column(name = "level_policy_number", nullable = false)
    private int levelPolicyNumber;

    @Column(name = "level_policy_condition", nullable = false)
    private int levelPolicyCondition;

    /**
     * 레벨 번호와 달성 경험치 조건으로 레벨 정책을 생성합니다.
     *
     * @param levelPolicyNumber 레벨 번호
     * @param levelPolicyCondition 레벨업 필요 경험치
     */
    public LevelPolicy(int levelPolicyNumber, int levelPolicyCondition) {
        this.levelPolicyNumber = levelPolicyNumber;
        this.levelPolicyCondition = levelPolicyCondition;
    }
}
