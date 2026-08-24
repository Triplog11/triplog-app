package triplog.backend.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import triplog.backend.notification.dto.response.NotificationResponse.ListResponse;
import triplog.backend.notification.dto.response.NotificationResponse.ReadResponse;
import triplog.backend.notification.dto.response.NotificationResponse.SettingsResponse;
import triplog.backend.notification.dto.request.NotificationRequest.SettingsUpdateRequest;
import triplog.backend.notification.entity.Notification;
import triplog.backend.notification.entity.NotificationPolicy;
import triplog.backend.notification.exception.NotificationException;
import triplog.backend.notification.repository.NotificationPolicyRepository;
import triplog.backend.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATION_ALREADY_READ;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATION_NOT_FOUND;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATION_SETTINGS_NOT_FOUND;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATIONS_NOT_FOUND;

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
     * 알림 정책이 모두 존재하면 각 정책의 활성화 여부가 설정 응답에 매핑되는지 검증합니다.
     */
    @Test
    @DisplayName("알림 설정을 조회하면 정책별 활성화 여부가 반환된다")
    void getsNotificationSettings() {
        // given
        when(notificationPolicyRepository.findAllByNotificationTypeIn(anyCollection()))
                .thenReturn(List.of(
                        policy("LEVEL_UP", true),
                        policy("RANK_UP", true),
                        policy("BADGE_ACQUIRED", false),
                        policy("CARD_ACQUIRED", true),
                        policy("REGION_COMPLETED", true),
                        policy("LANDMARK_VERIFIED", false),
                        policy("WEEKLY_MISSION_COMPLETE", true)
                ));

        // when
        SettingsResponse response = notificationService.getSettings();

        // then
        assertThat(response.getIsLevelUp()).isTrue();
        assertThat(response.getIsRankUp()).isTrue();
        assertThat(response.getIsBadgeAcquired()).isFalse();
        assertThat(response.getIsCardAcquired()).isTrue();
        assertThat(response.getIsRegionCompleted()).isTrue();
        assertThat(response.getIsLandmarkVerified()).isFalse();
        assertThat(response.getIsWeeklyMissonCompleted()).isTrue();
        verify(notificationPolicyRepository).findAllByNotificationTypeIn(anyCollection());
    }

    /**
     * 알림 설정에 필요한 정책이 누락되면 알림 설정 없음 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("알림 설정 정책이 누락되면 예외가 발생한다")
    void rejectsMissingNotificationSettings() {
        // given
        when(notificationPolicyRepository.findAllByNotificationTypeIn(anyCollection()))
                .thenReturn(List.of(policy("LEVEL_UP", true)));

        // when
        // then
        assertThatThrownBy(notificationService::getSettings)
                .isInstanceOf(NotificationException.class)
                .extracting("errorCode")
                .isEqualTo(NOTIFICATION_SETTINGS_NOT_FOUND);
    }

    /**
     * 알림 설정 수정 시 모든 정책의 활성화 여부가 변경되고 요청값이 응답되는지 검증합니다.
     */
    @Test
    @DisplayName("알림 설정을 수정하면 정책별 활성화 여부가 변경된다")
    void updatesNotificationSettings() {
        // given
        SettingsUpdateRequest request =
                new SettingsUpdateRequest(true, true, false, true, true, false, true);
        when(notificationPolicyRepository.findAllByNotificationTypeIn(anyCollection()))
                .thenReturn(List.of(
                        policy("LEVEL_UP", false),
                        policy("RANK_UP", false),
                        policy("BADGE_ACQUIRED", true),
                        policy("CARD_ACQUIRED", false),
                        policy("REGION_COMPLETED", false),
                        policy("LANDMARK_VERIFIED", true),
                        policy("WEEKLY_MISSION_COMPLETE", false)
                ));

        // when
        SettingsResponse response = notificationService.updateSettings(request);

        // then
        verify(notificationPolicyRepository).updateActive("LEVEL_UP", true);
        verify(notificationPolicyRepository).updateActive("RANK_UP", true);
        verify(notificationPolicyRepository).updateActive("BADGE_ACQUIRED", false);
        verify(notificationPolicyRepository).updateActive("CARD_ACQUIRED", true);
        verify(notificationPolicyRepository).updateActive("REGION_COMPLETED", true);
        verify(notificationPolicyRepository).updateActive("LANDMARK_VERIFIED", false);
        verify(notificationPolicyRepository).updateActive("WEEKLY_MISSION_COMPLETE", true);
        assertThat(response.getIsLevelUp()).isTrue();
        assertThat(response.getIsRankUp()).isTrue();
        assertThat(response.getIsBadgeAcquired()).isFalse();
        assertThat(response.getIsCardAcquired()).isTrue();
        assertThat(response.getIsRegionCompleted()).isTrue();
        assertThat(response.getIsLandmarkVerified()).isFalse();
        assertThat(response.getIsWeeklyMissonCompleted()).isTrue();
    }

    /**
     * 알림 설정 정책이 누락되면 어떤 정책도 수정하지 않는지 검증합니다.
     */
    @Test
    @DisplayName("알림 설정 정책이 누락되면 수정하지 않고 예외가 발생한다")
    void rejectsUpdateWhenNotificationSettingsAreMissing() {
        // given
        SettingsUpdateRequest request =
                new SettingsUpdateRequest(true, true, false, true, true, false, true);
        when(notificationPolicyRepository.findAllByNotificationTypeIn(anyCollection()))
                .thenReturn(List.of(policy("LEVEL_UP", true)));

        // when
        // then
        assertThatThrownBy(() -> notificationService.updateSettings(request))
                .isInstanceOf(NotificationException.class)
                .extracting("errorCode")
                .isEqualTo(NOTIFICATION_SETTINGS_NOT_FOUND);
        verify(notificationPolicyRepository, never()).updateActive(anyString(), anyBoolean());
    }

    /**
     * 전체 알림 조회 시 페이지 정보와 알림 항목이 응답 DTO로 변환되는지 검증합니다.
     */
    @Test
    @DisplayName("알림 목록을 조회하면 페이지 정보와 알림 항목이 반환된다")
    void getsNotifications() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 25, 10, 30);
        Notification notification = org.mockito.Mockito.mock(Notification.class);
        when(notification.getNotificationId()).thenReturn(NOTIFICATION_ID);
        when(notification.getNotificationTitle()).thenReturn("미션 완료");
        when(notification.getNotificationContent()).thenReturn("일일 미션을 완료했습니다.");
        when(notification.getNotificationType()).thenReturn("MISSION");
        when(notification.isRead()).thenReturn(false);
        when(notification.getNotificationCreatedAt()).thenReturn(createdAt);
        when(notificationRepository.findNotifications(USERS_ID, false, pageable))
                .thenReturn(new PageImpl<>(List.of(notification), pageable, 1));

        // when
        ListResponse response = notificationService.getNotifications(USERS_ID, false, pageable);

        // then
        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getNotifications()).hasSize(1);
        assertThat(response.getNotifications().getFirst().getNotificationId())
                .isEqualTo(NOTIFICATION_ID);
        assertThat(response.getNotifications().getFirst().getCreatedAt()).isEqualTo(createdAt);
        verify(notificationRepository).findNotifications(USERS_ID, false, pageable);
    }

    /**
     * 읽지 않은 알림만 조회하도록 요청하면 해당 조건이 Repository에 전달되는지 검증합니다.
     */
    @Test
    @DisplayName("읽지 않은 알림만 조회하도록 필터링할 수 있다")
    void getsUnreadNotifications() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        when(notificationRepository.findNotifications(USERS_ID, true, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        // when
        ListResponse response = notificationService.getNotifications(USERS_ID, true, pageable);

        // then
        assertThat(response.getNotifications()).isEmpty();
        assertThat(response.getTotalElements()).isZero();
        verify(notificationRepository).findNotifications(USERS_ID, true, pageable);
    }

    /**
     * 요청 페이지가 전체 페이지 범위를 벗어나면 알림 목록 없음 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("요청 페이지가 조회 범위를 벗어나면 예외가 발생한다")
    void rejectsOutOfRangeNotificationPage() {
        // given
        PageRequest pageable = PageRequest.of(2, 10);
        when(notificationRepository.findNotifications(USERS_ID, false, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 14));

        // when
        // then
        assertThatThrownBy(() -> notificationService.getNotifications(USERS_ID, false, pageable))
                .isInstanceOf(NotificationException.class)
                .extracting("errorCode")
                .isEqualTo(NOTIFICATIONS_NOT_FOUND);
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

    /**
     * 테스트에 사용할 알림 정책을 생성합니다.
     *
     * @param notificationType 알림 유형
     * @param active 알림 정책 활성화 여부
     * @return 테스트용 알림 정책
     */
    private NotificationPolicy policy(String notificationType, boolean active) {
        return new NotificationPolicy(
                notificationType,
                notificationType + " 정책",
                notificationType + "_EVENT",
                "알림 제목",
                "알림 내용",
                true,
                active
        );
    }
}
