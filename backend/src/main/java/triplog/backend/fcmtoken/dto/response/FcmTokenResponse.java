package triplog.backend.fcmtoken.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FCM 푸시 토큰 관련 응답 DTO를 그룹화하는 클래스입니다.
 * <p>
 * 서비스 계층에서 처리한 FCM 토큰 등록, 조회, 수정, 삭제 결과를 클라이언트에 반환할 때 사용합니다.
 */
@Schema(description = "FCM 푸시 토큰 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmTokenResponse {

    /**
     * FCM 푸시 토큰 등록 결과를 반환하는 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "FCM 푸시 토큰 등록 응답")
    public static class RegisterResponse {

        @Schema(description = "등록 여부", example = "true")
        private Boolean isRegistered;

        /**
         * 푸시 토큰 등록 여부를 기반으로 응답 DTO를 생성합니다.
         *
         * @param isRegistered 푸시 토큰 등록 여부
         * @return 푸시 토큰 등록 응답 DTO
         */
        public static RegisterResponse toDto(Boolean isRegistered) {
            return new RegisterResponse(isRegistered);
        }
    }

    /**
     * FCM 푸시 토큰 삭제 결과를 반환하는 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "FCM 푸시 토큰 삭제 응답")
    public static class DeleteResponse {

        @Schema(description = "등록 여부", example = "false")
        private Boolean isRegistered;

        /**
         * 푸시 토큰 등록 여부를 기반으로 삭제 응답 DTO를 생성합니다.
         *
         * @param isRegistered 푸시 토큰 등록 여부
         * @return 푸시 토큰 삭제 응답 DTO
         */
        public static DeleteResponse toDto(Boolean isRegistered) {
            return new DeleteResponse(isRegistered);
        }
    }
}
