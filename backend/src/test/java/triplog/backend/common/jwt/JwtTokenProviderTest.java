package triplog.backend.common.jwt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JwtTokenProvider의 JWT 생성, 검증, 정보 추출 기능을 검증하는 테스트입니다.
 */
@Slf4j
class JwtTokenProviderTest {

    private static final String SECRET = "SvEsvWx8nCvtvFmWsLgywF4d/SysLfI+BMoFEPCirx/P7VCnZJhC6Yr5D50agoTnZriLq6QhA55VGZcsJ7k52g==";
    private static final long ACCESS_TOKEN_VALIDITY = 10_800_000L;
    private static final long REFRESH_TOKEN_VALIDITY = 1_209_600_000L;

    /**
     * 사용자 ID로 Access Token과 Refresh Token을 생성하는지 검증합니다.
     */
    @Test
    @DisplayName("사용자 ID로 토큰 레코드를 생성한다")
    void 사용자_ID로_토큰_레코드를_생성한다() {
        // given
        JwtTokenProvider jwtTokenProvider = createJwtTokenProvider();
        UUID usersId = UUID.randomUUID();

        // when
        JwtTokenRecord tokenRecord = jwtTokenProvider.createTokenRecord(usersId);

        // then
        assertThat(tokenRecord.accessToken()).isNotBlank();
        assertThat(tokenRecord.refreshToken()).isNotBlank();
        assertThat(tokenRecord.accessTokenExpiresIn()).isEqualTo(ACCESS_TOKEN_VALIDITY);
        log.info("토큰 레코드 생성 성공 - usersId: {}, accessToken 만료시간: {}", usersId, tokenRecord.accessTokenExpiresIn());
    }

    /**
     * Access Token에서 사용자 ID를 추출하는지 검증합니다.
     */
    @Test
    @DisplayName("Access Token에서 usersId를 추출한다")
    void Access_Token에서_usersId를_추출한다() {
        // given
        JwtTokenProvider jwtTokenProvider = createJwtTokenProvider();
        UUID usersId = UUID.randomUUID();
        JwtTokenRecord tokenRecord = jwtTokenProvider.createTokenRecord(usersId);

        // when
        UUID extractedUsersId = jwtTokenProvider.getUsersId(tokenRecord.accessToken());

        // then
        assertThat(extractedUsersId).isEqualTo(usersId);
        log.info("usersId 추출 성공 - 예상 usersId: {}, 추출 usersId: {}", usersId, extractedUsersId);
    }

    /**
     * 회원가입용 임시 토큰에서 이메일을 추출하는지 검증합니다.
     */
    @Test
    @DisplayName("temporaryToken에서 이메일을 추출한다")
    void temporaryToken에서_이메일을_추출한다() {
        // given
        JwtTokenProvider jwtTokenProvider = createJwtTokenProvider();
        String email = "test@example.com";
        String temporaryToken = jwtTokenProvider.createTemporaryToken(email);

        // when
        String extractedEmail = jwtTokenProvider.getEmailFromTemporaryToken(temporaryToken);

        // then
        assertThat(extractedEmail).isEqualTo(email);
        log.info("temporaryToken 이메일 추출 성공 - 예상 이메일: {}, 추출 이메일: {}", email, extractedEmail);
    }

    /**
     * 일반 Access Token을 회원가입용 임시 토큰으로 사용할 수 없는지 검증합니다.
     */
    @Test
    @DisplayName("Access Token으로 temporaryToken 이메일을 추출하면 실패한다")
    void Access_Token으로_temporaryToken_이메일을_추출하면_실패한다() {
        // given
        JwtTokenProvider jwtTokenProvider = createJwtTokenProvider();
        UUID usersId = UUID.randomUUID();
        String accessToken = jwtTokenProvider.createTokenRecord(usersId).accessToken();

        // when
        // then
        assertThatThrownBy(() -> jwtTokenProvider.getEmailFromTemporaryToken(accessToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 회원가입용 임시 토큰입니다.");
        log.info("temporaryToken 검증 실패 확인 - 이유: Access Token에는 REGISTER 타입이 없습니다.");
    }

    /**
     * 테스트용 JwtTokenProvider를 생성합니다.
     */
    private JwtTokenProvider createJwtTokenProvider() {
        return new JwtTokenProvider(SECRET, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY);
    }
}
