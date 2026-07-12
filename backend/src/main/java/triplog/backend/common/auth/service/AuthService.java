package triplog.backend.common.auth.service;

import org.springframework.http.ResponseEntity;
import triplog.backend.common.auth.dto.request.AuthRequest;
import triplog.backend.common.auth.dto.request.AuthRequest.LoginRequest;
import triplog.backend.common.auth.dto.response.AuthResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.LoginResponse;

/**
 * 인증(Auth) 도메인의 비즈니스 로직을 정의하는 Service 인터페이스입니다.
 * <p>
 * 자체 로그인, 소셜 로그인, 토큰 재발급, 로그아웃처럼 인증 흐름에서 필요한 기능의 계약을 선언합니다.
 * 실제 구현은 {@link AuthServiceImpl}에서 담당합니다.
 */
public interface AuthService {

    /**
     * 로그인 요청을 처리합니다.
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 성공 또는 추가 정보 입력용 임시 토큰 응답
     */
    LoginResponse login(LoginRequest request);
}
