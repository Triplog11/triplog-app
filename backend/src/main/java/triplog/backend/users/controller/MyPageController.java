package triplog.backend.users.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.users.dto.response.MyPageResponse.ActivityHistoryResponse;
import triplog.backend.users.exception.UsersErrorCode;
import triplog.backend.users.exception.UsersException;
import triplog.backend.users.service.ActivityHistoryFacadeService;

/**
 * 마이페이지의 활동 관련 API 요청을 처리합니다.
 */
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Tag(name = "MyPage API", description = "마이페이지 API")
public class MyPageController {

    private final ActivityHistoryFacadeService activityHistoryFacadeService;

    /**
     * 로그인 사용자의 활동 히스토리를 페이지 단위로 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 활동 히스토리 목록
     */
    @GetMapping("/activityhistory")
    @Operation(summary = "활동 히스토리 조회", description = "내 활동 히스토리 목록을 최신순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "활동 히스토리 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityHistoryResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "page": 0,
                                      "size": 10,
                                      "totalElements": 24,
                                      "totalPages": 3,
                                      "activities": [
                                        {
                                          "activityId": 101,
                                          "activityType": "BADGE",
                                          "title": "여행 입문자 뱃지 획득",
                                          "content": "첫 방문 인증을 완료했습니다.",
                                          "score": 100,
                                          "xp": 30,
                                          "createdAt": "2026-08-23T14:30:00"
                                        }
                                      ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 페이지 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ActivityHistoryResponse> getActivityHistory(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1) {
            throw new UsersException(UsersErrorCode.INVALID_PAGE_REQUEST);
        }
        return ResponseEntity.ok(
                activityHistoryFacadeService.getActivityHistory(
                        userDetails.getUsername(),
                        PageRequest.of(page, size)
                )
        );
    }
}
