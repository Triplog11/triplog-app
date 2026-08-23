package triplog.backend.users.service;

import java.time.LocalDateTime;

/**
 * 통합 활동 히스토리에 저장할 활동 정보입니다.
 */
public record ActivityHistoryRecord(
        String usersId,
        String activityType,
        String sourceType,
        String sourceId,
        String eventKey,
        String title,
        String content,
        int xp,
        int score,
        int displayOrder,
        LocalDateTime createdAt
) {
}
