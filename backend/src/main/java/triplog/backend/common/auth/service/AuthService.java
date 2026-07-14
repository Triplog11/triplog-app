package triplog.backend.common.auth.service;

import triplog.backend.common.auth.dto.request.AuthRequest.AdditionalInfoRequest;
import triplog.backend.common.auth.dto.request.AuthRequest.LoginRequest;
import triplog.backend.common.auth.dto.response.AuthResponse.LoginResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.LoginSuccessResponse;

/**
 * 인증(Auth) 도메인의 비즈니스 로직을 정의하는 Service 인터페이스입니다.
 * <p>
 * 로컬 로그인, 소셜 로그인, 추가정보 입력 같은 인증 흐름에서 필요한 기능을 선언합니다.
 */
public interface AuthService {

    /**
     * 로그인 요청을 처리합니다.
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 성공 또는 추가정보 입력용 임시 토큰 응답
     */
    LoginResponse login(LoginRequest request);

    /**
     * 회원가입용 임시 토큰에서 인증된 이메일과 추가정보를 기반으로 신규 사용자를 생성합니다.
     *
     * @param email 회원가입용 임시 토큰에서 인증된 이메일
     * @param temporaryToken 회원가입용 임시 토큰
     * @param request 추가정보 입력 요청 DTO
     * @return 회원가입 완료 후 로그인 성공 응답
     */
    LoginSuccessResponse addAdditionalInfo(String email, String temporaryToken, AdditionalInfoRequest request);
}