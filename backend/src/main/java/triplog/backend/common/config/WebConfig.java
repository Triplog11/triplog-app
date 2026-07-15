package triplog.backend.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import triplog.backend.common.ratelimit.NicknameCheckRateLimitInterceptor;

/**
 * Spring MVC 관련 설정을 담당하는 Configuration 클래스입니다.
 * <p>
 * 공통 Interceptor 등록처럼 MVC 요청 처리 흐름에 필요한 설정을 정의합니다.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final NicknameCheckRateLimitInterceptor nicknameCheckRateLimitInterceptor;

    /**
     * 애플리케이션에서 사용할 Interceptor를 등록합니다.
     *
     * @param registry Interceptor 등록 객체
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(nicknameCheckRateLimitInterceptor)
                .addPathPatterns("/users/nickname/check");
    }
}
