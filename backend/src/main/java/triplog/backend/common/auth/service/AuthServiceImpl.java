package triplog.backend.common.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.common.auth.client.NaverClient;
import triplog.backend.common.auth.client.SocialApiClient;
import triplog.backend.common.auth.dto.request.AuthRequest.AdditionalInfoRequest;
import triplog.backend.common.auth.dto.request.AuthRequest.LoginRequest;
import triplog.backend.common.auth.dto.request.AuthRequest.LogoutRequest;
import triplog.backend.common.auth.dto.request.AuthRequest.SignupRequest;
import triplog.backend.common.auth.dto.response.AuthResponse.LoginResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.LoginSuccessResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.LogoutResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.SignupResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.TemporaryTokenResponse;
import triplog.backend.common.auth.entity.RefreshToken;
import triplog.backend.common.auth.exception.AuthException;
import triplog.backend.common.auth.repository.RefreshTokenRepository;
import triplog.backend.common.jwt.JwtTokenProvider;
import triplog.backend.common.jwt.JwtTokenRecord;
import triplog.backend.stats.service.StatsLoginInfo;
import triplog.backend.stats.service.StatsService;
import triplog.backend.users.entity.LoginType;
import triplog.backend.users.service.UsersAuthInfo;
import triplog.backend.users.service.UsersService;
import triplog.backend.users.service.UsersSignupInfo;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static triplog.backend.common.auth.dto.response.AuthResponse.LoginSuccessResponse.toDto;
import static triplog.backend.common.auth.dto.response.AuthResponse.SignupResponse.toDto;
import static triplog.backend.common.auth.exception.AuthErrorCode.LOGOUT_TOKEN_NOT_FOUND;
import static triplog.backend.common.auth.dto.response.AuthResponse.TemporaryTokenResponse.toDto;
import static triplog.backend.common.auth.exception.AuthErrorCode.LOCAL_EMAIL_REQUIRED;
import static triplog.backend.common.auth.exception.AuthErrorCode.LOCAL_LOGIN_FAILED;
import static triplog.backend.common.auth.exception.AuthErrorCode.LOCAL_PASSWORD_REQUIRED;
import static triplog.backend.common.auth.exception.AuthErrorCode.TEMPORARY_TOKEN_INVALID;
import static triplog.backend.common.auth.exception.AuthErrorCode.UNSUPPORTED_LOGIN_TYPE;
import static triplog.backend.users.entity.LoginType.LOCAL;

