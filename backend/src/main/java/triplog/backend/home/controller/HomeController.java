package triplog.backend.home.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.home.dto.response.HomeResponse.HomeInfoResponse;
import triplog.backend.home.service.HomeFacadeService;

/**
 * 홈 화면 API 요청을 처리합니다.
 */
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@Tag(name = "Home API", description = "홈 화면 API")
public class HomeController {

    private final HomeFacadeService homeFacadeService;

    /**
     * 로그인 사용자의 레벨·랭크·미션·카드·최근 지역 정보를 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @return 홈 화면 정보
     */
    @GetMapping
    @Operation(summary = "홈 정보 조회", description = "로그인 사용자의 홈 화면 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "홈 정보 조회 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HomeInfoResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 또는 통계 정보를 찾을 수 없음",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<HomeInfoResponse> getHomeInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(homeFacadeService.getHomeInfo(userDetails.getUsername()));
    }
}
