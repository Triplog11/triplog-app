package triplog.backend.notification.service;

import org.springframework.data.domain.Pageable;
import triplog.backend.notification.dto.response.NotificationResponse.ListResponse;
import triplog.backend.notification.dto.response.NotificationResponse.ReadResponse;

/**
 * 알림 관련 비즈니스 기능을 정의하는 서비스 인터페이스입니다.
 */
public interface NotificationService {

    /**
     * 로그인 사용자의 알림 목록을 페이지 단위로 조회합니다.
     *
     * @param usersId 로그인 사용자 식별자
     * @param unreadOnly 읽지 않은 알림만 조회할지 여부
     * @param pageable 페이지 정보
     * @return 페이징된 알림 목록
     */
    ListResponse getNotifications(String usersId, boolean unreadOnly, Pageable pageable);

    /**
     * 로그인 사용자가 소유한 알림을 읽음 처리합니다.
     *
     * @param usersId 로그인 사용자 식별자
     * @param notificationId 읽음 처리할 알림 식별자
     * @return 알림 읽음 처리 결과
     */
    ReadResponse read(String usersId, Long notificationId);
}
