package triplog.backend.notification.service;

import java.util.Map;

/**
 * 알림 정책의 트리거와 템플릿 치환값을 전달하는 이벤트입니다.
 *
 * @param triggerEvent 알림 정책에 정의된 트리거 이벤트
 * @param identifier 알림과 연결할 엔티티 식별자
 * @param targetType 알림 클릭 시 이동할 대상 유형
 * @param data 템플릿 치환 및 응답에 사용할 추가 정보
 */
public record NotificationEvent(
        String triggerEvent,
        Long identifier,
        String targetType,
        Map<String, Object> data
) {
}
