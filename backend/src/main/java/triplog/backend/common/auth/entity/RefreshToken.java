package triplog.backend.common.auth.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

/**
 * Refresh Token 정보를 Redis에 저장하기 위한 엔티티입니다.
 * <p>
 * 사용자의 식별자를 Key로 사용하고, 토큰 재발급 및 로그아웃 처리 시 저장된
 * Refresh Token을 조회하는 용도로 사용합니다.
 */
@Getter
@RedisHash(value = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private String usersId;

    @Indexed
    private String refreshToken;

    /**
     * Refresh Token 엔티티를 생성합니다.
     *
     * @param usersId 사용자 식별자
     * @param refreshToken Refresh Token
     */
    public RefreshToken(String usersId, String refreshToken) {
        this.usersId = usersId;
        this.refreshToken = refreshToken;
    }
}
