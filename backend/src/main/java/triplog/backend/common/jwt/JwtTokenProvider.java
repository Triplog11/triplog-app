package triplog.backend.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 생성, 검증 및 정보 추출을 담당하는 컴포넌트입니다.
 * <p>
 * Access Token, Refresh Token 및 회원가입용 임시 토큰을 관리합니다.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;
    private static final long TEMPORARY_TOKEN_VALIDITY = 1000 * 60 * 5;

    /**
     * 설정 파일의 JWT 프로퍼티를 주입받아 서명 키와 토큰 유효 시간을 초기화합니다.
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.access-token-validity}") long accessTokenValidity,
                            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    /**
     * 사용자 ID를 기반으로 Access Token과 Refresh Token을 생성해 토큰 응답 레코드로 반환합니다.
     */
    public JwtTokenRecord createTokenRecord(UUID usersId) {
        String accessToken = createToken(usersId, accessTokenValidity);
        String refreshToken = createToken(usersId, refreshTokenValidity);

        return new JwtTokenRecord(accessToken, refreshToken, accessTokenValidity);
    }

    /**
     * 회원가입 진행을 위해 이메일 정보를 담은 임시 토큰을 생성합니다.
     */
    public String createTemporaryToken(String email) {
        return createTemporaryToken(email, null);
    }

    /**
     * 회원가입 진행을 위해 이메일과 로그인 타입을 담은 임시 토큰을 생성합니다.
     */
    public String createTemporaryToken(String email, String loginType) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + TEMPORARY_TOKEN_VALIDITY);

        var builder = Jwts.builder()
                .subject("register-process")
                .claim("type", "REGISTER")
                .claim("email", email)
                .issuedAt(now)
                .expiration(validity);

        if (loginType != null) {
            builder.claim("loginType", loginType);
        }

        return builder.signWith(key).compact();
    }

    /**
     * 임시 토큰 만료 시간을 초 단위로 반환합니다.
     *
     * @return 임시 토큰 만료 시간
     */
    public long getTemporaryTokenExpiresIn() {
        return TEMPORARY_TOKEN_VALIDITY / 1000;
    }

    /**
     * JWT 토큰의 서명, 형식, 만료 시간을 검증합니다.
     */
    public void validateToken(String token) {
        getClaims(token);
    }

    /**
     * Access Token 또는 Refresh Token에서 사용자 ID를 추출합니다.
     */
    public UUID getUsersId(String token) {
        String subject = getClaims(token).getSubject();
        return UUID.fromString(subject);
    }

    /**
     * 회원가입용 임시 토큰에서 이메일을 추출합니다.
     */
    public String getEmailFromTemporaryToken(String temporaryToken) {
        Claims claims = getClaims(temporaryToken);

        if (!"REGISTER".equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("유효하지 않은 회원가입용 임시 토큰입니다.");
        }

        return claims.get("email", String.class);
    }

    /**
     * 회원가입용 임시 토큰에서 로그인 타입을 추출합니다.
     */
    public String getLoginTypeFromTemporaryToken(String temporaryToken) {
        Claims claims = getClaims(temporaryToken);

        if (!"REGISTER".equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("유효하지 않은 회원가입용 임시 토큰입니다.");
        }

        return claims.get("loginType", String.class);
    }

    /**
     * 사용자 ID와 유효 시간을 기반으로 JWT 토큰을 생성합니다.
     */
    private String createToken(UUID usersId, long validity) {
        Date now = new Date();
        Date validityDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .subject(usersId.toString())
                .issuedAt(now)
                .expiration(validityDate)
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰을 파싱해 클레임 정보를 추출합니다.
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
