package triplog.backend.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.notification.entity.NotificationPolicy;
import java.util.Collection;
import java.util.List;

/**
 * 알림 정책 엔티티의 저장과 조회를 담당하는 Repository입니다.
 */
public interface NotificationPolicyRepository extends JpaRepository<NotificationPolicy, Long> {

    /**
     * 전달받은 알림 유형에 해당하는 정책 목록을 조회합니다.
     *
     * @param notificationTypes 조회할 알림 유형 목록
     * @return 알림 유형에 해당하는 정책 목록
     */
    List<NotificationPolicy> findAllByNotificationTypeIn(Collection<String> notificationTypes);
}
