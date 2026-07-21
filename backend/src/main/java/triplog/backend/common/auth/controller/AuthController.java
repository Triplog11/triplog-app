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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.auth.dto.request.AuthRequest.AdditionalInfoRequest;
import triplog.backend.common.auth.dto.request.AuthRequest.LoginRequest;
import triplog.backend.common.auth.dto.request.AuthRequest.SignupRequest;
import triplog.backend.common.auth.dto.response.AuthResponse.LoginResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.LoginSuccessResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.SignupResponse;
import triplog.backend.common.auth.dto.response.AuthResponse.TemporaryTokenResponse;
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
                                            LoginSuccessResponse.class,
                                            TemporaryTokenResponse.class
                                    }
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "로그인 성공",
                                            value = "{\"nickname\": \"여행자\", \"level\": 1, \"xp\": 0, \"tier\": \"BRONZE\", \"accessToken\": \"access-token\", \"refreshToken\": \"refresh-token\"}"
                                    ),
                                    @ExampleObject(
                                            name = "추가 정보 입력 필요",
                                            value = "{\"expiresIn\": 300, \"temporaryToken\": \"temporary-token\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "소셜 인가 코드 누락", value = "{\"status\": 400, \"message\": \"소셜 로그인 인가 코드는 필수입니다.\"}"),
                                    @ExampleObject(name = "Naver state 누락", value = "{\"status\": 400, \"message\": \"Naver 로그인 state 값은 필수입니다.\"}"),
                                    @ExampleObject(name = "자체 로그인 이메일 누락", value = "{\"status\": 400, \"message\": \"자체 로그인 이메일은 필수입니다.\"}"),
                                    @ExampleObject(name = "자체 로그인 비밀번호 누락", value = "{\"status\": 400, \"message\": \"자체 로그인 비밀번호는 필수입니다.\"}"),
                                    @ExampleObject(name = "지원하지 않는 로그인 방식", value = "{\"status\": 400, \"message\": \"지원하지 않는 로그인 방식입니다.\"}")
                            })),
            @ApiResponse(responseCode = "401", description = "로그인 인증 처리에 실패했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "Google 토큰 발급 실패", value = "{\"status\": 401, \"message\": \"Google 토큰 발급에 실패했습니다.\"}"),
                                    @ExampleObject(name = "Google 토큰 응답 오류", value = "{\"status\": 401, \"message\": \"Google 토큰 응답이 올바르지 않습니다.\"}"),
                                    @ExampleObject(name = "Google ID Token 오류", value = "{\"status\": 401, \"message\": \"Google ID Token이 올바르지 않습니다.\"}"),
                                    @ExampleObject(name = "Google 이메일 없음", value = "{\"status\": 401, \"message\": \"Google 계정 이메일을 찾을 수 없습니다.\"}"),
                                    @ExampleObject(name = "Naver 토큰 발급 실패", value = "{\"status\": 401, \"message\": \"Naver 토큰 발급에 실패했습니다.\"}"),
                                    @ExampleObject(name = "Naver 토큰 응답 오류", value = "{\"status\": 401, \"message\": \"Naver 토큰 응답이 올바르지 않습니다.\"}"),
                                    @ExampleObject(name = "Naver 사용자 정보 조회 실패", value = "{\"status\": 401, \"message\": \"Naver 사용자 정보 조회에 실패했습니다.\"}"),
                                    @ExampleObject(name = "Naver 이메일 없음", value = "{\"status\": 401, \"message\": \"Naver 계정 이메일을 찾을 수 없습니다.\"}"),
                                    @ExampleObject(name = "자체 로그인 인증 실패", value = "{\"status\": 401, \"message\": \"이메일 또는 비밀번호가 올바르지 않습니다.\"}")
                            })),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\"}")))
    })
    @PostMapping("/oauth")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("로그인 요청 수신: provider={}", request.getProvider());
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * 로컬 회원가입 요청을 처리합니다.
     *
     * @param request 로컬 회원가입 요청 DTO
     * @return 회원가입 완료 여부 응답
     */
    @Operation(
            summary = "로컬 회원가입",
            description = "이메일과 비밀번호, 프로필 정보, 주소 정보를 기반으로 로컬 사용자를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입이 완료되었습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SignupResponse.class),
                            examples = @ExampleObject(value = "{\"isRegister\": true}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\": 400, \"message\": \"잘못된 요청입니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "회원가입 중복 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "이메일 중복", value = "{\"status\": 409, \"message\": \"이미 회원가입이 완료된 이메일입니다.\"}"),
                                    @ExampleObject(name = "닉네임 중복", value = "{\"status\": 409, \"message\": \"이미 사용 중인 닉네임입니다.\"}")
                            })),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\": 500, \"message\": \"서버 내부 오류가 발생했습니다.\"}")))
    })
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        return ResponseEntity.ok(authService.signup(request));
    }

    /**
     * 소셜 신규 회원의 추가정보 입력 요청을 처리합니다.
     *     * @param request 추가정보 입력 요청 DTO
     * @return 회원가입 완료 후 로그인 성공 응답
     */
    @Operation(
            summary = "추가정보 입력",
            description = "회원가입용 임시 토큰에서 이메일과 로그인 타입을 추출하고, 입력받은 추가정보로 사용자와 초기 통계를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가정보 입력에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginSuccessResponse.class),
                            examples = @ExampleObject(value = "{\"nickname\":\"여행자\",\"level\":1,\"xp\":0,\"tier\":\"BRONZE\",\"accessToken\":\"access-token\",\"refreshToken\":\"refresh-token\"}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "닉네임 누락", value = "{\"status\":400,\"message\":\"닉네임은 필수입니다.\"}"),
                                    @ExampleObject(name = "닉네임 길이 오류", value = "{\"status\":400,\"message\":\"닉네임은 2자 이상 12자 이하로 입력해야 합니다.\"}"),
                                    @ExampleObject(name = "시 누락", value = "{\"status\":400,\"message\":\"시는 필수입니다.\"}"),
                                    @ExampleObject(name = "도/군 누락", value = "{\"status\":400,\"message\":\"도/군은 필수입니다.\"}"),
                                    @ExampleObject(name = "구 누락", value = "{\"status\":400,\"message\":\"구는 필수입니다.\"}")
                            })),
            @ApiResponse(responseCode = "401", description = "회원가입용 임시 토큰이 유효하지 않습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":401,\"message\":\"유효하지 않은 회원가입용 임시 토큰입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 통계 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"사용자 또는 통계 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    @PostMapping("/additional-info")
    public ResponseEntity<LoginSuccessResponse> addAdditionalInfo(
            Authentication authentication,
            @Valid @RequestBody AdditionalInfoRequest request
    ) {
        String email = authentication.getName();
        String temporaryToken = authentication.getCredentials().toString();
        return ResponseEntity.ok(authService.addAdditionalInfo(email, temporaryToken, request));
    }
}
