package triplog.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 웹 실행 여부와 관계없이 인증 서비스에서 사용할 비밀번호 인코더를 구성합니다.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 자체 로그인 비밀번호 검증에 사용할 BCrypt 인코더를 등록합니다.
     *
     * @return BCrypt 기반 비밀번호 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
