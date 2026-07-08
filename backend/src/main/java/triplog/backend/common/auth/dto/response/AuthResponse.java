package triplog.backend.common.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 인증(Auth) API 응답 DTO를 그룹화하는 클래스입니다.
 * <p>
 * 로그인 성공, 회원가입 분기, 토큰 재발급 등 인증 흐름에서 클라이언트에 반환하는
 * Response DTO를 내부 정적 클래스로 정의합니다.
 */
@Schema(description = "인증 관련 응답 DTO 그룹")
public class AuthResponse {
}
