package triplog.backend.stats.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 적용된 단일 활동 정책의 보상 내역입니다.
 *
 * @param eventKey   보상 원장의 중복 방지 키
 * @param policyId   활동 정책 식별자
 * @param description 정책 설명
 * @param xp         지급 XP
 * @param score      지급 Score
 */
public record ActivityRewardInfo(
        @JsonIgnore String eventKey,
        String policyId,
        String description,
        int xp,
        int score
) {
}
