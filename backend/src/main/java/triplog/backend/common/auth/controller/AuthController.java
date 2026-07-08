package triplog.backend.common.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import triplog.backend.common.auth.service.AuthService;

/**
 * 인증(Auth) 관련 API 요청을 처리하는 Controller입니다.
 * <p>
 * 자체 로그인, 소셜 로그인, 토큰 재발급, 로그아웃 요청의 HTTP 진입점을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth API", description = "인증/인가 API")
@Slf4j
public class AuthController {

    private final AuthService authService;
}
