package triplog.backend.fcmtoken.controller;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.fcmtoken.dto.request.FcmTokenRequest.DeleteRequest;
import triplog.backend.fcmtoken.dto.request.FcmTokenRequest.RegisterRequest;
import triplog.backend.fcmtoken.dto.response.FcmTokenResponse.DeleteResponse;
import triplog.backend.fcmtoken.dto.response.FcmTokenResponse.RegisterResponse;
import triplog.backend.fcmtoken.service.FcmTokenService;

/**
 * FCM 푸시 토큰과 관련된 API 요청을 처리하는 Controller입니다.
 * <p>
 * 사용자 디바이스의 FCM 토큰 등록, 조회, 수정, 삭제와 관련된 HTTP 요청을 처리합니다.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "FCM Token API", description = "FCM 푸시 토큰 API")
@RequestMapping("/fcm-tokens")
@Slf4j
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    /**
     * 로그인한 사용자의 FCM 푸시 토큰 등록 요청을 처리합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param request FCM 푸시 토큰과 디바이스 정보
     * @return FCM 푸시 토큰 등록 결과
     */
    @PostMapping
    @Operation(summary = "푸시 토큰 등록", description = "로그인한 사용자의 FCM 푸시 토큰과 디바이스 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "푸시 토큰 등록에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterResponse.class),
                            examples = @ExampleObject(value = "{\"isRegistered\":true}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "FCM 토큰 누락", value = "{\"status\":400,\"message\":\"FCM 토큰은 필수입니다.\"}"),
                                    @ExampleObject(name = "FCM 토큰 길이 오류", value = "{\"status\":400,\"message\":\"FCM 토큰은 512자 이하여야 합니다.\"}"),
                                    @ExampleObject(name = "디바이스 타입 누락", value = "{\"status\":400,\"message\":\"디바이스 타입은 필수입니다.\"}"),
                                    @ExampleObject(name = "디바이스 타입 길이 오류", value = "{\"status\":400,\"message\":\"디바이스 타입은 50자 이하여야 합니다.\"}"),
                                    @ExampleObject(name = "디바이스 이름 누락", value = "{\"status\":400,\"message\":\"디바이스 이름은 필수입니다.\"}"),
                                    @ExampleObject(name = "디바이스 이름 길이 오류", value = "{\"status\":400,\"message\":\"디바이스 이름은 100자 이하여야 합니다.\"}")
                            })),
            @ApiResponse(responseCode = "401", description = "로그인이 필요한 기능입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":401,\"message\":\"로그인이 필요한 기능입니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 푸시 토큰입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":409,\"message\":\"이미 등록된 푸시 토큰입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<RegisterResponse> register(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(fcmTokenService.register(
                userDetails.getUsername(), request.getToken(), request.getDeviceType(), request.getDeviceName()));
    }

    /**
     * 로그인한 사용자의 FCM 푸시 토큰 삭제 요청을 처리합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param request 삭제할 FCM 푸시 토큰 정보
     * @return FCM 푸시 토큰 삭제 결과
     */
    @DeleteMapping
    @Operation(summary = "푸시 토큰 삭제", description = "로그인한 사용자가 등록한 FCM 푸시 토큰을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "푸시 토큰 삭제에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeleteResponse.class),
                            examples = @ExampleObject(value = "{\"isRegistered\":false}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "FCM 토큰 누락", value = "{\"status\":400,\"message\":\"FCM 토큰은 필수입니다.\"}"),
                                    @ExampleObject(name = "FCM 토큰 길이 오류", value = "{\"status\":400,\"message\":\"FCM 토큰은 512자 이하여야 합니다.\"}")
                            })),
            @ApiResponse(responseCode = "401", description = "로그인이 필요한 기능입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":401,\"message\":\"로그인이 필요한 기능입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "푸시 토큰 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"FCM 토큰 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<DeleteResponse> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DeleteRequest request) {
        return ResponseEntity.ok(fcmTokenService.delete(userDetails.getUsername(), request.getToken()));
    }
}
