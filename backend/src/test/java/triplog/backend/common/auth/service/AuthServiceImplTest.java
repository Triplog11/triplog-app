package triplog.backend.common.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import triplog.backend.common.auth.dto.request.AuthRequest.LoginRequest;
import triplog.backend.common.auth.dto.response.AuthResponse.LoginSuccessResponse;
import triplog.backend.common.auth.entity.RefreshToken;
import triplog.backend.common.auth.exception.AuthException;
import triplog.backend.common.auth.repository.RefreshTokenRepository;
import triplog.backend.common.jwt.JwtTokenProvider;
import triplog.backend.common.jwt.JwtTokenRecord;
import triplog.backend.users.entity.Users;
import triplog.backend.users.service.UsersService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static triplog.backend.common.auth.exception.AuthErrorCode.LOCAL_EMAIL_REQUIRED;
import static triplog.backend.common.auth.exception.AuthErrorCode.LOCAL_LOGIN_FAILED;
import static triplog.backend.common.auth.exception.AuthErrorCode.LOCAL_PASSWORD_REQUIRED;
import static triplog.backend.users.entity.LoginType.LOCAL;

/**
 * {@link AuthServiceImpl}의 로그인 처리 흐름을 검증하는 테스트입니다.
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String EMAIL = "local@test.com";
    private static final String RAW_PASSWORD = "password123!";
    private static final String ENCODED_PASSWORD = "$2a$10$encoded-password";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    @Mock
    private UsersService usersService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthServiceImpl authService;

    /**
     * 테스트 대상 서비스를 생성합니다.
     */
    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                List.of(),
                usersService,
                jwtTokenProvider,
                refreshTokenRepository,
                passwordEncoder
        );
    }

    /**
     * 자체 로그인 성공 시 토큰 응답과 Refresh Token 저장을 검증합니다.
     */
    @Test
    @DisplayName("LOCAL 로그인에 성공하면 JWT와 Refresh Token을 발급한다")
    void LOCAL_로그인에_성공하면_JWT와_Refresh_Token을_발급한다() {
        // given
        Users user = new Users(LOCAL, "로컬회원", "https://example.com/profile.png", EMAIL, ENCODED_PASSWORD);
        UUID usersId = UUID.fromString(user.getUsersId());
        LoginRequest request = new LoginRequest(LOCAL, null, null, EMAIL, RAW_PASSWORD);

        given(usersService.findByEmailAndLoginType(EMAIL, LOCAL)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(true);
        given(jwtTokenProvider.createTokenRecord(usersId)).willReturn(new JwtTokenRecord(ACCESS_TOKEN, REFRESH_TOKEN, 3_600_000L));

        // when
        LoginSuccessResponse response = (LoginSuccessResponse) authService.login(request);

        // then
        assertThat(response.getUsersId()).isEqualTo(user.getUsersId());
        assertThat(response.getNickname()).isEqualTo("로컬회원");
        assertThat(response.getAccessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(response.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        log.info("LOCAL 로그인 성공 테스트 완료 - usersId: {}", user.getUsersId());
    }

    /**
     * 자체 로그인 이메일이 비어 있으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("LOCAL 로그인 이메일이 비어 있으면 예외가 발생한다")
    void LOCAL_로그인_이메일이_비어_있으면_예외가_발생한다() {
        // given
        LoginRequest request = new LoginRequest(LOCAL, null, null, " ", RAW_PASSWORD);

        // when
        // then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(LOCAL_EMAIL_REQUIRED);
        verify(usersService, never()).findByEmailAndLoginType(any(), any());
        log.info("LOCAL 로그인 이메일 필수값 검증 테스트 완료");
    }

    /**
     * 자체 로그인 비밀번호가 비어 있으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("LOCAL 로그인 비밀번호가 비어 있으면 예외가 발생한다")
    void LOCAL_로그인_비밀번호가_비어_있으면_예외가_발생한다() {
        // given
        LoginRequest request = new LoginRequest(LOCAL, null, null, EMAIL, " ");

        // when
        // then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(LOCAL_PASSWORD_REQUIRED);
        verify(usersService, never()).findByEmailAndLoginType(any(), any());
        log.info("LOCAL 로그인 비밀번호 필수값 검증 테스트 완료");
    }

    /**
     * 자체 로그인 사용자를 찾을 수 없으면 인증 실패 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("LOCAL 로그인 사용자가 없으면 인증 실패 예외가 발생한다")
    void LOCAL_로그인_사용자가_없으면_인증_실패_예외가_발생한다() {
        // given
        LoginRequest request = new LoginRequest(LOCAL, null, null, EMAIL, RAW_PASSWORD);
        given(usersService.findByEmailAndLoginType(EMAIL, LOCAL)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(LOCAL_LOGIN_FAILED);
        verify(passwordEncoder, never()).matches(any(), any());
        log.info("LOCAL 로그인 사용자 없음 예외 테스트 완료");
    }

    /**
     * 자체 로그인 비밀번호가 일치하지 않으면 인증 실패 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("LOCAL 로그인 비밀번호가 틀리면 인증 실패 예외가 발생한다")
    void LOCAL_로그인_비밀번호가_틀리면_인증_실패_예외가_발생한다() {
        // given
        Users user = new Users(LOCAL, "로컬회원", "https://example.com/profile.png", EMAIL, ENCODED_PASSWORD);
        LoginRequest request = new LoginRequest(LOCAL, null, null, EMAIL, RAW_PASSWORD);

        given(usersService.findByEmailAndLoginType(EMAIL, LOCAL)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(LOCAL_LOGIN_FAILED);
        verify(jwtTokenProvider, never()).createTokenRecord(any());
        verify(refreshTokenRepository, never()).save(any());
        log.info("LOCAL 로그인 비밀번호 불일치 예외 테스트 완료");
    }
}
