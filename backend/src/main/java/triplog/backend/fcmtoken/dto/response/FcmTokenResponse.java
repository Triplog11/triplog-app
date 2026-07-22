package triplog.backend.fcmtoken.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * FCM 푸시 토큰 관련 응답 DTO를 그룹화하는 클래스입니다.
 * <p>
 * 서비스 계층에서 처리한 FCM 토큰 등록, 조회, 수정, 삭제 결과를 클라이언트에 반환할 때 사용합니다.
 */
@Schema(description = "FCM 푸시 토큰 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmTokenResponse {
}
