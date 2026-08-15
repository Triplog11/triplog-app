package triplog.backend.stats.service;

/**
 * 적용된 단일 활동 정책의 보상 내역입니다.
 *
 * @param policyId   활동 정책 식별자
 * @param description 정책 설명
 * @param xp         지급 XP
 * @param score      지급 Score
 */
public record ActivityRewardInfo(
        String policyId,
        String description,
        int xp,
        int score
) {
}
