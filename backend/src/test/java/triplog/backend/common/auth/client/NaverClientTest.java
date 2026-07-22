package triplog.backend.common.auth.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import triplog.backend.common.auth.dto.response.AuthResponse.NaverUserInfoResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.NaverUserInfoResponse.Response;
import triplog.backend.common.auth.exception.AuthErrorCode;
import triplog.backend.common.auth.exception.AuthException;
import triplog.backend.users.entity.LoginType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static triplog.backend.common.auth.exception.AuthErrorCode.AUTHORIZATION_CODE_REQUIRED;
import static triplog.backend.common.auth.exception.AuthErrorCode.NAVER_EMAIL_NOT_FOUND;
import static triplog.backend.common.auth.exception.AuthErrorCode.NAVER_STATE_REQUIRED;
import static triplog.backend.users.entity.LoginType.LOCAL;
import static triplog.backend.users.entity.LoginType.NAVER;

/**
 * NaverClient의 Naver 로그인 처리 기능을 검증하는 테스트입니다.
 */
@Slf4j
class NaverClientTest {

    /**
     * Naver 로그인 타입을 지원하는지 검증합니다.
     */
    @Test
    @DisplayName("NAVER provider는 지원한다")
    void NAVER_provider는_지원한다() {
        // given
        NaverClient naverClient = new NaverClient();

        // when
        boolean result = naverClient.supports(NAVER);

        // then
        assertThat(result).isTrue();
        log.info("Naver provider 지원 여부 검증 성공");
    }

    /**
     * Naver 이외의 로그인 타입은 지원하지 않는지 검증합니다.
     */
    @Test
    @DisplayName("NAVER가 아닌 provider는 지원하지 않는다")
    void NAVER가_아닌_provider는_지원하지_않는다() {
        // given
        NaverClient naverClient = new NaverClient();

        // when
        boolean result = naverClient.supports(LOCAL);

        // then
        assertThat(result).isFalse();
        log.info("Naver 이외 provider 미지원 검증 성공");
    }

    /**
     * Naver 인가 코드가 비어 있으면 인증 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("Naver 인가 코드가 비어 있으면 예외가 발생한다")
    void Naver_인가_코드가_비어_있으면_예외가_발생한다() {
        // given
        NaverClient naverClient = new NaverClient();

        // when
        // then
        assertThatThrownBy(() -> naverClient.getEmail(" "))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(AUTHORIZATION_CODE_REQUIRED);
        log.info("Naver 인가 코드 누락 예외 검증 성공");
    }

    /**
     * Naver state 값이 비어 있으면 인증 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("Naver state 값이 비어 있으면 예외가 발생한다")
    void Naver_state_값이_비어_있으면_예외가_발생한다() {
        // given
        NaverClient naverClient = new NaverClient();

        // when
        // then
        assertThatThrownBy(() -> naverClient.getEmail("authorization-code", " "))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(NAVER_STATE_REQUIRED);
        log.info("Naver state 누락 예외 검증 성공");
    }

    /**
     * Naver 사용자 정보 응답에서 이메일을 추출하는지 검증합니다.
     */
    @Test
    @DisplayName("Naver 사용자 정보 응답에서 이메일을 추출한다")
    void Naver_사용자_정보_응답에서_이메일을_추출한다() {
        // given
        NaverClient naverClient = new NaverClient();
        String expectedEmail = "test@example.com";
        NaverUserInfoResponse userInfoResponse = createUserInfoResponse(expectedEmail);

        // when
        String email = invokeMethod(naverClient, "extractEmail", userInfoResponse);

        // then
        assertThat(email).isEqualTo(expectedEmail);
        log.info("Naver 사용자 정보 이메일 추출 검증 성공");
    }

    /**
     * Naver 사용자 정보 응답에 이메일이 없으면 인증 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("Naver 사용자 정보 응답에 이메일이 없으면 예외가 발생한다")
    void Naver_사용자_정보_응답에_이메일이_없으면_예외가_발생한다() {
        // given
        NaverClient naverClient = new NaverClient();
        NaverUserInfoResponse userInfoResponse = createUserInfoResponse(null);

        // when
        // then
        assertThatThrownBy(() -> invokeMethod(naverClient, "extractEmail", userInfoResponse))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(NAVER_EMAIL_NOT_FOUND);
        log.info("Naver 사용자 정보 이메일 누락 예외 검증 성공");
    }

    /**
     * 테스트용 Naver 사용자 정보 응답을 생성합니다.
     *
     * @param email 응답에 포함할 이메일
     * @return Naver 사용자 정보 응답
     */
    private NaverUserInfoResponse createUserInfoResponse(String email) {
        NaverUserInfoResponse userInfoResponse = new NaverUserInfoResponse();
        Response response = new Response();

        setField(response, "email", email);
        setField(userInfoResponse, "response", response);

        return userInfoResponse;
    }
}
