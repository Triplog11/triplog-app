package triplog.backend.fcmtoken.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FCM 푸시 토큰 관련 요청 DTO를 그룹화하는 클래스입니다.
 * <p>
 * 클라이언트로부터 전달받은 FCM 토큰, 디바이스 유형, 디바이스 이름 정보를 서비스 계층으로 전달할 때 사용합니다.
 */
@Schema(description = "FCM 푸시 토큰 관련 요청 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmTokenRequest {

    /**
     * FCM 푸시 토큰 등록 요청 DTO입니다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "FCM 푸시 토큰 등록 요청")
    public static class RegisterRequest {

        @NotBlank(message = "FCM 토큰은 필수입니다.")
        @Size(max = 512, message = "FCM 토큰은 512자 이하여야 합니다.")
        @Schema(description = "FCM 토큰", example = "fcm_device_token_string")
        private String token;

        @NotBlank(message = "디바이스 타입은 필수입니다.")
        @Size(max = 50, message = "디바이스 타입은 50자 이하여야 합니다.")
        @Schema(description = "핸드폰 타입", example = "ANDROID")
        private String deviceType;

        @NotBlank(message = "디바이스 이름은 필수입니다.")
        @Size(max = 100, message = "디바이스 이름은 100자 이하여야 합니다.")
        @Schema(description = "디바이스 이름", example = "Galaxy Z Flip 5")
        private String deviceName;
    }
}
