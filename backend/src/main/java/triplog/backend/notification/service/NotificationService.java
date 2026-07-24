package triplog.backend.notification.service;

import triplog.backend.notification.dto.response.NotificationResponse.ReadResponse;

/**
 * 알림 관련 비즈니스 기능을 정의하는 서비스 인터페이스입니다.
 */
public interface NotificationService {

    /**
     * 로그인 사용자가 소유한 알림을 읽음 처리합니다.
     *
     * @param usersId 로그인 사용자 식별자
     * @param notificationId 읽음 처리할 알림 식별자
     * @return 알림 읽음 처리 결과
     */
    ReadResponse read(String usersId, Long notificationId);
}
