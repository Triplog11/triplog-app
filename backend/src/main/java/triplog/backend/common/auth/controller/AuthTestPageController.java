package triplog.backend.common.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 로그인 테스트 페이지 요청을 처리하는 Controller입니다.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class AuthTestPageController {

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
     * @param code Google 로그인 완료 후 전달되는 인가 코드
     * @param model 화면 렌더링에 사용할 모델
     * @return 로그인 테스트 페이지 템플릿
     */
    @GetMapping("/auth/test-login")
    public String testLoginPage(@RequestParam(required = false) String code, Model model) {
        log.info("로그인 테스트 페이지 요청 수신");
        addLoginTestAttributes(code, model);
        return "auth/login-test";
    }

    /**
     * Google OAuth redirect URI 요청을 로그인 테스트 페이지로 연결합니다.
     *
     * @param code Google 로그인 완료 후 전달되는 인가 코드
     * @param model 화면 렌더링에 사용할 모델
     * @return 로그인 테스트 페이지 템플릿
     */
    @GetMapping("/login/oauth2/code/google")
    public String googleCallbackPage(@RequestParam(required = false) String code, Model model) {
        log.info("Google 로그인 테스트 callback 요청 수신");
        addLoginTestAttributes(code, model);
        return "auth/login-test";
    }

    /**
     * 로그인 테스트 페이지에 필요한 모델 속성을 추가합니다.
     *
     * @param code Google 로그인 완료 후 전달되는 인가 코드
     * @param model 화면 렌더링에 사용할 모델
     */
    private void addLoginTestAttributes(String code, Model model) {
        model.addAttribute("googleClientId", googleClientId);
        model.addAttribute("googleRedirectUri", googleRedirectUri);
        model.addAttribute("naverClientId", naverClientId);
        model.addAttribute("naverRedirectUri", naverRedirectUri);
        model.addAttribute("code", code);
    }
}
