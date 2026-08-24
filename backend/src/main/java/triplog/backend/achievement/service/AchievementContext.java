package triplog.backend.achievement.service;

import java.util.Map;

/**
 * 뱃지와 칭호 조건 판정에 사용할 사용자 활동 지표입니다.
 *
 * @param metrics 성취 조건 target별 현재 누적값
 */
public record AchievementContext(Map<String, Long> metrics) {

    public AchievementContext {
        metrics = Map.copyOf(metrics);
    }

    /** 조건 target에 대응하는 현재 누적값을 조회합니다. */
    public long metric(String target) {
        return metrics.getOrDefault(target, 0L);
    }
}
