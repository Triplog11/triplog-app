package triplog.backend.users.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * 사용자(Users) 정보를 관리하는 엔티티 클래스입니다.
 * <p>
 * 데이터베이스의 {@code users} 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class Users {

    @Id
    @Column(name = "users_id", nullable = false, unique = true, length = 36)
    private String usersId = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    @Column(name = "nickname", nullable = false, unique = true, length = 12)
    private String nickname;

    @Column(name = "profile_url", nullable = false, length = 2048)
    private String profileUrl;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    public Users(LoginType loginType, String nickname, String profileUrl, String email, String password) {
        this.loginType = loginType;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.email = email;
        this.password = password;
    }
}
