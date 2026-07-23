package triplog.backend.common.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link AuthTestPageController}의 로그인 테스트 페이지 모델 구성을 검증하는 테스트입니다.
 */
@Slf4j
class AuthTestPageControllerTest {

    private static final String GOOGLE_CLIENT_ID = "google-client-id";
    private static final String GOOGLE_REDIRECT_URI = "http://localhost:8080/login/oauth2/code/google";
    private static final String NAVER_CLIENT_ID = "naver-client-id";
    private static final String NAVER_REDIRECT_URI = "http://localhost:8080/login/oauth2/code/naver";

    private AuthTestPageController controller;

    /**
     * 테스트 대상 컨트롤러와 OAuth 설정값을 준비합니다.
     */
    @BeforeEach
    void setUp() {
        controller = new AuthTestPageController();
        ReflectionTestUtils.setField(controller, "googleClientId", GOOGLE_CLIENT_ID);
        ReflectionTestUtils.setField(controller, "googleRedirectUri", GOOGLE_REDIRECT_URI);
        ReflectionTestUtils.setField(controller, "naverClientId", NAVER_CLIENT_ID);
        ReflectionTestUtils.setField(controller, "naverRedirectUri", NAVER_REDIRECT_URI);
    }

    /**
     * 로그인 테스트 시작 페이지가 Google을 기본 provider로 렌더링하는지 검증합니다.
     */
    @Test
    @DisplayName("로그인 테스트 시작 페이지는 Google을 기본 provider로 사용한다")
    void 로그인_테스트_시작_페이지는_Google을_기본_provider로_사용한다() {
        // given
        Model model = new ExtendedModelMap();

        // when
        String viewName = controller.testLoginPage(null, model);

        // then
        assertThat(viewName).isEqualTo("auth/login-test");
        assertCommonModelAttributes(model);
        assertThat(model.asMap().get("code")).isNull();
        assertThat(model.asMap().get("provider")).isEqualTo("google");
        log.info("로그인 테스트 시작 페이지 모델 검증 완료");
    }

    /**
     * Google OAuth callback 페이지가 인가 코드와 Google provider를 모델에 담는지 검증합니다.
     */
    @Test
    @DisplayName("Google callback은 code와 google provider를 모델에 담는다")
    void Google_callback은_code와_google_provider를_모델에_담는다() {
        // given
        Model model = new ExtendedModelMap();
        String code = "google-code";

        // when
        Object result = controller.googleCallbackPage(code, null, null, model);

        // then
        assertThat(result).isEqualTo("auth/login-test");
        assertCommonModelAttributes(model);
        assertThat(model.asMap().get("code")).isEqualTo(code);
        assertThat(model.asMap().get("provider")).isEqualTo("google");
        log.info("Google callback 모델 검증 완료");
    }

    /**
     * 앱 Google OAuth callback이 인가 코드와 state를 딥링크로 전달하는지 검증합니다.
     */
    @Test
    @DisplayName("앱 Google callback은 code와 state를 Triplog 딥링크로 전달한다")
    void 앱_Google_callback은_code와_state를_Triplog_딥링크로_전달한다() {
        // given
        Model model = new ExtendedModelMap();
        String code = "google-code";
        String state = "app:random-state";

        // when
        Object result = controller.googleCallbackPage(code, state, null, model);

        // then
        assertThat(result).isInstanceOf(ResponseEntity.class);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        UriComponents location = UriComponentsBuilder.fromUri(response.getHeaders().getLocation()).build();
        assertThat(location.getScheme()).isEqualTo("triplog");
        assertThat(location.getHost()).isEqualTo("oauth");
        assertThat(location.getQueryParams().getFirst("code")).isEqualTo(code);
        assertThat(location.getQueryParams().getFirst("state")).isEqualTo(state);
        assertThat(location.getQueryParams()).containsOnlyKeys("code", "state");
        assertThat(model.asMap()).isEmpty();
    }

