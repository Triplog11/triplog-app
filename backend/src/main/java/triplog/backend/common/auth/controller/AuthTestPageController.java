package triplog.backend.common.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 로그인 테스트 페이지 요청을 처리하는 Controller입니다.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class AuthTestPageController {

    private static final String APP_STATE_PREFIX = "app:";
    private static final String APP_DEEP_LINK = "triplog://oauth";

    @Value("${google.client_id}")
    private String googleClientId;

    @Value("${google.redirect_uri}")
    private String googleRedirectUri;

    @Value("${naver.client_id}")
    private String naverClientId;

    @Value("${naver.redirect_uri}")
    private String naverRedirectUri;

    /**
     * 로그인 테스트 시작 페이지를 반환합니다.
     *
     * @param code 소셜 로그인 완료 후 전달되는 인가 코드
     * @param model 화면 렌더링에 사용할 모델
     * @return 로그인 테스트 페이지 템플릿
     */
    @GetMapping("/auth/test-login")
    public String testLoginPage(@RequestParam(required = false) String code, Model model) {
        log.info("로그인 테스트 페이지 요청 수신");
        addLoginTestAttributes(code, "google", model);
        return "auth/login-test";
    }

    /**
     * Google OAuth callback 요청을 처리합니다.
     * <p>
     * 앱 로그인 요청이면 딥링크로 리다이렉트하고, 테스트 요청이면 기존 테스트 페이지를 반환합니다.
     *
     * @param code Google 로그인 완료 후 전달되는 인가 코드
     * @param state Google 로그인 요청의 CSRF 방지 및 클라이언트 구분 값
     * @param error Google 인증 실패 시 전달되는 오류 코드
     * @param model 화면 렌더링에 사용할 모델
     * @return 앱 딥링크 리다이렉트 응답 또는 로그인 테스트 페이지 템플릿
     */
    @GetMapping("/login/oauth2/code/google")
    public Object googleCallbackPage(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            Model model
    ) {
        if (isAppLogin(state)) {
            log.info("Google 앱 로그인 callback 요청 수신");
            return redirectToApp(code, state, error);
        }

        log.info("Google 로그인 테스트 callback 요청 수신");
        addLoginTestAttributes(code, "google", model);
        return "auth/login-test";
    }

    /**
     * Google callback의 state가 앱 로그인 요청을 나타내는지 확인합니다.
     *
     * @param state Google 로그인 요청에서 전달한 state
     * @return 앱 로그인 요청이면 {@code true}
     */
    private boolean isAppLogin(String state) {
        return state != null && state.startsWith(APP_STATE_PREFIX);
    }

    /**
     * Google 인증 결과를 Triplog 앱 딥링크로 전달합니다.
     *
     * @param code 소셜 로그인 인가 코드
     * @param state 소셜 로그인 요청의 state
     * @param error 소셜 로그인 인증 오류 코드
     * @return 앱 딥링크를 Location 헤더에 담은 302 응답
     */
    private ResponseEntity<Void> redirectToApp(String code, String state, String error) {
        UriComponentsBuilder redirect = UriComponentsBuilder.fromUriString(APP_DEEP_LINK);
        if (code != null) {
            redirect.queryParam("code", code);
        }
        if (state != null) {
            redirect.queryParam("state", state);
        }
        if (error != null) {
            redirect.queryParam("error", error);
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirect.build().encode().toUri())
                .build();
    }

    /**
     * Naver OAuth callback 요청을 처리합니다.
     * <p>
     * 앱 로그인 요청이면 딥링크로 리다이렉트하고, 테스트 요청이면 기존 테스트 페이지를 반환합니다.
     *
     * @param code Naver 로그인 완료 후 전달되는 인가 코드
     * @param state Naver 로그인 요청의 CSRF 방지 및 클라이언트 구분 값
     * @param error Naver 인증 실패 시 전달되는 오류 코드
     * @param model 화면 렌더링에 사용할 모델
     * @return 앱 딥링크 리다이렉트 응답 또는 로그인 테스트 페이지 템플릿
     */
    @GetMapping("/login/oauth2/code/naver")
    public Object naverCallbackPage(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            Model model
    ) {
        if (isAppLogin(state)) {
            log.info("Naver 앱 로그인 callback 요청 수신");
            return redirectToApp(code, state, error);
        }

        log.info("Naver 로그인 테스트 callback 요청 수신");
        addLoginTestAttributes(code, "naver", model);
        return "auth/login-test";
    }

    /**
     * 로그인 테스트 페이지에 필요한 모델 속성을 추가합니다.
     *
     * @param code 소셜 로그인 완료 후 전달되는 인가 코드
     * @param provider 화면에서 기본 선택할 로그인 제공자
     * @param model 화면 렌더링에 사용할 모델
     */
    private void addLoginTestAttributes(String code, String provider, Model model) {
        model.addAttribute("googleClientId", googleClientId);
        model.addAttribute("googleRedirectUri", googleRedirectUri);
        model.addAttribute("naverClientId", naverClientId);
        model.addAttribute("naverRedirectUri", naverRedirectUri);
        model.addAttribute("code", code);
        model.addAttribute("provider", provider);
    }
}
