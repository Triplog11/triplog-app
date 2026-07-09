package triplog.backend.common.auth.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import triplog.backend.common.auth.dto.response.AuthResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.GoogleTokenResponse;
import triplog.backend.common.auth.exception.AuthErrorCode;
import triplog.backend.common.auth.exception.AuthException;
import triplog.backend.users.entity.LoginType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static java.util.Base64.getUrlDecoder;
import static org.springframework.boot.json.JsonParserFactory.getJsonParser;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.util.StringUtils.hasText;
import static triplog.backend.common.auth.exception.AuthErrorCode.*;

/**
 * Google OAuth API와 통신하는 Client입니다.
 * <p>
 * 프론트엔드가 Google 로그인 완료 후 전달받은 인가 코드(Authorization Code)를
 * 백엔드에 넘기면, 이 Client가 해당 코드를 Google 인증 서버로 전달하여
 * 로그인 처리에 필요한 토큰을 발급받습니다.
 * <p>
 * 현재 소셜 로그인 단계에서는 사용자의 이메일만 로그인 식별 정보로 사용합니다.
 * 닉네임, 프로필 이미지 등 추가 정보는 별도 추가 정보 API에서 처리합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleClient implements SocialApiClient {

    private static final String GOOGLE_TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String EMAIL_CLAIM = "email";
    private final RestClient restClient = RestClient.create();

    @Value("${google.client_id}")
    private String clientId;

    @Value("${google.client_secret}")
    private String clientSecret;

    @Value("${google.redirect_uri}")
    private String redirectUri;

    /**
     * Google 로그인 제공자를 처리할 수 있는지 확인합니다.
     *
     * @param provider 로그인 제공자
     * @return Google 로그인 제공자 여부
     */
    @Override
    public boolean supports(LoginType provider) {
        return LoginType.GOOGLE.equals(provider);
    }

    /**
     * Google 인가 코드로 토큰을 발급받고 ID Token에서 이메일을 추출합니다.
     *
     * @param code Google 인가 코드
     * @return Google 계정 이메일
     */
    @Override
    public String getEmail(String code) {
        log.info("Google 로그인 이메일 조회 시작");

        if (!hasText(code)) {
            log.warn("Google 인가 코드 누락");
            throw new AuthException(AuthErrorCode.AUTHORIZATION_CODE_REQUIRED);
        }

        GoogleTokenResponse tokenResponse = requestToken(code);
        return extractEmail(tokenResponse.getIdToken());
    }


    /**
     * Google 인가 코드로 Google 토큰 발급 API를 호출합니다.
     *
     * @param code Google 인가 코드
     * @return Google 토큰 응답
     */
    private GoogleTokenResponse requestToken(String code) {
        log.debug("Google 토큰 발급 요청 시작");
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", AUTHORIZATION_CODE);

        GoogleTokenResponse tokenResponse = restClient.post()
                .uri(GOOGLE_TOKEN_URI)
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            throw new AuthException(GOOGLE_TOKEN_REQUEST_FAILED);
                        })
                .body(GoogleTokenResponse.class);

        if (tokenResponse == null || !hasText(tokenResponse.getIdToken())) {
            log.warn("Google 토큰 응답 유효성 검증 실패");
            throw new AuthException(GOOGLE_TOKEN_RESPONSE_INVALID);
        }

        log.debug("Google 토큰 발급 응답 수신 완료");
        return tokenResponse;
    }

    /**
     * Google ID Token의 payload에서 이메일을 추출합니다.
     *
     * @param idToken Google ID Token
     * @return Google 계정 이메일
     */
    private String extractEmail(String idToken) {
        log.debug("Google ID Token 이메일 추출 시작");
        String[] tokenParts = idToken.split("\\.");

        if (tokenParts.length < 2) {
            log.warn("Google ID Token 형식 오류");
            throw new AuthException(GOOGLE_ID_TOKEN_INVALID);
        }

        String payload = new String(getUrlDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);
        Map<String, Object> claims = getJsonParser().parseMap(payload);
        Object email = claims.get(EMAIL_CLAIM);

        if (!(email instanceof String value) || !hasText(value)) {
            log.warn("Google ID Token 이메일 누락");
            throw new AuthException(GOOGLE_EMAIL_NOT_FOUND);
        }

        log.debug("Google ID Token 이메일 추출 완료");
        return value;
    }
}
