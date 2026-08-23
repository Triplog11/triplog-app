package triplog.backend.review.controller;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.common.exception.ErrorResponse;
import triplog.backend.review.dto.request.ReviewRequest.CreateRequest;
import triplog.backend.review.dto.response.ReviewResponse.CreateReviewResponse;
import triplog.backend.review.dto.response.ReviewResponse.DetailResponse;
import triplog.backend.review.dto.response.ReviewResponse.ListResponse;
import triplog.backend.review.exception.ReviewErrorCode;
import triplog.backend.review.exception.ReviewException;
import triplog.backend.review.service.ReviewService;
import triplog.backend.review.service.ReviewFacadeService;

import java.util.List;

/**
 * 리뷰(Review) 관련 API 요청을 처리하는 REST Controller입니다.
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Review API", description = "방문 인증 리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewFacadeService reviewFacadeService;

    /**
     * 로그인 사용자가 작성한 방문 인증 상세 정보를 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param reviewId 방문 인증 리뷰 식별자
     * @return 방문 인증 상세 응답
     */
    @GetMapping("/{reviewId}/detail")
    @Operation(summary = "방문 인증 상세 조회 (리뷰)", description = "로그인 사용자가 작성한 방문 인증 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방문 인증 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 리뷰 ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "방문 인증을 찾을 수 없거나 조회 권한이 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DetailResponse> getReviewDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "방문 인증 리뷰 ID", example = "7001")
            @PathVariable Long reviewId
    ) {
        return ResponseEntity.ok(reviewService.getReviewDetail(userDetails.getUsername(), reviewId));
    }

    /**
     * 로그인 사용자의 방문 인증 목록을 조회합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 방문 인증 목록 응답
     */
    @GetMapping
    @Operation(summary = "방문 인증 목록 조회 (리뷰)", description = "로그인 사용자의 방문 인증 목록을 최신 생성순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방문 인증 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListResponse.class))),
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
    public ResponseEntity<ListResponse> getReviews(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        if (page < 0 || size < 1) {
            throw new ReviewException(ReviewErrorCode.INVALID_PAGE_REQUEST);
        }
        return ResponseEntity.ok(reviewService.getReviews(
                userDetails.getUsername(),
                PageRequest.of(page, size)
        ));
    }

    /**
     * 방문 인증 리뷰를 등록합니다.
     *
     * @param userDetails JWT 인증 사용자 정보
     * @param request     방문 인증 등록 요청 데이터 (JSON)
     * @param files       인증 이미지 파일 목록 (선택)
     * @return 방문 인증 등록 응답
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "방문 인증 등록 (리뷰)", description = "관광 콘텐츠 방문 인증과 선택적인 여행 기록을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방문 인증 등록에 성공했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateReviewResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "isVerified": true
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
            @ApiResponse(responseCode = "404", description = "관광 콘텐츠 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":404,\"message\":\"관광 콘텐츠 정보를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":500,\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
    })
    public ResponseEntity<CreateReviewResponse> createReview(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "방문 인증 요청 데이터", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @Valid @RequestPart("request") CreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        return ResponseEntity.ok(reviewFacadeService.createReview(userDetails.getUsername(), request, files));
    }
}
