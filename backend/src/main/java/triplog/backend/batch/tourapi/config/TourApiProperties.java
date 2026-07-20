package triplog.backend.batch.tourapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 한국관광공사 TourAPI 호출에 공통으로 사용하는 설정입니다.
 *
 * @param baseUrl TourAPI 국문 관광정보 서비스 기본 URL
 * @param serviceKey 공공데이터포털에서 발급받은 인코딩 또는 디코딩 인증키
 * @param mobileOs 호출 애플리케이션의 OS 구분
 * @param mobileApp 공공데이터포털에 표시할 애플리케이션 이름
 * @param connectTimeout TourAPI 연결 제한 시간
 * @param readTimeout TourAPI 응답 제한 시간
 */
@ConfigurationProperties(prefix = "tour-api")
public record TourApiProperties(
        String baseUrl,
        String serviceKey,
        String mobileOs,
        String mobileApp,
        Duration connectTimeout,
        Duration readTimeout
) {
}
