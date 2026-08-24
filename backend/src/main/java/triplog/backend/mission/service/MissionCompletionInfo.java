package triplog.backend.mission.service;

import triplog.backend.stats.service.GrowthUpdateResult;

/**
 * 이번 판정에서 최초 완료되어 실제 보상이 지급된 미션 정보입니다.
 *
 * @param missionId 미션 식별자
 * @param missionName 미션명
 * @param missionType WEEKLY
 * @param xp 지급 XP
 * @param score 지급 Score
 * @param growth 보상 지급 후 성장 정보
 */
public record MissionCompletionInfo(
        Long missionId,
        String missionName,
        String missionType,
        int xp,
        int score,
        GrowthUpdateResult growth
) {
}
