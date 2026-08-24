package triplog.backend.stats.service;

/**
 * 원본 활동에 연결된 보상 회수 결과입니다.
 *
 * @param revokedCount 회수한 보상 이력 수
 * @param revokedXp 회수한 총 XP
 * @param revokedScore 회수한 총 누적 Score
 */
public record RewardRevocationResult(
        int revokedCount,
        int revokedXp,
        int revokedScore
) {
}
