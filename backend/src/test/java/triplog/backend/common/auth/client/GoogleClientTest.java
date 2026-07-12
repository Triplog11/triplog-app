package triplog.backend.common.auth.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import triplog.backend.common.auth.exception.AuthErrorCode;
import triplog.backend.common.auth.exception.AuthException;
import triplog.backend.users.entity.LoginType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * GoogleClient의 Google 로그인 처리 기능을 검증하는 테스트입니다.
 */
@Slf4j
class GoogleClientTest {

    /**
     * Google 로그인 타입을 지원하는지 검증합니다.
     */
    @Test
    @DisplayName("GOOGLE provider는 지원한다")
    void GOOGLE_provider는_지원한다() {
        // given
        GoogleClient googleClient = new GoogleClient();

        // when
        boolean result = googleClient.supports(LoginType.GOOGLE);

        // then
        assertThat(result).isTrue();
        log.info("Google provider 지원 여부 검증 성공");
    }

    /**
     * Google 이외의 로그인 타입은 지원하지 않는지 검증합니다.
     */
    @Test
    @DisplayName("GOOGLE이 아닌 provider는 지원하지 않는다")
    void GOOGLE이_아닌_provider는_지원하지_않는다() {
        // given
        GoogleClient googleClient = new GoogleClient();

        // when
        boolean result = googleClient.supports(LoginType.LOCAL);

        // then
        assertThat(result).isFalse();
        log.info("Google 이외 provider 미지원 검증 성공");
    }

    /**
     * Google 인가 코드가 비어 있으면 인증 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("Google 인가 코드가 비어 있으면 예외가 발생한다")
    void Google_인가_코드가_비어_있으면_예외가_발생한다() {
        // given
        GoogleClient googleClient = new GoogleClient();

        // when
        // then
        assertThatThrownBy(() -> googleClient.getEmail(" "))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.AUTHORIZATION_CODE_REQUIRED);
        log.info("Google 인가 코드 누락 예외 검증 성공");
    }

    /**
     * Google ID Token에서 이메일을 추출하는지 검증합니다.
     */
    @Test
    @DisplayName("Google ID Token에서 이메일을 추출한다")
    void Google_ID_Token에서_이메일을_추출한다() {
        // given
        GoogleClient googleClient = new GoogleClient();
        String expectedEmail = "test@example.com";
        String idToken = createIdToken("{\"email\":\"" + expectedEmail + "\"}");

        // when
        String email = ReflectionTestUtils.invokeMethod(googleClient, "extractEmail", idToken);

        // then
        assertThat(email).isEqualTo(expectedEmail);
        log.info("Google ID Token 이메일 추출 검증 성공");
    }

    /**
     * Google ID Token에 이메일이 없으면 인증 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("Google ID Token에 이메일이 없으면 예외가 발생한다")
    void Google_ID_Token에_이메일이_없으면_예외가_발생한다() {
        // given
        GoogleClient googleClient = new GoogleClient();
        String idToken = createIdToken("{\"sub\":\"google-user-id\"}");

        // when
        // then
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(googleClient, "extractEmail", idToken))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.GOOGLE_EMAIL_NOT_FOUND);
        log.info("Google ID Token 이메일 누락 예외 검증 성공");
    }

    /**
     * 테스트용 ID Token 형식의 문자열을 생성합니다.
     *
     * @param payloadJson payload JSON
     * @return ID Token 형식 문자열
     */
    private String createIdToken(String payloadJson) {
        String header = encode("{}");
        String payload = encode(payloadJson);
        String signature = encode("signature");

        return header + "." + payload + "." + signature;
    }

    /**
     * 문자열을 Base64 URL 형식으로 인코딩합니다.
     *
     * @param value 인코딩할 문자열
     * @return Base64 URL 인코딩 문자열
     */
    private String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
