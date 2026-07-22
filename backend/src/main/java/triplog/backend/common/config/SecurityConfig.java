package triplog.backend.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import triplog.backend.common.jwt.JwtAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

/**
 * 애플리케이션의 Spring Security 설정을 담당하는 구성 클래스입니다.
 * <p>
 * JWT 기반의 stateless 인증 방식을 사용하며, 로그인과 문서 접근처럼 인증 전에 필요한 요청만 허용합니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * HTTP 요청에 적용할 보안 필터 체인을 구성합니다.
     * <p>
     * CSRF와 세션을 비활성화하고, 인증이 필요 없는 공개 경로를 제외한 모든 요청에 JWT 인증을 적용합니다.
     *
     * @param http Spring Security HTTP 보안 설정 객체
     * @return 구성된 보안 필터 체인
     * @throws Exception 보안 필터 체인 구성 중 오류가 발생한 경우
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/auth/oauth",
                                "/auth/signup",
                                "/auth/test-login",
                                "/login/oauth2/**",
                                "/auth/additional-info",
                                "/users/nickname/check",
                                "/users/email/check",
                                "/images/**",
                                "/scalar/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 인증되지 않은 사용자가 보호된 리소스에 접근했을 때 401 응답을 반환합니다.
     *
     * @return 인증 실패 처리 핸들러
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) ->
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
    }

    /**
     * 인증된 사용자가 권한이 없는 리소스에 접근했을 때 403 응답을 반환합니다.
     *
     * @return 접근 거부 처리 핸들러
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
    }

    /**
     * Spring Security 예외 응답을 공통 JSON 형식으로 작성합니다.
     *
     * @param response 클라이언트로 반환할 HTTP 응답
     * @param status HTTP 상태 코드
     * @param message 응답에 포함할 에러 메시지
     * @throws IOException 응답 본문 작성 중 I/O 오류가 발생한 경우
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "status", status,
                "message", message
        )));
    }
}
