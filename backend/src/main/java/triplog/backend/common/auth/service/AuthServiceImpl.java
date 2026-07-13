package triplog.backend.common.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.common.auth.client.NaverClient;
import triplog.backend.common.auth.client.SocialApiClient;
import triplog.backend.common.auth.dto.request.AuthRequest.LoginRequest;
import triplog.backend.common.auth.dto.response.AuthResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.LoginResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.TemporaryTokenResponse;
import triplog.backend.common.auth.entity.RefreshToken;
import triplog.backend.common.auth.exception.AuthException;
import triplog.backend.common.auth.repository.RefreshTokenRepository;
import triplog.backend.common.jwt.JwtTokenProvider;
import triplog.backend.common.jwt.JwtTokenRecord;
import triplog.backend.users.entity.LoginType;
import triplog.backend.users.entity.Users;
import triplog.backend.users.service.UsersService;

import java.util.List;
import java.util.UUID;

import static triplog.backend.common.auth.exception.AuthErrorCode.LOCAL_EMAIL_REQUIRED;
import static triplog.backend.common.auth.exception.AuthErrorCode.LOCAL_LOGIN_FAILED;
import static triplog.backend.common.auth.exception.AuthErrorCode.LOCAL_PASSWORD_REQUIRED;
import static triplog.backend.common.auth.exception.AuthErrorCode.UNSUPPORTED_LOGIN_TYPE;
import static triplog.backend.users.entity.LoginType.LOCAL;

/**
 * {@link AuthService}의 구현 클래스입니다.
 * <p>
 * 요청의 로그인 제공자(provider)에 따라 자체 로그인 또는 소셜 로그인 흐름으로 분기하고,
 * 인증 성공 후 JWT 발급 및 Refresh Token 저장 흐름을 조율합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final int DEFAULT_LEVEL = 1;
    private static final int DEFAULT_XP = 0;
    private static final String DEFAULT_TIER = "BRONZE";

    private final List<SocialApiClient> socialApiClients;
    private final UsersService usersService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 요청을 provider에 따라 자체 로그인 또는 소셜 로그인으로 분기해 처리합니다.
     * <p>
     * 자체 로그인은 이메일과 비밀번호를 검증해 기존 회원을 로그인시키고,
     * 소셜 로그인은 제공자별 OAuth 인가 코드로 이메일을 조회합니다.
     * 기존 회원이면 JWT와 Refresh Token을 발급하고, 소셜 신규 사용자이면 추가 정보 입력용 임시 토큰을 반환합니다.
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 성공 또는 추가 정보 입력용 임시 토큰 응답
     */
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("로그인 처리 시작: provider={}", request.getProvider());

        if (LOCAL.equals(request.getProvider())) {
            return loginLocalUser(request);
        }

        SocialApiClient socialApiClient = getSocialApiClient(request.getProvider());
        String email = getSocialEmail(socialApiClient, request);

        return usersService.findByEmailAndLoginType(email, request.getProvider())
                .<LoginResponse>map(user -> {
                    log.info("기존 회원 로그인 처리: provider={}", request.getProvider());
                    return loginExistingUser(user);
                })
                .orElseGet(() -> {
                    log.info("추가 정보 입력 필요 사용자 로그인 처리: provider={}", request.getProvider());
                    return createTemporaryToken(email, request.getProvider());
                });
    }

    /**
     * 자체 로그인 요청을 처리합니다.
     * <p>
     * 이메일과 비밀번호 필수값을 검증한 뒤, 이메일과 로그인 타입으로 사용자를 조회합니다.
     * 조회된 사용자의 저장 비밀번호 해시와 입력 비밀번호를 {@link PasswordEncoder}로 비교하고,
     * 검증에 성공하면 JWT와 Refresh Token을 발급합니다.
     *
     * @param request 자체 로그인 요청 DTO
     * @return 로그인 성공 응답 DTO
     */
    private LoginResponse loginLocalUser(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new AuthException(LOCAL_EMAIL_REQUIRED);
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new AuthException(LOCAL_PASSWORD_REQUIRED);
        }

        Users user = usersService.findByEmailAndLoginType(request.getEmail(), LOCAL)
                .orElseThrow(() -> new AuthException(LOCAL_LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException(LOCAL_LOGIN_FAILED);
        }

        log.info("자체 회원 로그인 처리");
        return loginExistingUser(user);
    }

    /**
     * 로그인 제공자에 맞는 소셜 로그인 Client를 조회합니다.
     *
     * @param provider 로그인 제공자
     * @return 소셜 로그인 Client
     */
    private SocialApiClient getSocialApiClient(LoginType provider) {
        log.debug("소셜 로그인 Client 조회: provider={}", provider);
        return socialApiClients.stream()
                .filter(client -> client.supports(provider))
                .findFirst()
                .orElseThrow(() -> new AuthException(UNSUPPORTED_LOGIN_TYPE));
    }

    /**
     * 소셜 로그인 Client를 통해 로그인 식별용 이메일을 조회합니다.
     *
     * @param socialApiClient 소셜 로그인 Client
     * @param request 로그인 요청 DTO
     * @return 소셜 계정 이메일
     */
    private String getSocialEmail(SocialApiClient socialApiClient, LoginRequest request) {
        if (socialApiClient instanceof NaverClient naverClient) {
            return naverClient.getEmail(request.getCode(), request.getState());
        }

        return socialApiClient.getEmail(request.getCode());
    }

    /**
     * 기존 회원 로그인 성공 응답을 생성합니다.
     *
     * @param users 사용자 엔티티
     * @return 로그인 성공 응답 DTO
     */
    private AuthResponse.LoginSuccessResponse loginExistingUser(Users users) {
        log.debug("기존 회원 토큰 발급 및 Refresh Token 저장 시작");
        UUID usersId = UUID.fromString(users.getUsersId());
        JwtTokenRecord tokenRecord = jwtTokenProvider.createTokenRecord(usersId);

        refreshTokenRepository.save(new RefreshToken(users.getUsersId(), tokenRecord.refreshToken()));

        return AuthResponse.LoginSuccessResponse.toDto(
                users.getNickname(),
                DEFAULT_LEVEL,
                DEFAULT_XP,
                DEFAULT_TIER,
                tokenRecord.accessToken(),
                tokenRecord.refreshToken()
        );
    }

    /**
     * 추가 정보 입력이 필요한 소셜 신규 사용자의 임시 토큰 응답을 생성합니다.
     *
     * @param email 소셜 계정 이메일
     * @return 임시 토큰 응답 DTO
     */
    private TemporaryTokenResponse createTemporaryToken(String email, LoginType loginType) {
        log.debug("추가 정보 입력용 임시 토큰 발급 시작");
        return TemporaryTokenResponse.toDto(
                jwtTokenProvider.getTemporaryTokenExpiresIn(),
                jwtTokenProvider.createTemporaryToken(email, loginType.name())
        );
    }
}
