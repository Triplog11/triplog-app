package triplog.backend.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.notification.dto.response.NotificationResponse.ListResponse;
import triplog.backend.notification.dto.response.NotificationResponse.ReadResponse;
import triplog.backend.notification.dto.response.NotificationResponse.SettingsResponse;
import triplog.backend.notification.dto.request.NotificationRequest.SettingsUpdateRequest;
import triplog.backend.notification.exception.NotificationException;
import triplog.backend.notification.service.NotificationService;
import static triplog.backend.notification.exception.NotificationErrorCode.INVALID_PAGE_REQUEST;

/**
 * 알림 관련 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Tag(name = "Notification API", description = "알림 API")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림 정책별 활성화 상태를 조회합니다.
     *
     * @return 알림 설정 조회 응답
     */
    @GetMapping("/settings")
    @Operation(summary = "알림 설정 조회", description = "알림 정책별 활성화 여부를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 설정 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SettingsResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "isLevelUp": true,
                                      "isRankUp": true,
                                      "isBadgeAcquired": false,
                                      "isCardAcquired": true,
                                      "isRegionCompleted": true,
                                      "isLandmarkVerified": false,
                                      "isWeeklyMissonCompleted": true
                                    }
                                    """))),
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
            @ApiResponse(responseCode = "404", description = "알림 설정 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"알림 설정 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<SettingsResponse> getSettings() {
        return ResponseEntity.ok(notificationService.getSettings());
    }

    /**
     * 알림 정책별 활성화 상태를 수정합니다.
     *
     * @param request 알림 설정 수정 요청
     * @return 수정된 알림 설정
     */
    @PatchMapping("/settings")
    @Operation(summary = "알림 설정 수정", description = "알림 정책별 활성화 여부를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 설정 수정에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SettingsResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "isLevelUp": true,
                                      "isRankUp": true,
                                      "isBadgeAcquired": false,
                                      "isCardAcquired": true,
                                      "isRegionCompleted": true,
                                      "isLandmarkVerified": false,
                                      "isWeeklyMissonCompleted": true
                                    }
                                    """))),
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
            @ApiResponse(responseCode = "404", description = "알림 설정 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"알림 설정 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<SettingsResponse> updateSettings(
            @Valid @RequestBody SettingsUpdateRequest request
    ) {
        return ResponseEntity.ok(notificationService.updateSettings(request));
    }

    /**
     * 로그인 사용자의 알림 목록을 페이지 단위로 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param unreadOnly 읽지 않은 알림만 조회할지 여부
     * @return 페이징된 알림 목록
     */
    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "로그인 사용자의 알림 목록을 최신순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 목록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "page": 0,
                                      "size": 10,
                                      "totalElements": 25,
                                      "totalPages": 3,
                                      "notifications": [{
                                        "notificationId": 101,
                                        "title": "미션 완료",
                                        "content": "일일 미션을 완료했습니다.",
                                        "notificationType": "MISSION",
                                        "isRead": false,
                                        "createdAt": "2026-06-25T10:30:00"
                                      }]
                                    }
                                    """))),
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
            @ApiResponse(responseCode = "404", description = "알림 정보들을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"알림 정보들을 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<ListResponse> getNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "읽지 않은 알림만 조회할지 여부", example = "false")
            @RequestParam(defaultValue = "false") boolean unreadOnly
    ) {
        if (page < 0 || size < 1) {
            throw new NotificationException(INVALID_PAGE_REQUEST);
        }
        return ResponseEntity.ok(notificationService.getNotifications(
                userDetails.getUsername(),
                unreadOnly,
                PageRequest.of(page, size)
        ));
    }

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
