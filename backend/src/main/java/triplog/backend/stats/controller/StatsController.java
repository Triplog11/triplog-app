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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.stats.dto.response.StatsResponse.MyRankingResponse;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;
import triplog.backend.stats.dto.response.StatsResponse.RankingListResponse;
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
                                      "tier": "SILVER",
                                      "nextTier": "GOLD",
                                      "requiredScore": 1500
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

    /**
     * 전체 랭킹을 페이지 단위로 조회합니다.
     *
     * @param rankingType 랭킹 타입 (TOTAL, MONTHLY)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 전체 랭킹 목록
     */
    @GetMapping("/rankings")
    @Operation(summary = "전체 랭킹 조회", description = "전체 랭킹을 페이지 단위로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 랭킹 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RankingListResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "rankingType": "MONTHLY",
                                      "page": 0,
                                      "size": 10,
                                      "totalElements": 1200,
                                      "totalPages": 120,
                                      "rankings": [
                                        {
                                          "rank": 1,
                                          "usersId": "550e8400-e29b-41d4-a716-446655440000",
                                          "nickname": "여행자",
                                          "profileUrl": "https://cdn.triplog.com/profiles/user-001.png",
                                          "score": 3200,
                                          "level": 7,
                                          "tier": "GOLD"
                                        }
                                      ]
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
            @ApiResponse(responseCode = "404", description = "전체 랭킹 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"전체 랭킹 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<RankingListResponse> getRankings(
            @Parameter(description = "랭킹 타입 (TOTAL, MONTHLY)", example = "MONTHLY")
            @RequestParam(defaultValue = "MONTHLY") String rankingType,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(statsService.getRankings(rankingType, page, size));
    }

    /**
     * 로그인 사용자의 스탯 정보를 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @return 내 스탯 정보
     */
    @GetMapping("/me")
    @Operation(summary = "내 스탯 정보 조회", description = "로그인 사용자의 스탯 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 스탯 정보 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MyStatsResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "level": 3,
                                      "xp": 240,
                                      "currentTier": "SILVER",
                                      "overallScore": 1250,
                                      "monthScore": 220,
                                      "nextLevel": 4,
                                      "requiredXp": 300,
                                      "remainingXp": 60,
                                      "nextTier": "GOLD",
                                      "requiredScore": 1500
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
            @ApiResponse(responseCode = "404", description = "내 스탯 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"내 스탯 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<MyStatsResponse> getMyStats(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(statsService.getMyStats(userDetails.getUsername()));
    }
}
