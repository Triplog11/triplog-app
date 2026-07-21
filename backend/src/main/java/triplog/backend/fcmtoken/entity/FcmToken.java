package triplog.backend.fcmtoken.entity;

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
import triplog.backend.users.entity.Users;

import java.time.LocalDateTime;

/**
 * 사용자 디바이스의 FCM 푸시 토큰 정보를 관리하는 엔티티입니다.
 * <p>
 * 데이터베이스의 {@code fcm_token} 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fcm_token")
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_token_id", nullable = false, unique = true)
    private Long fcmTokenId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    private Users users;

    @Column(name = "token", nullable = false, length = 512)
    private String token;

    @Column(name = "device_type", nullable = false, length = 50)
    private String deviceType;

    @Column(name = "device_name", nullable = false, length = 100)
    private String deviceName;

    @Column(name = "fcm_token_created_at", nullable = false)
    private LocalDateTime fcmTokenCreatedAt;

    /**
     * FCM 푸시 토큰 엔티티를 생성합니다.
     *
     * @param users 토큰을 등록한 사용자
     * @param token 디바이스 FCM 토큰
     * @param deviceType 디바이스 유형
     * @param deviceName 디바이스 이름
     */
    public FcmToken(Users users, String token, String deviceType, String deviceName) {
        this.users = users;
        this.token = token;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
        this.fcmTokenCreatedAt = LocalDateTime.now();
    }
}
