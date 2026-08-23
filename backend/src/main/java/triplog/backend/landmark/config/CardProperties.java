package triplog.backend.landmark.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 랜드마크 카드 표시 기본값 설정입니다.
 *
 * @param defaultImageUrl 카드 전용 이미지가 없을 때 사용할 기본 Cloudinary URL
 */
@ConfigurationProperties(prefix = "card")
public record CardProperties(String defaultImageUrl) {
}
