package triplog.backend.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import triplog.backend.users.entity.Users;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 사용자에게 발송된 알림 내용과 읽음 상태를 관리하는 엔티티입니다.
 * <p>
 * 데이터베이스의 {@code notification} 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false, unique = true)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_policy_id", nullable = false)
    private NotificationPolicy notificationPolicy;

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    @Column(name = "notification_title", nullable = false, length = 100)
    private String notificationTitle;

    @Column(name = "notification_content", nullable = false, length = 500)
    private String notificationContent;

    @Column(name = "notification_identifier", nullable = false)
    private Long notificationIdentifier;

    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notification_data", nullable = false, columnDefinition = "json")
    private Map<String, Object> notificationData;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "notification_created_at", nullable = false)
    private LocalDateTime notificationCreatedAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * 사용자와 알림 정책을 기반으로 읽지 않은 새 알림을 생성합니다.
     *
     * @param users 알림을 수신할 사용자
     * @param notificationPolicy 알림 생성에 적용된 정책
     * @param notificationTitle 알림 제목
     * @param notificationContent 알림 내용
     * @param notificationIdentifier 알림과 관련된 엔티티 식별자
     * @param targetType 알림 클릭 시 이동할 대상 유형
     * @param notificationData 알림 처리에 필요한 추가 정보
     */
    public Notification(
            Users users,
            NotificationPolicy notificationPolicy,
            String notificationTitle,
            String notificationContent,
            Long notificationIdentifier,
            String targetType,
            Map<String, Object> notificationData
    ) {
        this.users = users;
        this.notificationPolicy = notificationPolicy;
        this.notificationType = notificationPolicy.getNotificationType();
        this.notificationTitle = notificationTitle;
        this.notificationContent = notificationContent;
        this.notificationIdentifier = notificationIdentifier;
        this.targetType = targetType;
        this.notificationData = notificationData;
        this.read = false;
        this.notificationCreatedAt = LocalDateTime.now();
    }

}