    /**
     * 앱 Google 인증 실패 callback이 오류 정보를 딥링크로 전달하는지 검증합니다.
     */
    @Test
    @DisplayName("앱 Google callback은 인증 오류를 Triplog 딥링크로 전달한다")
    void 앱_Google_callback은_인증_오류를_Triplog_딥링크로_전달한다() {
        // given
        Model model = new ExtendedModelMap();
        String state = "app:random-state";
        String error = "access_denied";

        // when
        Object result = controller.googleCallbackPage(null, state, error, model);

        // then
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        UriComponents location = UriComponentsBuilder.fromUri(response.getHeaders().getLocation()).build();
        assertThat(location.getQueryParams().getFirst("state")).isEqualTo(state);
        assertThat(location.getQueryParams().getFirst("error")).isEqualTo(error);
        assertThat(location.getQueryParams()).doesNotContainKey("code");
        assertThat(model.asMap()).isEmpty();
    }

    /**
     * Naver OAuth callback 페이지가 인가 코드와 Naver provider를 모델에 담는지 검증합니다.
     */
    @Test
    @DisplayName("Naver callback은 code와 naver provider를 모델에 담는다")
    void Naver_callback은_code와_naver_provider를_모델에_담는다() {
        // given
        Model model = new ExtendedModelMap();
        String code = "naver-code";

        // when
        Object result = controller.naverCallbackPage(code, "naver-random-state", null, model);

        // then
        assertThat(result).isEqualTo("auth/login-test");
        assertCommonModelAttributes(model);
        assertThat(model.asMap().get("code")).isEqualTo(code);
        assertThat(model.asMap().get("provider")).isEqualTo("naver");
        log.info("Naver callback 모델 검증 완료");
    }

    /**
     * 앱 Naver OAuth callback이 인가 코드와 state를 딥링크로 전달하는지 검증합니다.
     */
    @Test
    @DisplayName("앱 Naver callback은 code와 state를 Triplog 딥링크로 전달한다")
    void 앱_Naver_callback은_code와_state를_Triplog_딥링크로_전달한다() {
        // given
        Model model = new ExtendedModelMap();
        String code = "naver-code";
        String state = "app:random-state";

        // when
        Object result = controller.naverCallbackPage(code, state, null, model);

        // then
        assertThat(result).isInstanceOf(ResponseEntity.class);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        UriComponents location = UriComponentsBuilder.fromUri(response.getHeaders().getLocation()).build();
        assertThat(location.getScheme()).isEqualTo("triplog");
        assertThat(location.getHost()).isEqualTo("oauth");
        assertThat(location.getQueryParams().getFirst("code")).isEqualTo(code);
        assertThat(location.getQueryParams().getFirst("state")).isEqualTo(state);
        assertThat(location.getQueryParams()).containsOnlyKeys("code", "state");
        assertThat(model.asMap()).isEmpty();
    }

    /**
     * 앱 Naver 인증 실패 callback이 오류 정보를 딥링크로 전달하는지 검증합니다.
     */
    @Test
    @DisplayName("앱 Naver callback은 인증 오류를 Triplog 딥링크로 전달한다")
    void 앱_Naver_callback은_인증_오류를_Triplog_딥링크로_전달한다() {
        // given
        Model model = new ExtendedModelMap();
        String state = "app:random-state";
        String error = "access_denied";

        // when
        Object result = controller.naverCallbackPage(null, state, error, model);

        // then
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        UriComponents location = UriComponentsBuilder.fromUri(response.getHeaders().getLocation()).build();
        assertThat(location.getQueryParams().getFirst("state")).isEqualTo(state);
        assertThat(location.getQueryParams().getFirst("error")).isEqualTo(error);
        assertThat(location.getQueryParams()).doesNotContainKey("code");
        assertThat(model.asMap()).isEmpty();
    }

    /**
     * 로그인 테스트 페이지 공통 모델 속성을 검증합니다.
     *
     * @param model 검증할 모델
     */
    private void assertCommonModelAttributes(Model model) {
        assertThat(model.asMap().get("googleClientId")).isEqualTo(GOOGLE_CLIENT_ID);
        assertThat(model.asMap().get("googleRedirectUri")).isEqualTo(GOOGLE_REDIRECT_URI);
        assertThat(model.asMap().get("naverClientId")).isEqualTo(NAVER_CLIENT_ID);
        assertThat(model.asMap().get("naverRedirectUri")).isEqualTo(NAVER_REDIRECT_URI);
    }
}
