package triplog.backend.stats.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.stats.dto.response.StatsResponse.MyRankingResponse;
import triplog.backend.stats.service.StatsService;

/**
 * 사용자 통계(Stats)와 관련된 API 요청을 처리하는 Controller입니다.
 * <p>
 * 사용자 통계 정보 조회 및 수정, 점수 및 레벨 관리 등
 * Stats 도메인과 관련된 HTTP 요청을 처리합니다.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Stats API", description = "사용자 통계 API")
@RequestMapping("/stats")
@Slf4j
public class StatsController {

    private final StatsService statsService;

    /**
     * 로그인 사용자의 전체 및 월간 랭킹 정보를 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @return 내 랭킹 정보
     */
    @GetMapping("/rankings/me")
    @Operation(summary = "내 랭킹 조회", description = "로그인 사용자의 전체 및 월간 랭킹 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 랭킹 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MyRankingResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nickname": "여행자",
                                      "profileUrl": "https://cdn.triplog.com/profiles/user-001.png",
                                      "totalRank": 120,
                                      "monthlyRank": 34,
                                      "overallScore": 1250,
                                      "monthScore": 220,
                                      "level": 3,
                                      "tier": "BRONZE",
                                      "nextTier": "SILVER",
                                      "requiredScore": 500
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
            @ApiResponse(responseCode = "404", description = "내 랭킹 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"내 랭킹 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<MyRankingResponse> getMyRanking(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(statsService.getMyRanking(userDetails.getUsername()));
    }
}
