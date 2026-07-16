package triplog.backend.users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자(Users) API 요청 DTO를 그룹화하는 클래스입니다.
 * <p>
 * 사용자 정보 조회, 변경, 중복 확인 등 사용자 도메인에서 사용하는 요청 DTO를
 * 내부 정적 클래스로 정의합니다.
 */
@Schema(description = "사용자 관련 요청 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsersRequest {

    /**
     * 닉네임 중복 확인 요청 DTO입니다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "닉네임 중복 확인 요청")
    public static class NicknameCheckRequest {

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해야 합니다.")
        @Schema(description = "닉네임", example = "홍길동")
        private String nickName;
    }

    /**
     * 이메일 중복 확인 요청 DTO입니다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "이메일 중복 확인 요청")
    public static class EmailCheckRequest {

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Schema(description = "이메일", example = "example@example.com")
        private String email;
    }
}
