package triplog.backend.common.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.users.entity.LoginType;

/**
 * 인증(Auth) API 요청 DTO를 그룹화하는 클래스입니다.
 * <p>
 * 로그인, 소셜 로그인, 토큰 재발급, 로그아웃 요청에서 사용하는 Request DTO를
 * 내부 정적 클래스로 정의합니다.
 */
@Schema(description = "인증 관련 요청 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthRequest {

    /**
     * 로그인 요청 DTO입니다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "로그인 요청")
    public static class LoginRequest {

        @NotNull(message = "로그인 제공자는 필수입니다.")
        @Schema(description = "로그인 제공자", example = "GOOGLE")
        private LoginType provider;

        @Schema(description = "소셜 로그인(GOOGLE, NAVER)에서 사용하는 인가 코드", example = "authorization-code-value")
        private String code;

        @Schema(description = "Naver 소셜 로그인 CSRF 방지 state 값", example = "naver-state-value")
        private String state;

        @Schema(description = "자체 로그인(LOCAL)에서 사용하는 이메일", example = "test@test.com")
        private String email;

        @Schema(description = "자체 로그인(LOCAL)에서 사용하는 비밀번호", example = "344rsdkf")
        private String password;
    }
}
