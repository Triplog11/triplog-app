package triplog.backend.users.repository;

import java.time.LocalDateTime;

/**
 * 사용자 활동 히스토리 통합 조회 결과입니다.
 */
public record ActivityHistoryQueryResult(
        Long activityId,
        String activityType,
        String title,
        String content,
        Integer score,
        Integer xp,
        LocalDateTime createdAt
) {
}
