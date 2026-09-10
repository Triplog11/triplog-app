package triplog.backend.common.ratelimit;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

/**
 * {@link ReviewCreateRateLimitInterceptor}의 방문 인증 등록 요청 간격 제한 동작을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class ReviewCreateRateLimitInterceptorTest {

    private static final String USERS_ID = "user-1";
    private static final String KEY = "rate-limit:review-create:user:" + USERS_ID;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private ReviewCreateRateLimitInterceptor interceptor;

    /**
     * 테스트 대상 Interceptor와 Redis 및 인증 정보를 준비합니다.
     */
    @BeforeEach
    void setUp() {
        interceptor = new ReviewCreateRateLimitInterceptor(stringRedisTemplate);
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(USERS_ID, null, java.util.List.of())
        );
    }

    /**
     * 테스트에서 사용한 인증 정보를 정리합니다.
     */
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * 첫 요청은 허용되고 Redis에 5초 제한 키가 생성되는지 검증합니다.
     *
     * @throws Exception Interceptor 실행 중 오류가 발생한 경우
    */
    @Test
    @DisplayName("방문 인증 등록 첫 요청이면 요청을 허용하고 5초 제한 키를 생성한다")
    void firstRequestIsAllowedAndStartsFiveSecondCooldown() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/reviews");
        MockHttpServletResponse response = new MockHttpServletResponse();
        given(valueOperations.setIfAbsent(KEY, "1", Duration.ofSeconds(5))).willReturn(true);

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isTrue();
        verify(valueOperations).setIfAbsent(KEY, "1", Duration.ofSeconds(5));
    }

    /**
     * 제한 시간 안의 재요청은 남은 대기 시간과 함께 차단되는지 검증합니다.
     *
     * @throws Exception Interceptor 실행 중 오류가 발생한 경우
    */
    @Test
    @DisplayName("방문 인증 등록을 5초 안에 다시 요청하면 남은 대기 시간과 함께 차단한다")
    void requestDuringCooldownIsRejectedWithRetryAfter() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/reviews");
        MockHttpServletResponse response = new MockHttpServletResponse();
        given(valueOperations.setIfAbsent(KEY, "1", Duration.ofSeconds(5))).willReturn(false);
        given(stringRedisTemplate.getExpire(KEY, TimeUnit.SECONDS)).willReturn(3L);

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isFalse();
        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getHeader("Retry-After")).isEqualTo("3");
        assertThat(response.getContentAsString()).contains("\"status\":429");
    }

    /**
     * POST가 아닌 요청은 방문 인증 등록 제한 대상에서 제외되는지 검증합니다.
     *
     * @throws Exception Interceptor 실행 중 오류가 발생한 경우
    */
    @Test
    @DisplayName("POST가 아닌 방문 인증 요청은 요청 간격을 제한하지 않는다")
    void nonPostRequestIsNotRateLimited() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/reviews");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertThat(result).isTrue();
    }
}
