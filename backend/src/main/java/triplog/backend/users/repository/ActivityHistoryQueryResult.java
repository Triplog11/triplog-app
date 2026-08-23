package triplog.backend.users.repository;

import java.time.LocalDateTime;

/**
 * 사용자 활동 히스토리 통합 조회 결과입니다.
 *
 * @param activityId 통합 활동 로그 식별자
 * @param activityType 활동 유형
 * @param title 활동 제목
 * @param content 활동 상세 내용
 * @param score 획득 점수
 * @param xp 획득 경험치
 * @param createdAt 활동 발생 일시
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
