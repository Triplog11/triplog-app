package triplog.backend.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.notification.entity.NotificationPolicy;

/**
 * 알림 정책 엔티티의 저장과 조회를 담당하는 Repository입니다.
 */
public interface NotificationPolicyRepository extends JpaRepository<NotificationPolicy, Long> {
}
