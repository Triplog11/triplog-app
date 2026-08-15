package triplog.backend.stats.service;

import java.util.List;

/**
 * 활동 정책 적용 후의 전체 보상 및 성장 결과입니다.
 *
 * @param rewards      정책별 보상 내역
 * @param totalXp      총 지급 XP
 * @param totalScore   총 지급 Score
 * @param currentLevel 지급 후 현재 레벨
 * @param currentTier  지급 후 현재 티어
 * @param levelUp      레벨 상승 여부
 * @param rankUp       랭크 상승 여부
 */
public record ActivityRewardResult(
        List<ActivityRewardInfo> rewards,
        int totalXp,
        int totalScore,
        int currentLevel,
        String currentTier,
        boolean levelUp,
        boolean rankUp
) {
}
