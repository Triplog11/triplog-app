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
 * 제한 키를 저장하고, 5초 안에 새로운 요청이 반복되면 429 Too Many Requests 응답을 반환합니다.
 * <p>
 * 단, 같은 {@code Idempotency-Key}를 가진 요청은 네트워크 실패 후의 정상 재시도일 수 있으므로
 * 제한하지 않습니다. 동일 키 요청은 서비스 계층과 데이터베이스 유일 제약이 실제 중복 실행을
 * 차단합니다. 다른 키를 사용한 새 인증 요청만 기존 5초 제한의 적용을 받습니다.
 */
@Component
@RequiredArgsConstructor
public class ReviewCreateRateLimitInterceptor implements HandlerInterceptor {

    private static final String RATE_LIMIT_KEY_PREFIX = "rate-limit:review-create:";
    private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";
    private static final String NO_IDEMPOTENCY_KEY = "1";
    private static final Duration COOLDOWN = Duration.ofSeconds(5);

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 방문 인증 등록 요청이 허용된 간격 안에 반복되었는지 확인합니다.
     * Redis 값에는 단순 점유 표시가 아니라 최초 요청의 멱등성 키를 저장하여, 제한 시간 안의
     * 후속 요청이 재시도인지 새로운 인증 요청인지 구분합니다.
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
        String requestKey = normalizeIdempotencyKey(request.getHeader(IDEMPOTENCY_HEADER));
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(key, requestKey, COOLDOWN);
        if (!Boolean.FALSE.equals(acquired)) {
            return true;
        }
        // 같은 키의 재시도는 Facade의 멱등성 처리까지 도달해야 최초 응답을 재사용할 수 있습니다.
        if (!NO_IDEMPOTENCY_KEY.equals(requestKey)
                && requestKey.equals(stringRedisTemplate.opsForValue().get(key))) {
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

    /** 헤더가 없는 요청은 기존 요청 제한 동작을 유지할 수 있도록 고정 점유 값으로 바꿉니다. */
    private String normalizeIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return NO_IDEMPOTENCY_KEY;
        }
        return idempotencyKey.trim();
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
