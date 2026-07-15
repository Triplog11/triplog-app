package triplog.backend.common.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import triplog.backend.common.exception.CommonErrorCode;
import triplog.backend.common.exception.ErrorResponse;

import java.io.IOException;
import java.time.Duration;

/**
 * 닉네임 중복 확인 API의 요청 횟수를 제한하는 Interceptor입니다.
 * <p>
 * 클라이언트 IP를 기준으로 Redis에 요청 횟수를 저장하고, 제한 시간 안에 허용 횟수를
 * 초과하면 429 Too Many Requests 응답을 반환합니다.
 */
@Component
@RequiredArgsConstructor
public class NicknameCheckRateLimitInterceptor implements HandlerInterceptor {

    private static final String RATE_LIMIT_KEY_PREFIX = "rate-limit:nickname-check:";
    private static final int MAX_REQUEST_COUNT = 10;
    private static final Duration WINDOW_DURATION = Duration.ofMinutes(1);

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 닉네임 중복 확인 요청이 제한 횟수를 초과했는지 확인합니다.
     *
     * @param request  클라이언트 HTTP 요청
     * @param response 클라이언트 HTTP 응답
     * @param handler  선택된 핸들러 객체
     * @return 요청을 계속 처리할 수 있으면 {@code true}, 제한 횟수를 초과하면 {@code false}
     * @throws IOException 제한 응답 본문 작성 중 I/O 오류가 발생한 경우
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String clientIp = resolveClientIp(request);
        String key = RATE_LIMIT_KEY_PREFIX + clientIp;

        Long requestCount = stringRedisTemplate.opsForValue().increment(key);
        if (requestCount != null && requestCount == 1L) {
            stringRedisTemplate.expire(key, WINDOW_DURATION);
        }

        if (requestCount != null && requestCount > MAX_REQUEST_COUNT) {
            sendTooManyRequestsResponse(response);
            return false;
        }

        return true;
    }

    /**
     * 프록시 환경을 고려해 클라이언트 IP를 추출합니다.
     *
     * @param request 클라이언트 HTTP 요청
     * @return 요청 클라이언트 IP
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * 요청 횟수 초과 응답을 공통 에러 포맷으로 작성합니다.
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
