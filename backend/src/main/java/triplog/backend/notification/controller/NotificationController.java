package triplog.backend.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.notification.dto.response.NotificationResponse.ReadResponse;
import triplog.backend.notification.service.NotificationService;

/**
 * 알림 관련 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Tag(name = "Notification API", description = "알림 API")
public class NotificationController {

    /**
     * 알림 비즈니스 로직을 처리하는 서비스입니다.
     */
    private final NotificationService notificationService;

    /**
     * 로그인 사용자가 소유한 알림을 읽음 상태로 변경합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param notificationId 읽음 처리할 알림 식별자
     * @return 알림 식별자와 읽음 여부 및 읽은 일시
     */
    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "로그인 사용자가 소유한 알림을 읽음 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReadResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"notificationId\":101,\"isRead\":true,"
                                            + "\"readAt\":\"2026-06-25T10:40:00\"}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":400,\"message\":\"잘못된 요청입니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요한 기능입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":401,\"message\":\"로그인이 필요한 기능입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "알림 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"알림 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 읽은 알림입니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":409,\"message\":\"이미 읽은 알림입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<ReadResponse> read(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId
    ) {
        return ResponseEntity.ok(notificationService.read(userDetails.getUsername(), notificationId));
    }
}
