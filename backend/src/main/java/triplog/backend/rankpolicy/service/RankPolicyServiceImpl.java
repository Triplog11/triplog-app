package triplog.backend.rankpolicy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.rankpolicy.repository.RankPolicyRepository;
import java.util.Optional;

/**
 * 랭크 정책 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankPolicyServiceImpl implements RankPolicyService {

    private final RankPolicyRepository rankPolicyRepository;

    /**
     * 현재 누적 점수보다 높은 조건 중 가장 가까운 다음 랭크 정책을 조회합니다.
     *
     * @param overallScore 사용자의 현재 누적 점수
     * @return 다음 랭크 정책 요약 정보, 최고 티어이면 빈 값
     */
    @Override
    public Optional<RankPolicyInfo> findNextRankPolicy(int overallScore) {
        return rankPolicyRepository
                .findFirstByRankPolicyConditionGreaterThanOrderByRankPolicyConditionAsc(overallScore)
                .map(rankPolicy -> new RankPolicyInfo(
                        rankPolicy.getRankPolicyTier(),
                        rankPolicy.getRankPolicyCondition()
                ));
    }

    /**
     * 누적 Score 이하에서 가장 높은 랭크 정책을 조회합니다.
     *
     * @param overallScore 누적 Score
     * @return 현재 랭크 정책
     */
    @Override
    public RankPolicyInfo findCurrentRankPolicy(int overallScore) {
        return rankPolicyRepository
                .findFirstByRankPolicyConditionLessThanEqualOrderByRankPolicyConditionDesc(overallScore)
                .map(rankPolicy -> new RankPolicyInfo(
                        rankPolicy.getRankPolicyTier(),
                        rankPolicy.getRankPolicyCondition()
                ))
                .orElse(new RankPolicyInfo("BRONZE", 0));
    }
}
