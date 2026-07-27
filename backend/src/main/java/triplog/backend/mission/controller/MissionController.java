package triplog.backend.mission.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.mission.dto.response.MissionResponse.MissionListResponse;
import triplog.backend.mission.dto.response.MissionResponse.MyMissionListResponse;
import triplog.backend.mission.service.MissionService;

/**
 * 미션(Mission)과 관련된 API 요청을 처리하는 Controller입니다.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Mission API", description = "미션 API")
@RequestMapping("/missions")
public class MissionController {

    private final MissionService missionService;

    /**
     * 로그인 사용자의 미션 진행 현황을 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param missionType 미션 타입
     * @return 미션 진행 현황 목록
     */
    @GetMapping("/me")
    @Operation(summary = "내 미션 진행 현황 조회", description = "로그인 사용자의 미션 진행 현황을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 미션 진행 현황 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MyMissionListResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "missions": [
                                        {
                                          "missionId": 10,
                                          "missionName": "이번 주 지역 3곳 방문",
                                          "missionType": "WEEKLY",
                                          "missionTarget": "REGION_VISIT",
                                          "missionCondition": null,
                                          "weekStart": "2026-06-22T00:00:00",
                                          "weekEnd": "2026-06-28T23:59:59",
                                          "rewardScore": 100,
                                          "rewardXp": 150,
                                          "completed": false,
                                          "completedAt": null
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
            @ApiResponse(responseCode = "404", description = "미션 진행 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"미션 진행 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<MyMissionListResponse> getMyMissions(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "미션 타입 (WEEKLY, DAILY), 미입력 시 전체 조회", example = "WEEKLY")
            @RequestParam(required = false) String missionType
    ) {
        return ResponseEntity.ok(missionService.getMyMissions(userDetails.getUsername(), missionType));
    }

    /**
     * 미션 타입별 미션 목록을 조회합니다.
     *
     * @param missionType 미션 타입
     * @return 미션 목록
     */
    @GetMapping
    @Operation(summary = "미션 목록 조회", description = "미션 타입별 미션 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 목록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MissionListResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "missions": [
                                        {
                                          "missionId": 10,
                                          "missionName": "이번 주 지역 3곳 방문",
                                          "missionType": "WEEKLY",
                                          "missionTarget": "REGION_VISIT",
                                          "missionCondition": "주간 내 랜드마크 1곳 인증",
                                          "weekStart": "2026-06-22T00:00:00",
                                          "weekEnd": "2026-06-28T23:59:59",
                                          "rewardScore": 100,
                                          "rewardXp": 150
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
            @ApiResponse(responseCode = "404", description = "미션 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"미션 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<MissionListResponse> getMissions(
            @Parameter(description = "미션 타입 (WEEKLY, DAILY), 미입력 시 전체 조회", example = "WEEKLY")
            @RequestParam(required = false) String missionType
    ) {
        return ResponseEntity.ok(missionService.getMissions(missionType));
    }
}
