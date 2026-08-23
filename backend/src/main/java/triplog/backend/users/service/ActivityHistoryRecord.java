package triplog.backend.users.service;

import java.time.LocalDateTime;

/**
 * 통합 활동 히스토리에 저장할 활동 정보입니다.
 *
 * @param usersId 사용자 식별자
 * @param activityType 화면에 표시할 활동 유형
 * @param sourceType 활동을 발생시킨 원본 유형
 * @param sourceId 원본 데이터 식별자
 * @param eventKey 사용자별 중복 기록을 방지하는 멱등성 키
 * @param title 활동 제목
 * @param content 활동 상세 내용
 * @param xp 활동으로 획득한 경험치
 * @param score 활동으로 획득한 점수
 * @param displayOrder 동일 이벤트에서의 표시 순서
 * @param createdAt 활동 발생 일시
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
