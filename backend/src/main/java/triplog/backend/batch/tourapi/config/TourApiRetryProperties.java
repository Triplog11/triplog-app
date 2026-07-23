package triplog.backend.batch.tourapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

/**
 * TourAPI 요청 재시도 정책을 관리하는 설정입니다.
 *
 * @param maxRetries 최초 요청 실패 후 추가로 시도할 최대 횟수
 * @param delays 각 재시도 전에 적용할 기본 대기 시간
 */
@ConfigurationProperties(prefix = "tour-api.retry")
public record TourApiRetryProperties(
        int maxRetries,
        List<Duration> delays
) {

    /**
     * 설정된 재시도 순번에 대응하는 대기 시간을 반환합니다.
     *
     * @param retryIndex 0부터 시작하는 재시도 순번
     * @return 재시도 전에 적용할 대기 시간
     */
    public Duration delayFor(int retryIndex) {
        if (delays == null || delays.isEmpty()) {
            return Duration.ZERO;
        }
        int safeIndex = Math.min(Math.max(retryIndex, 0), delays.size() - 1);
        return delays.get(safeIndex);
    }
}
