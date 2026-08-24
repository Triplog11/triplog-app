package triplog.backend.appellation.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.appellation.dto.response.AppellationResponse.RepresentativeResponse;
import triplog.backend.appellation.dto.response.AppellationResponse.AcquiredListResponse;
import triplog.backend.appellation.service.AppellationService;
import triplog.backend.common.exception.ErrorResponse;

/**
 * 칭호 조회와 대표 칭호 변경 요청을 처리합니다.
 */
@RestController
@RequestMapping("/appellations")
@RequiredArgsConstructor
@Tag(name = "Appellation API", description = "칭호 API")
public class AppellationController {

    private final AppellationService appellationService;

    /**
     * 로그인 사용자가 획득한 칭호를 모두 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @return 획득 칭호 목록
     */
    @GetMapping
    @Operation(
            summary = "획득 칭호 목록 조회",
            description = "로그인 사용자가 획득한 칭호를 대표 칭호 우선으로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "획득 칭호 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AcquiredListResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "totalElements": 2,
                                      "items": [
                                        {
                                          "appellationId": 2,
                                          "appellationName": "랜드마크 탐험가",
                                          "representative": true
                                        },
                                        {
                                          "appellationId": 1,
                                          "appellationName": "여행의 시작",
                                          "representative": false
                                        }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AcquiredListResponse> getAcquiredAppellations(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                appellationService.getAcquiredAppellations(userDetails.getUsername())
        );
    }

    /**
     * 로그인 사용자가 획득한 칭호를 대표 칭호로 지정합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param appellationId 대표로 지정할 칭호 식별자
     * @return 지정된 대표 칭호 정보
     */
    @PatchMapping("/{appellationId}/representative")
    @Operation(
            summary = "대표 칭호 변경",
            description = "로그인 사용자가 획득한 칭호 중 하나를 대표 칭호로 지정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대표 칭호 변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RepresentativeResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "appellationId": 2,
                                      "appellationName": "랜드마크 탐험가",
                                      "representative": true
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "획득한 칭호 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value =
                                    "{\"status\":404,\"message\":\"획득한 칭호를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RepresentativeResponse> changeRepresentativeAppellation(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "대표로 지정할 칭호 ID", required = true, example = "2")
            @PathVariable Long appellationId
    ) {
        return ResponseEntity.ok(
                appellationService.changeRepresentativeAppellation(
                        userDetails.getUsername(), appellationId
                )
        );
    }
}
