package triplog.backend.badge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.badge.dto.response.BadgeListResult;
import triplog.backend.badge.dto.response.BadgeResponse;
import triplog.backend.badge.service.BadgeService;
import triplog.backend.badge.exception.BadgeErrorCode;
import triplog.backend.badge.exception.BadgeException;
import triplog.backend.common.exception.ErrorResponse;

/**
 * 배지 목록 및 상세 조회 요청을 처리하는 REST Controller입니다.
 */
@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
@Tag(name = "Badge API", description = "배지 API")
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping("/{badgeId}")
    @Operation(summary = "배지 상세 조회", description = "배지 ID로 상세 정보와 로그인 사용자의 획득 상태를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배지 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BadgeResponse.BadgeDetailResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "badgeId": 1,
                                      "badgeName": "첫 발자국",
                                      "badgeUrl": "https://cdn.triplog.com/badges/first-step.png",
                                      "badgeGroup": 1,
                                      "badgeType": "REVIEW",
                                      "badgeTarget": "REVIEW_COUNT",
                                      "badgeOperator": ">=",
                                      "badgeValue": 1,
                                      "acquired": true,
                                      "representative": false
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"message\":\"잘못된 요청입니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":401,\"message\":\"로그인이 필요합니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "배지 상세 정보 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"뱃지 상세 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<BadgeResponse.BadgeDetailResponse> getBadgeDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "조회할 배지 ID", required = true, example = "1")
            @PathVariable Long badgeId) {
        return ResponseEntity.ok(badgeService.getBadgeDetail(userDetails.getUsername(), badgeId));
    }

    @GetMapping
    @Operation(
            summary = "전체 배지 목록 및 획득 배지 목록 조회",
            description = "배지 타입과 획득 여부로 필터링한 배지 목록을 페이지 단위로 조회합니다. "
                    + "isAcquired가 true이면 획득 배지 전용 간소화 응답을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배지 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BadgeListResult.class),
                            examples = {
                                    @ExampleObject(name = "전체 배지 목록", value = """
                                            {
                                              "page": 0,
                                              "size": 10,
                                              "totalElements": 14,
                                              "totalPages": 2,
                                              "items": [{
                                                "badgeId": 1,
                                                "badgeName": "첫 발자국",
                                                "badgeUrl": "https://cdn.triplog.com/badges/first-step.png",
                                                "badgeType": "REVIEW",
                                                "badgeTarget": "REVIEW_COUNT",
                                                "badgeValue": 1,
                                                "acquired": true,
                                                "representative": false
                                              }]
                                            }
                                            """),
                                    @ExampleObject(name = "획득 배지 목록", value = """
                                            {
                                              "page": 0,
                                              "size": 10,
                                              "totalElements": 4,
                                              "totalPages": 1,
                                              "items": [{
                                                "badgeId": 1,
                                                "badgeName": "첫 발자국",
                                                "badgeUrl": "https://cdn.triplog.com/badges/first-step.png",
                                                "representative": false
                                              }]
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 페이지 값",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"message\":\"잘못된 요청입니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":401,\"message\":\"로그인이 필요합니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "요청 페이지가 조회 범위를 벗어남",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"뱃지 정보들을 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<BadgeListResult> getBadges(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "배지 타입", example = "REVIEW")
            @RequestParam(required = false) String badgeType,
            @Parameter(description = "획득 여부", example = "true")
            @RequestParam(required = false) Boolean isAcquired,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        if (page < 0 || size < 1) {
            throw new BadgeException(BadgeErrorCode.INVALID_PAGE_REQUEST);
        }
        return ResponseEntity.ok(badgeService.getBadges(
                userDetails.getUsername(), badgeType, isAcquired, PageRequest.of(page, size)));
    }
}
