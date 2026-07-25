package triplog.backend.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.notification.dto.response.NotificationResponse.ReadResponse;
import triplog.backend.notification.exception.NotificationException;
import triplog.backend.notification.repository.NotificationPolicyRepository;
import triplog.backend.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATION_ALREADY_READ;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATION_NOT_FOUND;

/**
 * {@link NotificationServiceImpl}의 알림 읽음 처리 흐름을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    private static final String USERS_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final Long NOTIFICATION_ID = 101L;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPolicyRepository notificationPolicyRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(
                notificationRepository,
                notificationPolicyRepository
        );
    }

    /**
     * 로그인 사용자의 알림 수정에 성공하면 읽음 여부와 읽은 시각을 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("알림 읽음 처리에 성공하면 읽음 정보가 반환된다")
    void readsNotification() {
        // given
        when(notificationRepository.updateRead(
                eq(NOTIFICATION_ID),
                eq(USERS_ID),
                any(LocalDateTime.class)
        )).thenReturn(1);

        // when
        ReadResponse response = notificationService.read(USERS_ID, NOTIFICATION_ID);

        // then
        ArgumentCaptor<LocalDateTime> readAtCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(notificationRepository).updateRead(
                eq(NOTIFICATION_ID),
                eq(USERS_ID),
                readAtCaptor.capture()
        );
        assertThat(response.getNotificationId()).isEqualTo(NOTIFICATION_ID);
        assertThat(response.getIsRead()).isTrue();
        assertThat(response.getReadAt()).isEqualTo(readAtCaptor.getValue());
    }

    /**
     * 알림이 없거나 로그인 사용자의 알림이 아니면 알림 없음 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("수정할 알림을 찾을 수 없으면 예외가 발생한다")
    void rejectsMissingNotification() {
        // given
        when(notificationRepository.updateRead(
                eq(NOTIFICATION_ID),
                eq(USERS_ID),
                any(LocalDateTime.class)
        )).thenReturn(0);
        when(notificationRepository.existsByNotificationIdAndUsersUsersId(
                NOTIFICATION_ID,
                USERS_ID
        )).thenReturn(false);

        // when
        // then
        assertThatThrownBy(() -> notificationService.read(USERS_ID, NOTIFICATION_ID))
                .isInstanceOf(NotificationException.class)
                .extracting("errorCode")
                .isEqualTo(NOTIFICATION_NOT_FOUND);
    }

    /**
     * 이미 읽은 알림을 다시 읽음 처리하면 충돌 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("이미 읽은 알림을 다시 읽음 처리하면 예외가 발생한다")
    void rejectsAlreadyReadNotification() {
        // given
        when(notificationRepository.updateRead(
                eq(NOTIFICATION_ID),
                eq(USERS_ID),
                any(LocalDateTime.class)
        )).thenReturn(0);
        when(notificationRepository.existsByNotificationIdAndUsersUsersId(
                NOTIFICATION_ID,
                USERS_ID
        )).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> notificationService.read(USERS_ID, NOTIFICATION_ID))
                .isInstanceOf(NotificationException.class)
                .extracting("errorCode")
                .isEqualTo(NOTIFICATION_ALREADY_READ);
    }
}
