package triplog.backend.landmark.controller;

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
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.landmark.dto.response.LandmarkResponse.LandmarkDetailResponse;
import triplog.backend.landmark.dto.response.LandmarkResponse.ObtainedCardListResponse;
import triplog.backend.landmark.exception.LandmarkErrorCode;
import triplog.backend.landmark.exception.LandmarkException;
import triplog.backend.landmark.service.LandmarkService;

/**
 * 랜드마크(Landmark) 관련 API 요청을 처리하는 REST Controller입니다.
 */
@RestController
@RequestMapping("/landmarks")
@RequiredArgsConstructor
@Tag(name = "Landmark API", description = "랜드마크 API")
public class LandmarkController {

    private final LandmarkService landmarkService;

    /**
     * 로그인 사용자가 획득한 카드 목록을 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 획득 카드 목록 응답
     */
    @GetMapping("/me")
    @Operation(summary = "내가 획득한 카드 전체 조회", description = "로그인 사용자가 획득한 카드를 최신 획득순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "획득 카드 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ObtainedCardListResponse.class))),
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
    public ResponseEntity<ObtainedCardListResponse> getObtainedCards(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        if (page < 0 || size < 1) {
            throw new LandmarkException(LandmarkErrorCode.INVALID_PAGE_REQUEST);
        }
        return ResponseEntity.ok(landmarkService.getObtainedCards(
                userDetails.getUsername(),
                PageRequest.of(page, size)
        ));
    }

    /**
     * 랜드마크 상세 정보를 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param landmarkId  랜드마크 ID
     * @return 랜드마크 상세 응답
     */
    @GetMapping("/{landmarkId}")
    @Operation(summary = "랜드마크 상세 조회", description = "로그인 사용자의 특정 랜드마크 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "랜드마크 상세 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LandmarkDetailResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "landmarkId": 301,
                                      "landmarkName": "수원화성",
                                      "regionId": 101,
                                      "regionName": "수원시",
                                      "contentId": "TOUR-10001",
                                      "legalRegionCode": "41",
                                      "legalDistrictCode": "110",
                                      "cardName": "수원 화성",
                                      "cardTier": "RARE",
                                      "cardUrl": "https://res.cloudinary.com/demo/image/upload/125555.png",
                                      "acquired": true,
                                      "acquiredAt": "2026-06-20T14:30:00",
                                      "visitCount": 2
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
            @ApiResponse(responseCode = "404", description = "랜드마크 상세 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"랜드마크 상세 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<LandmarkDetailResponse> getLandmarkDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "랜드마크 ID", required = true, example = "301")
            @PathVariable Long landmarkId
    ) {
        return ResponseEntity.ok(landmarkService.getLandmarkDetail(userDetails.getUsername(), landmarkId));
    }
}
