package triplog.backend.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Set;
import static triplog.backend.notification.dto.response.NotificationResponse.ReadResponse.toDto;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATION_ALREADY_READ;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATION_NOT_FOUND;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATION_SETTINGS_NOT_FOUND;
import static triplog.backend.notification.exception.NotificationErrorCode.NOTIFICATIONS_NOT_FOUND;

/**
 * 알림 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    /**
     * 알림 설정 조회에 사용하는 알림 정책 유형입니다.
     */
    private static final Set<String> SETTING_NOTIFICATION_TYPES = Set.of(
            "LEVEL_UP",
            "RANK_UP",
            "BADGE_ACQUIRED",
            "CARD_ACQUIRED",
            "REGION_COMPLETED",
            "LANDMARK_VERIFIED",
            "WEEKLY_MISSION_COMPLETE"
    );

    /**
     * 알림 저장과 조회를 담당하는 Repository입니다.
     */
    private final NotificationRepository notificationRepository;

    /**
     * 알림 정책 저장과 조회를 담당하는 Repository입니다.
     */
    private final NotificationPolicyRepository notificationPolicyRepository;

    /**
     * 알림 정책별 활성화 상태를 조회하여 알림 설정 응답으로 반환합니다.
     *
     * @return 알림 설정 조회 응답
     * @throws NotificationException 필요한 알림 정책 정보를 찾을 수 없는 경우
     */
    @Override
    public SettingsResponse getSettings() {
        List<NotificationPolicy> policies =
                notificationPolicyRepository.findAllByNotificationTypeIn(SETTING_NOTIFICATION_TYPES);

        if (policies.size() != SETTING_NOTIFICATION_TYPES.size()) {
            throw new NotificationException(NOTIFICATION_SETTINGS_NOT_FOUND);
        }

        return SettingsResponse.toDto(policies);
    }

    /**
     * 알림 정책별 활성화 상태를 수정하고 수정된 설정을 반환합니다.
     *
     * @param request 알림 설정 수정 요청
     * @return 수정된 알림 설정
     * @throws NotificationException 필요한 알림 정책 정보를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public SettingsResponse updateSettings(SettingsUpdateRequest request) {
        List<NotificationPolicy> policies =
                notificationPolicyRepository.findAllByNotificationTypeIn(SETTING_NOTIFICATION_TYPES);

        if (policies.size() != SETTING_NOTIFICATION_TYPES.size()) {
            throw new NotificationException(NOTIFICATION_SETTINGS_NOT_FOUND);
        }

        notificationPolicyRepository.updateActive("LEVEL_UP", request.getIsLevelUp());
        notificationPolicyRepository.updateActive("RANK_UP", request.getIsRankUp());
        notificationPolicyRepository.updateActive("BADGE_ACQUIRED", request.getIsBadgeAcquired());
        notificationPolicyRepository.updateActive("CARD_ACQUIRED", request.getIsCardAcquired());
        notificationPolicyRepository.updateActive("REGION_COMPLETED", request.getIsRegionCompleted());
        notificationPolicyRepository.updateActive("LANDMARK_VERIFIED", request.getIsLandmarkVerified());
        notificationPolicyRepository.updateActive(
                "WEEKLY_MISSION_COMPLETE",
                request.getIsWeeklyMissonCompleted()
        );

        return SettingsResponse.toDto(request);
    }

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
