package triplog.backend.common.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.common.auth.client.SocialApiClient;
import triplog.backend.common.auth.dto.request.AuthRequest;
import triplog.backend.common.auth.dto.response.AuthResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.LoginResponse;
import triplog.backend.common.auth.entity.RefreshToken;
import triplog.backend.common.auth.exception.AuthErrorCode;
import triplog.backend.common.auth.exception.AuthException;
import triplog.backend.common.auth.repository.RefreshTokenRepository;
import triplog.backend.common.jwt.JwtTokenProvider;
import triplog.backend.common.jwt.JwtTokenRecord;
import triplog.backend.users.entity.LoginType;
import triplog.backend.users.entity.Users;
import triplog.backend.users.service.UsersService;

import java.util.List;
import java.util.UUID;

import static triplog.backend.common.auth.exception.AuthErrorCode.UNSUPPORTED_LOGIN_TYPE;

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

    /**
     * 구글 로그인 요청을 처리합니다.
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 성공 또는 추가 정보 입력용 임시 토큰 응답
     */
    @Override
    @Transactional
    public AuthResponse.LoginResponse login(AuthRequest.LoginRequest request) {
        log.info("로그인 처리 시작: provider={}", request.getProvider());

        SocialApiClient socialApiClient = getSocialApiClient(request.getProvider());
        String email = socialApiClient.getEmail(request.getCode());

        return usersService.findByEmailAndLoginType(email, request.getProvider())
                .<AuthResponse.LoginResponse>map(user -> {
                    log.info("기존 회원 로그인 처리: provider={}", request.getProvider());
                    return loginExistingUser(user);
                })
                .orElseGet(() -> {
                    log.info("추가 정보 입력 필요 사용자 로그인 처리: provider={}", request.getProvider());
                    return createTemporaryToken(email);
                });
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
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNSUPPORTED_LOGIN_TYPE));
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
                users.getUsersId(),
                users.getNickname(),
                DEFAULT_LEVEL,
                DEFAULT_XP,
                DEFAULT_TIER,
                tokenRecord.accessToken(),
                tokenRecord.refreshToken()
        );
    }

    /**
     * 추가 정보 입력이 필요한 사용자의 임시 토큰 응답을 생성합니다.
     *
     * @param email 사용자 이메일
     * @return 임시 토큰 응답 DTO
     */
    private AuthResponse.TemporaryTokenResponse createTemporaryToken(String email) {
        log.debug("추가 정보 입력용 임시 토큰 발급 시작");
        return AuthResponse.TemporaryTokenResponse.toDto(
                jwtTokenProvider.getTemporaryTokenExpiresIn(),
                jwtTokenProvider.createTemporaryToken(email)
        );
    }
}
