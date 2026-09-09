package triplog.backend.common.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import triplog.backend.common.exception.CommonErrorCode;
import triplog.backend.common.exception.ErrorResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 방문 인증 등록 API의 요청 간격을 제한하는 Interceptor입니다.
 * <p>
 * 인증된 사용자는 사용자 식별자, 비인증 사용자는 클라이언트 IP를 기준으로 Redis에
 * 제한 키를 저장하고, 5초 안에 요청이 반복되면 429 Too Many Requests 응답을 반환합니다.
 */
@Component
@RequiredArgsConstructor
public class ReviewCreateRateLimitInterceptor implements HandlerInterceptor {

    private static final String RATE_LIMIT_KEY_PREFIX = "rate-limit:review-create:";
    private static final Duration COOLDOWN = Duration.ofSeconds(5);

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 방문 인증 등록 요청이 허용된 간격 안에 반복되었는지 확인합니다.
     *
     * @param request  클라이언트 HTTP 요청
     * @param response 클라이언트 HTTP 응답
     * @param handler  선택된 핸들러 객체
     * @return 요청을 계속 처리할 수 있으면 {@code true}, 제한 간격 안의 요청이면 {@code false}
     * @throws IOException 제한 응답 본문 작성 중 I/O 오류가 발생한 경우
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return true;
        }

        String key = RATE_LIMIT_KEY_PREFIX + resolveRequester(request);
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", COOLDOWN);
        if (!Boolean.FALSE.equals(acquired)) {
            return true;
        }

        Long remainingSeconds = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        long retryAfter = remainingSeconds == null || remainingSeconds < 1
                ? COOLDOWN.toSeconds()
                : remainingSeconds;
        response.setHeader("Retry-After", Long.toString(retryAfter));
        sendTooManyRequestsResponse(response);
        return false;
    }

    /**
     * 요청 제한 키에 사용할 요청자 식별값을 결정합니다.
     *
     * @param request 클라이언트 HTTP 요청
     * @return 인증 사용자의 식별자 또는 비인증 사용자의 IP가 포함된 요청자 식별값
     */
    private String resolveRequester(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getName() != null && !authentication.getName().isBlank()) {
            return "user:" + authentication.getName();
        }
        return "ip:" + request.getRemoteAddr();
    }

    /**
     * 요청 간격 제한 응답을 공통 에러 포맷으로 작성합니다.
     *
     * @param response 클라이언트 HTTP 응답
     * @throws IOException 응답 본문 작성 중 I/O 오류가 발생한 경우
     */
    private void sendTooManyRequestsResponse(HttpServletResponse response) throws IOException {
        CommonErrorCode errorCode = CommonErrorCode.TOO_MANY_REQUESTS;
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                new ErrorResponse(errorCode.getHttpStatus().value(), errorCode.getMessage())
        ));
    }
}
