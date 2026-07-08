package triplog.backend.common.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * {@link AuthService}의 구현 클래스입니다.
 * <p>
 * 요청의 로그인 제공자(provider)에 따라 자체 로그인 또는 소셜 로그인 흐름으로 분기하고,
 * 인증 성공 후 JWT 발급 및 Refresh Token 저장 흐름을 조율합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
}
