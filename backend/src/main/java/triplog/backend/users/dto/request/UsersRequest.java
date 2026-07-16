package triplog.backend.users.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
     * 프로필 수정 요청 DTO입니다.
     * <p>
     * 모든 필드는 선택 입력값이며, 요청에서 전달되지 않은 필드는 수정 대상에서 제외됩니다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "프로필 수정 요청")
    public static class ProfileUpdateRequest {

        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해야 합니다.")
        @Schema(description = "닉네임", example = "여행자", nullable = true)
        private String nickname;

        @Size(min = 1, max = 20, message = "시는 1자 이상 20자 이하로 입력해야 합니다.")
        @Schema(description = "시", example = "수원시", nullable = true)
        private String addressSi;

        @Size(min = 1, max = 20, message = "도/군은 1자 이상 20자 이하로 입력해야 합니다.")
        @Schema(description = "도/군", example = "경기도", nullable = true)
        private String addressDoGun;

        @Size(min = 1, max = 30, message = "구는 1자 이상 30자 이하로 입력해야 합니다.")
        @Schema(description = "구", example = "팔달구", nullable = true)
        private String addressGu;

        @Size(min = 1, max = 2048, message = "프로필 이미지 URL은 1자 이상 2048자 이하로 입력해야 합니다.")
        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png", nullable = true)
        private String profileUrl;
    }
}
