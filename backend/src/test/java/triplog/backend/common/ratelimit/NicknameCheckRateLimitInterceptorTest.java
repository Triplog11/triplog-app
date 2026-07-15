package triplog.backend.common.ratelimit;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * {@link NicknameCheckRateLimitInterceptor}의 요청 횟수 제한 동작을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class NicknameCheckRateLimitInterceptorTest {

    private static final String CLIENT_IP = "127.0.0.1";
    private static final String RATE_LIMIT_KEY = "rate-limit:nickname-check:" + CLIENT_IP;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private NicknameCheckRateLimitInterceptor interceptor;

    /**
     * 테스트 대상 Interceptor와 Redis mock 동작을 준비합니다.
     */
    @BeforeEach
    void setUp() {
        interceptor = new NicknameCheckRateLimitInterceptor(stringRedisTemplate);
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
    }

    /**
     * 제한 횟수 이내 요청은 컨트롤러로 계속 전달되는지 검증합니다.
     *
     * @throws Exception Interceptor 실행 중 오류가 발생한 경우
     */
    @Test
    @DisplayName("닉네임 중복 확인 요청이 제한 횟수 이내이면 요청을 허용한다")
    void preHandle_Allowed() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(CLIENT_IP);
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(valueOperations.increment(RATE_LIMIT_KEY)).willReturn(10L);

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isTrue();
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    /**
     * 첫 요청이면 Redis 키 만료 시간이 설정되는지 검증합니다.
     *
     * @throws Exception Interceptor 실행 중 오류가 발생한 경우
     */
    @Test
    @DisplayName("닉네임 중복 확인 첫 요청이면 Redis 키 만료 시간을 설정한다")
    void preHandle_FirstRequest_SetExpire() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(CLIENT_IP);
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(valueOperations.increment(RATE_LIMIT_KEY)).willReturn(1L);

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isTrue();
        verify(stringRedisTemplate).expire(eq(RATE_LIMIT_KEY), eq(Duration.ofMinutes(1)));
    }

    /**
     * 제한 횟수를 초과한 요청은 429 응답으로 차단되는지 검증합니다.
     *
     * @throws Exception Interceptor 실행 중 오류가 발생한 경우
     */
    @Test
    @DisplayName("닉네임 중복 확인 요청이 제한 횟수를 초과하면 429 응답을 반환한다")
    void preHandle_TooManyRequests() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(CLIENT_IP);
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(valueOperations.increment(RATE_LIMIT_KEY)).willReturn(11L);

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getContentAsString()).contains("\"status\":429");
        assertThat(response.getContentAsString()).contains("\"message\":\"요청을 너무 많이 보냈습니다.\"");
    }

    /**
     * 프록시 헤더가 있으면 X-Forwarded-For의 첫 번째 IP를 기준으로 제한하는지 검증합니다.
     *
     * @throws Exception Interceptor 실행 중 오류가 발생한 경우
     */
    @Test
    @DisplayName("X-Forwarded-For 헤더가 있으면 첫 번째 IP를 기준으로 요청 횟수를 제한한다")
    void preHandle_UseForwardedFor() throws Exception {
        // given
        String forwardedIp = "203.0.113.1";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", forwardedIp + ", 10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(valueOperations.increment("rate-limit:nickname-check:" + forwardedIp)).willReturn(1L);

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isTrue();
        verify(valueOperations).increment("rate-limit:nickname-check:" + forwardedIp);
        verify(stringRedisTemplate).expire(any(String.class), eq(Duration.ofMinutes(1)));
    }
}
