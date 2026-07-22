package triplog.backend.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collections;

/**
 * 요청의 Authorization 헤더에서 JWT를 추출해 인증 정보를 설정하는 필터입니다.
 * <p>
 * 일반 Access Token은 사용자 ID를 principal로 사용하고,
 * 회원가입용 임시 토큰은 추가정보 입력 API에서만 이메일을 principal로 사용합니다.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ADDITIONAL_INFO_PATH = "/auth/additional-info";
    private final JwtTokenProvider jwtTokenProvider;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * JWT 토큰을 검증하고 인증 객체를 SecurityContext에 저장합니다.
     * <p>
     * 회원가입용 임시 토큰은 추가정보 입력 API에서만 인증 객체로 변환합니다.
     * 다른 보호 API에서 임시 토큰이 일반 인증 토큰처럼 사용되는 것을 막기 위한 제한입니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);

            if (token != null) {
                jwtTokenProvider.validateToken(token);

                if (!jwtTokenProvider.isTemporaryToken(token) || ADDITIONAL_INFO_PATH.equals(request.getServletPath())) {
                    String principal = jwtTokenProvider.getAuthenticationPrincipal(token);
                    Authentication authentication = createAuthentication(principal, token, request);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 값을 추출합니다.
     */
    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * JWT principal을 기반으로 Spring Security 인증 객체를 생성합니다.
     */
    private Authentication createAuthentication(String principal, String token, HttpServletRequest request) {
        UserDetails userDetails = User.withUsername(principal)
                .password("")
                .authorities(Collections.emptyList())
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }
}
