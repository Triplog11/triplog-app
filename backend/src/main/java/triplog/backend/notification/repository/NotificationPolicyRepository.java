package triplog.backend.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.notification.entity.NotificationPolicy;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 알림 정책 엔티티의 저장과 조회를 담당하는 Repository입니다.
 */
public interface NotificationPolicyRepository extends JpaRepository<NotificationPolicy, Long> {

    /**
     * 트리거 이벤트에 대응하는 활성 알림 정책을 조회합니다.
     *
     * @param triggerEvent 발생한 트리거 이벤트
     * @return 활성화된 알림 정책
     */
    Optional<NotificationPolicy> findByTriggerEventAndActiveTrue(String triggerEvent);

    /**
     * 전달받은 알림 유형에 해당하는 정책 목록을 조회합니다.
     *
     * @param notificationTypes 조회할 알림 유형 목록
     * @return 알림 유형에 해당하는 정책 목록
     */
    List<NotificationPolicy> findAllByNotificationTypeIn(Collection<String> notificationTypes);

    /**
     * 알림 유형에 해당하는 정책의 활성화 여부를 수정합니다.
     *
     * @param notificationType 수정할 알림 유형
     * @param active 변경할 활성화 여부
     * @return 수정된 행 수
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update NotificationPolicy np
            set np.active = :active
            where np.notificationType = :notificationType
            """)
    int updateActive(
            @Param("notificationType") String notificationType,
            @Param("active") boolean active
    );
}
