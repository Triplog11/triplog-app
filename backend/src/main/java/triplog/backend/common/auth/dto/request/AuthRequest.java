package triplog.backend.common.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    /**
     * 소셜 로그인 신규 사용자의 추가정보 입력 요청 DTO입니다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "추가정보 입력 요청")
    public static class AdditionalInfoRequest {

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해야 합니다.")
        @Schema(description = "닉네임", example = "여행자")
        private String nickname;

        @Schema(description = "프로필 이미지 URL", example = "profile-default.png", nullable = true)
        private String profileUrl;

        @NotBlank(message = "시는 필수입니다.")
        @Schema(description = "시", example = "수원시")
        private String addressSi;

        @NotBlank(message = "도/군은 필수입니다.")
        @Schema(description = "도/군", example = "경기도")
        private String addressDoGun;

        @NotBlank(message = "구는 필수입니다.")
        @Schema(description = "구", example = "팔달구")
        private String addressGu;

        @NotNull(message = "전체 알림 여부는 필수입니다.")
        @Schema(description = "전체 알림 여부. 알림 도메인 구현 전까지는 저장하지 않습니다.", example = "true")
        private Boolean isNotification;
    }
}
