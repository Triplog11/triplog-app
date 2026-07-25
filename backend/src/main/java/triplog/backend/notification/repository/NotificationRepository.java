package triplog.backend.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import triplog.backend.notification.entity.Notification;
import java.time.LocalDateTime;

/**
 * 알림 엔티티의 저장과 조회를 담당하는 Repository입니다.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 로그인 사용자의 알림 목록을 최신순으로 조회합니다.
     * <p>
     * {@code unreadOnly}가 {@code true}이면 읽지 않은 알림만 조회합니다.
     *
     * @param usersId 알림을 조회할 사용자 식별자
     * @param unreadOnly 읽지 않은 알림만 조회할지 여부
     * @param pageable 페이지 정보
     * @return 조회 조건에 맞는 알림 페이지
     */
    @Query("""
            select n
            from Notification n
            where n.users.usersId = :usersId
              and (:unreadOnly = false or n.read = false)
            order by n.notificationCreatedAt desc, n.notificationId desc
            """)
    Page<Notification> findNotifications(
            @Param("usersId") String usersId,
            @Param("unreadOnly") boolean unreadOnly,
            Pageable pageable
    );

    /**
     * 로그인 사용자가 해당 알림을 소유하고 있는지 확인합니다.
     *
     * @param notificationId 확인할 알림 식별자
     * @param usersId 알림 소유 여부를 확인할 사용자 식별자
     * @return 로그인 사용자가 해당 알림을 소유하면 {@code true}
     */
    boolean existsByNotificationIdAndUsersUsersId(Long notificationId, String usersId);

    /**
     * 로그인 사용자가 소유한 알림을 읽음 상태로 변경하고 읽은 시각을 기록합니다.
     *
     * @param notificationId 읽음 처리할 알림 식별자
     * @param usersId 알림을 소유한 사용자 식별자
     * @param readAt 알림을 읽은 시각
     * @return 수정된 행 수
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Notification n
            set n.read = true,
                n.readAt = :readAt
            where n.notificationId = :notificationId
              and n.users.usersId = :usersId
              and n.read = false
            """)
    int updateRead(
            @Param("notificationId") Long notificationId,
            @Param("usersId") String usersId,
            @Param("readAt") LocalDateTime readAt
    );
}