/**
 * {@link AuthService}의 구현 클래스입니다.
 * <p>
 * 요청의 로그인 제공자(provider)에 따라 로컬 로그인 또는 소셜 로그인 흐름으로 분기하고,
 * 인증 성공 후 JWT 발급 및 Refresh Token 저장 흐름을 조율합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final List<SocialApiClient> socialApiClients;
    private final UsersService usersService;
    private final StatsService statsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 요청을 provider에 따라 로컬 로그인 또는 소셜 로그인으로 분기해 처리합니다.
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 성공 또는 추가정보 입력용 임시 토큰 응답
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

        return usersService.findAuthInfoByEmailAndLoginType(email, request.getProvider())
                .<LoginResponse>map(user -> {
                    log.info("기존 회원 로그인 처리: provider={}", request.getProvider());
                    return loginExistingUser(user);
                })
                .orElseGet(() -> {
                    log.info("추가정보 입력 필요 사용자 로그인 처리: provider={}", request.getProvider());
                    return createTemporaryToken(email, request.getProvider());
                });
    }

    /**
     * 로컬 회원가입 요청을 처리합니다.
     *
     * @param request 로컬 회원가입 요청 DTO
     * @return 회원가입 완료 여부 응답
     */
    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        UsersSignupInfo users = usersService.createLocalUser(
                request.getEmail(),
                request.getNickname(),
                request.getProfileUrl(),
                passwordEncoder.encode(request.getPassword())
        );

        statsService.createInitialStats(
                users.usersId(),
                request.getAddressSi(),
                request.getAddressDoGun(),
                request.getAddressGu()
        );

        return toDto(true);
    }

    /**
     * 로그아웃 요청을 처리합니다.
     *
     * @param usersId 인증된 사용자 ID
     * @param request 로그아웃 요청 DTO
     * @return 로그아웃 처리 여부 응답
     */
    @Override
    public LogoutResponse logout(String usersId, LogoutRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
                .filter(token -> usersId.equals(token.getUsersId()))
                .orElseThrow(() -> new AuthException(LOGOUT_TOKEN_NOT_FOUND));

        refreshTokenRepository.deleteById(refreshToken.getUsersId());
        return LogoutResponse.toDto(true);
    }

    /**
     * 회원가입용 임시 토큰에서 인증된 이메일과 추가정보를 기반으로 신규 사용자를 생성합니다.
     *
     * @param email 회원가입용 임시 토큰에서 인증된 이메일
     * @param temporaryToken 회원가입용 임시 토큰
     * @param request 추가정보 입력 요청 DTO
     * @return 회원가입 완료 후 로그인 성공 응답
     */
    @Override
    @Transactional
    public LoginSuccessResponse addAdditionalInfo(String email, String temporaryToken, AdditionalInfoRequest request) {
        String tokenEmail = jwtTokenProvider.getEmailFromTemporaryToken(temporaryToken);
        if (!tokenEmail.equals(email)) {
            throw new AuthException(TEMPORARY_TOKEN_INVALID);
        }

        String loginTypeName = jwtTokenProvider.getLoginTypeFromTemporaryToken(temporaryToken);
        LoginType loginType = Arrays.stream(LoginType.values())
                .filter(type -> type.name().equals(loginTypeName))
                .findFirst()
                .orElseThrow(() -> new AuthException(TEMPORARY_TOKEN_INVALID));

        UsersSignupInfo users = usersService.createSocialUser(
                email,
                loginType,
                request.getNickname(),
                request.getProfileUrl()
        );
        StatsLoginInfo stats = statsService.createInitialStats(
                users.usersId(),
                request.getAddressSi(),
                request.getAddressDoGun(),
                request.getAddressGu()
        );

        JwtTokenRecord tokenRecord = jwtTokenProvider.createTokenRecord(UUID.fromString(users.usersId()));
        refreshTokenRepository.save(new RefreshToken(users.usersId(), tokenRecord.refreshToken()));

        return toDto(
                users.nickname(),
                stats.level(),
                stats.xp(),
                stats.tier(),
                tokenRecord.accessToken(),
                tokenRecord.refreshToken()
        );
    }

    /**
     * 로컬 로그인 요청을 처리합니다.
     *
     * @param request 로컬 로그인 요청 DTO
     * @return 로그인 성공 응답 DTO
     */
    private LoginResponse loginLocalUser(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new AuthException(LOCAL_EMAIL_REQUIRED);
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new AuthException(LOCAL_PASSWORD_REQUIRED);
        }

        UsersAuthInfo user = usersService.findAuthInfoByEmailAndLoginType(request.getEmail(), LOCAL)
                .orElseThrow(() -> new AuthException(LOCAL_LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), user.password())) {
            throw new AuthException(LOCAL_LOGIN_FAILED);
        }

        log.info("로컬 회원 로그인 처리");
        return loginExistingUser(user);
    }

    /**
     * 로그인 제공자에 맞는 소셜 로그인 Client를 조회합니다.
     */
    private SocialApiClient getSocialApiClient(LoginType provider) {
        log.info("소셜 로그인 Client 조회: provider={}", provider);
        return socialApiClients.stream()
                .filter(client -> client.supports(provider))
                .findFirst()
                .orElseThrow(() -> new AuthException(UNSUPPORTED_LOGIN_TYPE));
    }

    /**
     * 소셜 로그인 Client를 통해 로그인 계정의 이메일을 조회합니다.
     */
    private String getSocialEmail(SocialApiClient socialApiClient, LoginRequest request) {
        if (socialApiClient instanceof NaverClient naverClient) {
            return naverClient.getEmail(request.getCode(), request.getState());
        }

        return socialApiClient.getEmail(request.getCode());
    }

    /**
     * 기존 회원 로그인 성공 응답을 생성합니다.
     */
    private LoginSuccessResponse loginExistingUser(UsersAuthInfo users) {
        log.info("기존 회원 토큰 발급 및 Refresh Token 저장 시작");
        UUID usersId = UUID.fromString(users.usersId());
        JwtTokenRecord tokenRecord = jwtTokenProvider.createTokenRecord(usersId);
        StatsLoginInfo stats = statsService.getLoginStats(users.usersId());

        refreshTokenRepository.save(new RefreshToken(users.usersId(), tokenRecord.refreshToken()));

        return toDto(
                users.nickname(),
                stats.level(),
                stats.xp(),
                stats.tier(),
                tokenRecord.accessToken(),
                tokenRecord.refreshToken()
        );
    }

    /**
     * 추가정보 입력이 필요한 소셜 신규 사용자의 임시 토큰 응답을 생성합니다.
     */
    private TemporaryTokenResponse createTemporaryToken(String email, LoginType loginType) {
        log.info("추가정보 입력용 임시 토큰 발급 시작");
        return toDto(
                jwtTokenProvider.getTemporaryTokenExpiresIn(),
                jwtTokenProvider.createTemporaryToken(email, loginType.name())
        );
    }
}
