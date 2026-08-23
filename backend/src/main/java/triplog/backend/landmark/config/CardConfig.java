package triplog.backend.landmark.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 랜드마크 카드 설정을 등록합니다.
 */
@Configuration
@EnableConfigurationProperties(CardProperties.class)
public class CardConfig {
}
