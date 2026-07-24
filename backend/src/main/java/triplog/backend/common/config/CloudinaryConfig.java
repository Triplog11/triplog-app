package triplog.backend.common.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import triplog.backend.image.config.ImageUploadProperties;

/**
 * Cloudinary Java SDK를 사용하기 위한 애플리케이션 설정 클래스입니다.
 */
@Configuration
@EnableConfigurationProperties(ImageUploadProperties.class)
public class CloudinaryConfig {

    /**
     * 설정 파일에서 Cloudinary 인증 정보를 읽어 SDK 클라이언트를 생성합니다.
     *
     * @param cloudName Cloudinary product environment의 cloud name
     * @param apiKey Cloudinary API key
     * @param apiSecret Cloudinary API secret
     * @return HTTPS 통신을 사용하도록 설정된 Cloudinary 클라이언트
     */
    @Bean
    public Cloudinary cloudinary(
            @Value("${cloudinary.cloud.name}") String cloudName,
            @Value("${cloudinary.api.key}") String apiKey,
            @Value("${cloudinary.api.secret}") String apiSecret) {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}
