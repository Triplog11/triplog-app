package triplog.backend.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 유형별 트리거와 메시지 템플릿 및 기본 수신 정책을 관리하는 엔티티입니다.
 * <p>
 * 데이터베이스의 {@code notification_policy} 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_policy")
public class NotificationPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_policy_id", nullable = false, unique = true)
    private Long notificationPolicyId;

    @Column(name = "notification_type", nullable = false, unique = true, length = 50)
    private String notificationType;

    @Column(name = "notification_policy_name", nullable = false, length = 100)
    private String notificationPolicyName;

    @Column(name = "trigger_event", nullable = false, length = 50)
    private String triggerEvent;

    @Column(name = "title_template", nullable = false, length = 100)
    private String titleTemplate;

    @Column(name = "content_template", nullable = false, length = 500)
    private String contentTemplate;

    @Column(name = "default_enable", nullable = false)
    private boolean defaultEnable;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    /**
     * 알림 유형에 사용할 정책과 메시지 템플릿을 생성합니다.
     *
     * @param notificationType 알림 유형
     * @param notificationPolicyName 알림 정책 이름
     * @param triggerEvent 알림 생성을 유발하는 이벤트
     * @param titleTemplate 알림 제목 템플릿
     * @param contentTemplate 알림 내용 템플릿
     * @param defaultEnable 사용자 설정이 없을 때 적용할 기본 수신 여부
     * @param active 알림 정책 활성화 여부
     */
    public NotificationPolicy(
            String notificationType,
            String notificationPolicyName,
            String triggerEvent,
            String titleTemplate,
            String contentTemplate,
            boolean defaultEnable,
            boolean active
    ) {
        this.notificationType = notificationType;
        this.notificationPolicyName = notificationPolicyName;
        this.triggerEvent = triggerEvent;
        this.titleTemplate = titleTemplate;
        this.contentTemplate = contentTemplate;
        this.defaultEnable = defaultEnable;
        this.active = active;
    }
}
