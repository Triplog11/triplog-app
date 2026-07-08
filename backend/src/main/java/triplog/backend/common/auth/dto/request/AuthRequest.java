package triplog.backend.common.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 인증(Auth) API 요청 DTO를 그룹화하는 클래스입니다.
 * <p>
 * 로그인, 소셜 로그인, 토큰 재발급, 로그아웃 요청에서 사용하는 Request DTO를
 * 내부 정적 클래스로 정의합니다.
 */
@Schema(description = "인증 관련 요청 DTO 그룹")
public class AuthRequest {
}
