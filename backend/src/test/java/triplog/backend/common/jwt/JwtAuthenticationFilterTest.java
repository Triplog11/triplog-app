package triplog.backend.common.jwt;

import jakarta.servlet.FilterChain;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import triplog.backend.common.auth.exception.AuthException;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static triplog.backend.common.auth.exception.AuthErrorCode.ACCESS_TOKEN_EXPIRED;

/**
 * JwtAuthenticationFilter의 JWT 인증 처리 흐름을 검증하는 테스트입니다.
 */
@Slf4j
class JwtAuthenticationFilterTest {

    private static final String SECRET = "SvEsvWx8nCvtvFmWsLgywF4d/SysLfI+BMoFEPCirx/P7VCnZJhC6Yr5D50agoTnZriLq6QhA55VGZcsJ7k52g==";
    private static final long ACCESS_TOKEN_VALIDITY = 10_800_000L;
    private static final long REFRESH_TOKEN_VALIDITY = 1_209_600_000L;

    /**
     * 테스트 후 SecurityContext를 초기화합니다.
     */
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * 유효한 JWT 토큰이 있으면 UserDetails 인증 객체를 저장하는지 검증합니다.
     */
    @Test
    @DisplayName("유효한 Bearer 토큰이면 인증 객체를 저장한다")
    void 유효한_Bearer_토큰이면_인증_객체를_저장한다() throws Exception {
        // given
        JwtTokenProvider jwtTokenProvider = createJwtTokenProvider();
        JwtAuthenticationFilter filter = createJwtAuthenticationFilter(jwtTokenProvider, createHandlerExceptionResolver());
        UUID usersId = UUID.randomUUID();
        String accessToken = jwtTokenProvider.createTokenRecord(usersId).accessToken();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        request.addHeader("Authorization", "Bearer " + accessToken);
        FilterChain filterChain = (servletRequest, servletResponse) -> chainCalled.set(true);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(chainCalled).isTrue();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isInstanceOf(UserDetails.class);
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(usersId.toString());
        log.info("JWT 인증 성공 - usersId: {}, SecurityContext 인증 객체 저장 완료", usersId);
    }

    /**
     * JWT 토큰이 없으면 인증 객체 없이 다음 필터로 진행하는지 검증합니다.
     */
    @Test
    @DisplayName("토큰이 없으면 인증 없이 다음 필터로 진행한다")
    void 토큰이_없으면_인증_없이_다음_필터로_진행한다() throws Exception {
        // given
        JwtTokenProvider jwtTokenProvider = createJwtTokenProvider();
        JwtAuthenticationFilter filter = createJwtAuthenticationFilter(jwtTokenProvider, createHandlerExceptionResolver());
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        FilterChain filterChain = (servletRequest, servletResponse) -> chainCalled.set(true);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(chainCalled).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        log.info("JWT 인증 생략 성공 - 이유: Authorization 헤더에 Bearer 토큰이 없습니다.");
    }

    /**
     * 유효하지 않은 JWT 토큰이면 예외를 HandlerExceptionResolver로 전달하는지 검증합니다.
     */
    @Test
    @DisplayName("유효하지 않은 토큰이면 예외를 HandlerExceptionResolver로 전달한다")
    void 유효하지_않은_토큰이면_예외를_HandlerExceptionResolver로_전달한다() throws Exception {
        // given
        JwtTokenProvider jwtTokenProvider = createJwtTokenProvider();
        AtomicBoolean exceptionResolved = new AtomicBoolean(false);
        AtomicReference<Exception> resolvedException = new AtomicReference<>();
        HandlerExceptionResolver handlerExceptionResolver = (request, response, handler, exception) -> {
            exceptionResolved.set(true);
            resolvedException.set(exception);
            return new ModelAndView();
        };
        JwtAuthenticationFilter filter = createJwtAuthenticationFilter(jwtTokenProvider, handlerExceptionResolver);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        request.addHeader("Authorization", "Bearer invalid-token");
        FilterChain filterChain = (servletRequest, servletResponse) -> chainCalled.set(true);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(chainCalled).isFalse();
        assertThat(exceptionResolved).isTrue();
        assertThat(resolvedException.get()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        log.info("JWT 예외 전달 성공 - 이유: 유효하지 않은 토큰이 HandlerExceptionResolver로 전달되었습니다.");
    }

    /**
     * 만료된 Access Token이면 401 에러 코드의 인증 예외로 변환하는지 검증합니다.
     */
    @Test
    @DisplayName("만료된 Access Token이면 401 인증 예외로 변환한다")
    void 만료된_Access_Token이면_401_인증_예외로_변환한다() throws Exception {
        // given
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, -1L, REFRESH_TOKEN_VALIDITY);
        AtomicReference<Exception> resolvedException = new AtomicReference<>();
        HandlerExceptionResolver handlerExceptionResolver = (request, response, handler, exception) -> {
            resolvedException.set(exception);
            return new ModelAndView();
        };
        JwtAuthenticationFilter filter = createJwtAuthenticationFilter(jwtTokenProvider, handlerExceptionResolver);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        String expiredAccessToken = jwtTokenProvider.createTokenRecord(UUID.randomUUID()).accessToken();
        request.addHeader("Authorization", "Bearer " + expiredAccessToken);
        FilterChain filterChain = (servletRequest, servletResponse) -> chainCalled.set(true);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(chainCalled).isFalse();
        assertThat(resolvedException.get())
                .isInstanceOfSatisfying(AuthException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ACCESS_TOKEN_EXPIRED));
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    /**
     * 테스트용 JwtAuthenticationFilter를 생성합니다.
     */
    private JwtAuthenticationFilter createJwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                                                  HandlerExceptionResolver handlerExceptionResolver) {
        return new JwtAuthenticationFilter(jwtTokenProvider, handlerExceptionResolver);
    }

    /**
     * 테스트용 HandlerExceptionResolver를 생성합니다.
     */
    private HandlerExceptionResolver createHandlerExceptionResolver() {
        return (request, response, handler, exception) -> new ModelAndView();
    }

    /**
     * 테스트용 JwtTokenProvider를 생성합니다.
     */
    private JwtTokenProvider createJwtTokenProvider() {
        return new JwtTokenProvider(SECRET, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY);
    }
}
