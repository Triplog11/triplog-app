package triplog.backend.rankpolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import triplog.backend.rankpolicy.entity.RankPolicy;
import java.util.Optional;

/**
 * 랭크 정책 엔티티의 저장과 조회를 담당하는 Repository입니다.
 */
@Repository
public interface RankPolicyRepository extends JpaRepository<RankPolicy, Long> {

    /**
     * 현재 누적 점수보다 높은 조건 중 가장 가까운 다음 랭크 정책을 조회합니다.
     *
     * @param overallScore 사용자의 현재 누적 점수
     * @return 다음 랭크 정책, 최고 티어이면 빈 값
     */
    Optional<RankPolicy> findFirstByRankPolicyConditionGreaterThanOrderByRankPolicyConditionAsc(
            int overallScore
    );

    /**
     * 기준 Score 이하에서 조건이 가장 높은 랭크 정책을 조회합니다.
     *
     * @param overallScore 누적 Score
     * @return 현재 랭크 정책
     */
    Optional<RankPolicy> findFirstByRankPolicyConditionLessThanEqualOrderByRankPolicyConditionDesc(
            int overallScore
    );
}
