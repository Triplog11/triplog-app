package triplog.backend.fcmtoken.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * FCM 푸시 토큰 관련 요청 DTO를 그룹화하는 클래스입니다.
 * <p>
 * 클라이언트로부터 전달받은 FCM 토큰, 디바이스 유형, 디바이스 이름 정보를 서비스 계층으로 전달할 때 사용합니다.
 */
@Schema(description = "FCM 푸시 토큰 관련 요청 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmTokenRequest {
}
