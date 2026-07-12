package triplog.backend.common.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.auth.dto.request.AuthRequest;
import triplog.backend.common.auth.dto.request.AuthRequest.LoginRequest;
import triplog.backend.common.auth.dto.response.AuthResponse;
import triplog.backend.common.auth.service.AuthService;
import triplog.backend.common.exception.ErrorResponse;

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

    /**
     * 소셜 로그인 및 로컬 로그인 요청을 처리합니다.
     *
     * @param request 로그인 요청 DTO
     * @return 로그인 성공 또는 추가 정보 입력용 임시 토큰 응답
     */
    @Operation(
            summary = "소셜 로그인 및 로컬 로그인",
            description = "로그인 제공자(provider)에 따라 소셜 로그인 또는 로컬 로그인을 처리하고, 기존 회원 로그인 또는 추가 정보 입력용 임시 토큰 발급을 처리합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공 또는 추가 정보 입력 필요",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    oneOf = {
                                            AuthResponse.LoginSuccessResponse.class,
                                            AuthResponse.TemporaryTokenResponse.class
                                    }
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "로그인 성공",
                                            value = "{\"usersId\": \"550e8400-e29b-41d4-a716-446655440000\", \"nickname\": \"여행자\", \"level\": 1, \"xp\": 0, \"tier\": \"BRONZE\", \"accessToken\": \"access-token\", \"refreshToken\": \"refresh-token\"}"
                                    ),
                                    @ExampleObject(
                                            name = "추가 정보 입력 필요",
                                            value = "{\"expiresIn\": 300, \"temporaryToken\": \"temporary-token\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 로그인 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "인가 코드 누락", value = "{\"status\": 400, \"message\": \"소셜 로그인 인가 코드는 필수입니다.\"}"),
                                    @ExampleObject(name = "지원하지 않는 로그인 방식", value = "{\"status\": 400, \"message\": \"지원하지 않는 로그인 방식입니다.\"}")
                            })),
            @ApiResponse(responseCode = "401", description = "Google 인증 처리에 실패했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "Google 토큰 발급 실패", value = "{\"status\": 401, \"message\": \"Google 토큰 발급에 실패했습니다.\"}"),
                                    @ExampleObject(name = "Google 토큰 응답 오류", value = "{\"status\": 401, \"message\": \"Google 토큰 응답이 올바르지 않습니다.\"}"),
                                    @ExampleObject(name = "Google ID Token 오류", value = "{\"status\": 401, \"message\": \"Google ID Token이 올바르지 않습니다.\"}"),
                                    @ExampleObject(name = "Google 이메일 없음", value = "{\"status\": 401, \"message\": \"Google 계정 이메일을 찾을 수 없습니다.\"}")
                            })),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "500 Internal Server Error", value = "{\"status\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\"}")))
    })
    @PostMapping("/oauth")
    public ResponseEntity<AuthResponse.LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("로그인 요청 수신: provider={}", request.getProvider());
        return ResponseEntity.ok(authService.login(request));
    }
}
