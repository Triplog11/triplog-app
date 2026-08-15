package triplog.backend.rankpolicy.service;

import java.util.Optional;

/**
 * 랭크 정책 관련 비즈니스 기능을 정의하는 서비스 인터페이스입니다.
 */
public interface RankPolicyService {

    /**
     * 현재 누적 점수보다 높은 조건 중 가장 가까운 다음 랭크 정책을 조회합니다.
     *
     * @param overallScore 사용자의 현재 누적 점수
     * @return 다음 랭크 정책 요약 정보, 최고 티어이면 빈 값
     */
    Optional<RankPolicyInfo> findNextRankPolicy(int overallScore);

    /**
     * 누적 Score에 해당하는 현재 랭크 정책을 조회합니다.
     *
     * @param overallScore 누적 Score
     * @return 현재 랭크 정책
     */
    RankPolicyInfo findCurrentRankPolicy(int overallScore);
}
