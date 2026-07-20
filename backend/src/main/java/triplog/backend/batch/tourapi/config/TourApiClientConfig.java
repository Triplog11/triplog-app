package triplog.backend.batch.tourapi.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

/**
 * TourAPI 전용 HTTP Client를 구성합니다.
 */
@Configuration
@EnableConfigurationProperties(TourApiProperties.class)
public class TourApiClientConfig {

    /**
     * TourAPI 기본 URL과 JSON 응답 헤더가 적용된 RestClient를 생성합니다.
     *
     * @param properties TourAPI 설정
     * @return TourAPI 전용 RestClient
     */
    @Bean
    public RestClient tourApiRestClient(TourApiProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
