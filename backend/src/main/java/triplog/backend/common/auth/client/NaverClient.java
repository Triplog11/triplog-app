package triplog.backend.common.auth.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import triplog.backend.common.auth.dto.response.AuthResponse.NaverTokenResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.NaverUserInfoResponse;
import triplog.backend.common.auth.exception.AuthException;
import triplog.backend.users.entity.LoginType;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.util.StringUtils.hasText;
import static triplog.backend.common.auth.exception.AuthErrorCode.*;

/**
 * Naver OAuth API와 통신하는 Client입니다.
 * <p>
 * 프론트엔드가 Naver 로그인 완료 후 전달받은 인가 코드(Authorization Code)를
 * 백엔드에 넘기면, 이 Client가 해당 코드를 Naver 인증 서버로 전달하여
 * 로그인 처리에 필요한 토큰을 발급받습니다.
 * <p>
 * 현재 소셜 로그인 단계에서는 사용자의 이메일만 로그인 식별 정보로 사용합니다.
 * 닉네임, 프로필 이미지 등 추가 정보는 별도 추가 정보 API에서 처리합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NaverClient implements SocialApiClient {

    private static final String NAVER_TOKEN_URI = "https://nid.naver.com/oauth2.0/token";
    private static final String NAVER_USER_INFO_URI = "https://openapi.naver.com/v1/nid/me";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private final RestClient restClient = RestClient.create();

    @Value("${naver.client_id}")
    private String clientId;

    @Value("${naver.client_secret}")
    private String clientSecret;

    /**
     * Naver 로그인 제공자를 처리할 수 있는지 확인합니다.
     *
     * @param provider 로그인 제공자
     * @return Naver 로그인 제공자 여부
     */
    @Override
    public boolean supports(LoginType provider) {
        return LoginType.NAVER.equals(provider);
    }

    /**
     * Naver 인가 코드로 토큰을 발급받고 사용자 정보 API에서 이메일을 추출합니다.
     *
     * @param code Naver 인가 코드
     * @return Naver 계정 이메일
     */
    @Override
    public String getEmail(String code) {
        return getEmail(code, null);
    }

    /**
     * Naver 인가 코드와 state로 토큰을 발급받고 사용자 정보 API에서 이메일을 추출합니다.
     *
     * @param code Naver 인가 코드
     * @param state Naver OAuth state 값
     * @return Naver 계정 이메일
     */
    public String getEmail(String code, String state) {
        log.info("Naver 로그인 이메일 조회 시작");

        if (!hasText(code)) {
            log.warn("Naver 인가 코드 누락");
            throw new AuthException(AUTHORIZATION_CODE_REQUIRED);
        }

        if (!hasText(state)) {
            log.warn("Naver state 값 누락");
            throw new AuthException(NAVER_STATE_REQUIRED);
        }

        NaverTokenResponse tokenResponse = requestToken(code, state);
        NaverUserInfoResponse userInfoResponse = requestUserInfo(tokenResponse.getAccessToken());
        return extractEmail(userInfoResponse);
    }

    /**
     * Naver 인가 코드로 Naver 토큰 발급 API를 호출합니다.
     *
     * @param code Naver 인가 코드
     * @param state Naver OAuth state 값
     * @return Naver 토큰 응답
     */
    private NaverTokenResponse requestToken(String code, String state) {
        log.debug("Naver 토큰 발급 요청 시작");
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", AUTHORIZATION_CODE);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("state", state);

        NaverTokenResponse tokenResponse = restClient.post()
                .uri(NAVER_TOKEN_URI)
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            throw new AuthException(NAVER_TOKEN_REQUEST_FAILED);
                        })
                .body(NaverTokenResponse.class);

        if (tokenResponse == null || !hasText(tokenResponse.getAccessToken())) {
            log.warn("Naver 토큰 응답 유효성 검증 실패");
            throw new AuthException(NAVER_TOKEN_RESPONSE_INVALID);
        }

        log.debug("Naver 토큰 발급 응답 수신 완료");
        return tokenResponse;
    }

    /**
     * Naver access token으로 사용자 정보 API를 호출합니다.
     *
     * @param accessToken Naver access token
     * @return Naver 사용자 정보 응답
     */
    private NaverUserInfoResponse requestUserInfo(String accessToken) {
        log.debug("Naver 사용자 정보 요청 시작");
        return restClient.get()
                .uri(NAVER_USER_INFO_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            throw new AuthException(NAVER_USER_INFO_REQUEST_FAILED);
                        })
                .body(NaverUserInfoResponse.class);
    }

    /**
     * Naver 사용자 정보 응답에서 이메일을 추출합니다.
     *
     * @param userInfoResponse Naver 사용자 정보 응답
     * @return Naver 계정 이메일
     */
    private String extractEmail(NaverUserInfoResponse userInfoResponse) {
        log.debug("Naver 사용자 정보 이메일 추출 시작");

        if (userInfoResponse == null
                || userInfoResponse.getResponse() == null
                || !hasText(userInfoResponse.getResponse().getEmail())) {
            log.warn("Naver 사용자 정보 이메일 누락");
            throw new AuthException(NAVER_EMAIL_NOT_FOUND);
        }

        log.debug("Naver 사용자 정보 이메일 추출 완료");
        return userInfoResponse.getResponse().getEmail();
    }
}
