package triplog.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * 운영 코드와 테스트에서 동일한 시간 공급 경계를 사용하도록 구성합니다.
 */
@Configuration
public class TimeConfig {

    /**
     * 시스템 기본 시간대의 시계를 제공합니다.
     *
     * @return 애플리케이션 공용 시계
     */
    @Bean
    public Clock applicationClock() {
        return Clock.systemDefaultZone();
    }
}
