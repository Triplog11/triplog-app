package triplog.backend.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자(Users) API 응답 DTO를 그룹화하는 클래스입니다.
 * <p>
 * 사용자 정보 조회, 변경, 중복 확인 등 사용자 도메인에서 반환하는 응답 DTO를
 * 내부 정적 클래스로 정의합니다.
 */
@Schema(description = "사용자 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsersResponse {

    /**
     * 닉네임 중복 확인 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "닉네임 중복 확인 응답")
    public static class NicknameCheckResponse {

        @Schema(description = "사용 가능 여부", example = "true")
        private Boolean available;

        @Schema(description = "결과 메시지", example = "사용 가능한 닉네임입니다.")
        private String message;

        /**
         * 닉네임 사용 가능 여부를 기반으로 응답 DTO를 생성합니다.
         *
         * @param available 닉네임 사용 가능 여부
         * @return 닉네임 중복 확인 응답 DTO
         */
        public static NicknameCheckResponse toDto(Boolean available) {
            String message = available ? "사용 가능한 닉네임입니다." : "중복된 닉네임입니다.";
            return new NicknameCheckResponse(available, message);
        }
    }

    /**
     * 프로필 수정 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "프로필 수정 응답")
    public static class ProfileUpdateResponse {

        @Schema(description = "유저 식별자", example = "550e8400-e29b-41d4-a716-446655440000")
        private String usersId;

        @Schema(description = "닉네임", example = "여행자")
        private String nickname;

        @Schema(description = "시", example = "수원시")
        private String addressSi;

        @Schema(description = "도/군", example = "경기도")
        private String addressDoGun;

        @Schema(description = "구", example = "팔달구")
        private String addressGu;

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png", nullable = true)
        private String profileUrl;
    }
  
   /**
     * 이메일 중복 확인 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "이메일 중복 확인 응답")
    public static class EmailCheckResponse {

        @Schema(description = "사용 가능 여부", example = "true")
        private Boolean available;

        @Schema(description = "결과 메시지", example = "사용 가능한 이메일입니다.")
        private String message;

        /**
         * 이메일 사용 가능 여부를 기반으로 응답 DTO를 생성합니다.
         *
         * @param available 이메일 사용 가능 여부
         * @return 이메일 중복 확인 응답 DTO
         */
        public static EmailCheckResponse toDto(Boolean available) {
            String message = available ? "사용 가능한 이메일입니다." : "중복된 이메일입니다.";
            return new EmailCheckResponse(available, message);
        }
    }
}

