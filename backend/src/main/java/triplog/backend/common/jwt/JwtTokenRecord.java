package triplog.backend.common.jwt;

/**
 * JWT 토큰 발급 결과를 담는 응답 레코드입니다.
 * <p>
 * Access Token, Refresh Token 및 Access Token 만료 시간을 전달합니다.
 */
public record JwtTokenRecord(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn
) {}
