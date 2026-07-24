package triplog.backend.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.notification.dto.response.NotificationResponse.ListResponse;
import triplog.backend.notification.dto.response.NotificationResponse.ReadResponse;
import triplog.backend.notification.entity.Notification;
import triplog.backend.notification.exception.NotificationException;
import triplog.backend.notification.repository.NotificationPolicyRepository;
import triplog.backend.notification.repository.NotificationRepository;

import java.time.LocalDateTime;

import static triplog.backend.notification.dto.response.NotificationResponse.ReadResponse.toDto;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATION_ALREADY_READ;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATION_NOT_FOUND;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATIONS_NOT_FOUND;

/**
 * 알림 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    /**
     * 알림 저장과 조회를 담당하는 Repository입니다.
     */
    private final NotificationRepository notificationRepository;

    /**
     * 알림 정책 저장과 조회를 담당하는 Repository입니다.
     */
    private final NotificationPolicyRepository notificationPolicyRepository;

    /**
     * 로그인 사용자의 알림 목록을 조회 조건과 페이지 정보에 따라 반환합니다.
     *
     * @param usersId 로그인 사용자 식별자
     * @param unreadOnly 읽지 않은 알림만 조회할지 여부
     * @param pageable 페이지 정보
     * @return 페이징된 알림 목록
     * @throws NotificationException 요청 페이지가 조회 범위를 벗어난 경우
     */
    @Override
    public ListResponse getNotifications(String usersId, boolean unreadOnly, Pageable pageable) {
        Page<Notification> result =
                notificationRepository.findNotifications(usersId, unreadOnly, pageable);

        if (pageable.getPageNumber() > 0 && pageable.getPageNumber() >= result.getTotalPages()) {
            throw new NotificationException(NOTIFICATIONS_NOT_FOUND);
        }

        return ListResponse.toDto(result);
    }

    /**
     * 로그인 사용자가 소유한 알림을 읽음 처리하고 읽은 시각을 반환합니다.
     *
     * @param usersId 로그인 사용자 식별자
     * @param notificationId 읽음 처리할 알림 식별자
     * @return 알림 읽음 처리 결과
     * @throws NotificationException 알림이 존재하지 않거나 로그인 사용자의 알림이 아닌 경우
     */
    @Override
    @Transactional
    public ReadResponse read(String usersId, Long notificationId) {
        LocalDateTime readAt = LocalDateTime.now();
        int updatedCount = notificationRepository.updateRead(notificationId, usersId, readAt);

        if (updatedCount == 0) {
            boolean notificationExists =
                    notificationRepository.existsByNotificationIdAndUsersUsersId(notificationId, usersId);
            throw new NotificationException(
                    notificationExists ? NOTIFICATION_ALREADY_READ : NOTIFICATION_NOT_FOUND
            );
        }

        return toDto(notificationId, readAt);
    }
}
