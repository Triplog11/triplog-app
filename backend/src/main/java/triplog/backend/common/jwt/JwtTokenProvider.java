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

    private static final long TEMPORARY_TOKEN_VALIDITY = 1000 * 60 * 5;
    private static final String REGISTER_PROCESS_SUBJECT = "register-process";
    private static final String REGISTER_TOKEN_TYPE = "REGISTER";
    private final SecretKey key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

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
     * 사용자 ID를 기반으로 Access Token과 Refresh Token을 생성합니다.
     *
     * @param usersId 사용자 식별자
     * @return Access Token, Refresh Token, Access Token 만료 시간을 담은 레코드
     */
    public JwtTokenRecord createTokenRecord(UUID usersId) {
        String accessToken = createToken(usersId, accessTokenValidity);
        String refreshToken = createToken(usersId, refreshTokenValidity);

        return new JwtTokenRecord(accessToken, refreshToken, accessTokenValidity);
    }

    /**
     * 회원가입 진행을 위해 이메일 정보를 담은 임시 토큰을 생성합니다.
     *
     * @param email 소셜 계정 이메일
     * @return 회원가입용 임시 토큰
     */
    public String createTemporaryToken(String email) {
        return createTemporaryToken(email, null);
    }

    /**
     * 회원가입 진행을 위해 이메일과 로그인 타입을 담은 임시 토큰을 생성합니다.
     *
     * @param email 소셜 계정 이메일
     * @param loginType 로그인 타입
     * @return 회원가입용 임시 토큰
     */
    public String createTemporaryToken(String email, String loginType) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + TEMPORARY_TOKEN_VALIDITY);

        var builder = Jwts.builder()
                .subject(REGISTER_PROCESS_SUBJECT)
                .claim("type", REGISTER_TOKEN_TYPE)
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
     *
     * @param token 검증할 JWT
     */
    public void validateToken(String token) {
        getClaims(token);
    }

    /**
     * 전달된 JWT가 회원가입용 임시 토큰인지 확인합니다.
     *
     * @param token 확인할 JWT
     * @return 회원가입용 임시 토큰이면 {@code true}, 아니면 {@code false}
     */
    public boolean isTemporaryToken(String token) {
        Claims claims = getClaims(token);
        return isTemporaryClaims(claims);
    }

    /**
     * JWT 인증 객체의 principal로 사용할 값을 추출합니다.
     * <p>
     * 회원가입용 임시 토큰은 이메일을 principal로 사용하고,
     * 일반 Access Token과 Refresh Token은 사용자 ID를 principal로 사용합니다.
     *
     * @param token principal을 추출할 JWT
     * @return 인증 객체의 principal 값
     */
    public String getAuthenticationPrincipal(String token) {
        Claims claims = getClaims(token);

        if (isTemporaryClaims(claims)) {
            return claims.get("email", String.class);
        }

        return claims.getSubject();
    }

    /**
     * Access Token 또는 Refresh Token에서 사용자 ID를 추출합니다.
     *
     * @param token 사용자 ID를 추출할 JWT
     * @return 사용자 식별자
     */
    public UUID getUsersId(String token) {
        String subject = getClaims(token).getSubject();
        return UUID.fromString(subject);
    }

    /**
     * 회원가입용 임시 토큰에서 이메일을 추출합니다.
     *
     * @param temporaryToken 회원가입용 임시 토큰
     * @return 임시 토큰에 저장된 이메일
     */
    public String getEmailFromTemporaryToken(String temporaryToken) {
        Claims claims = getClaims(temporaryToken);

        if (!isTemporaryClaims(claims)) {
            throw new IllegalArgumentException("유효하지 않은 회원가입용 임시 토큰입니다.");
        }

        return claims.get("email", String.class);
    }

    /**
     * 회원가입용 임시 토큰에서 로그인 타입을 추출합니다.
     *
     * @param temporaryToken 회원가입용 임시 토큰
     * @return 임시 토큰에 저장된 로그인 타입
     */
    public String getLoginTypeFromTemporaryToken(String temporaryToken) {
        Claims claims = getClaims(temporaryToken);

        if (!isTemporaryClaims(claims)) {
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
     * Claims가 회원가입용 임시 토큰의 Claims인지 확인합니다.
     */
    private boolean isTemporaryClaims(Claims claims) {
        return REGISTER_PROCESS_SUBJECT.equals(claims.getSubject())
                && REGISTER_TOKEN_TYPE.equals(claims.get("type", String.class));
    }

    /**
     * JWT 토큰의 Claims를 추출합니다.
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}