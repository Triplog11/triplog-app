package triplog.backend.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.notification.repository.NotificationPolicyRepository;
import triplog.backend.notification.repository.NotificationRepository;

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
}
