package triplog.backend.stats.service;

/**
 * XP·Score 변경 후 재계산된 사용자 성장 정보입니다.
 *
 * @param currentLevel 변경 후 레벨
 * @param currentTier 변경 후 랭크
 * @param levelUp 레벨 상승 여부
 * @param rankUp 랭크 상승 여부
 */
public record GrowthUpdateResult(
        int currentLevel,
        String currentTier,
        boolean levelUp,
        boolean rankUp
) {
}
