package triplog.backend.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰(Review) 관련 요청 데이터를 전달하기 위한 DTO입니다.
 */
@Schema(description = "리뷰 관련 요청 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewRequest {

    /**
     * 방문 인증 등록 요청 DTO입니다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "방문 인증 등록 요청")
    public static class CreateReviewRequest {

        @Schema(description = "관광 콘텐츠 ID", example = "301")
        private Long tourismContentId;

        @Schema(description = "법정동 시도 코드", example = "41")
        private String legalRegionCode;

        @Schema(description = "법정동 시군구 코드", example = "110")
        private String legalDistrictCode;

        @Schema(description = "리뷰 제목", example = "수원화성 방문")
        private String reviewTitle;

        @Schema(description = "리뷰 내용", example = "방문 인증 완료")
        private String reviewContent;

        @Schema(description = "만족도 (1~5)", example = "5")
        private Integer reviewScore;

        @Schema(description = "총 획득 포인트", example = "150")
        private Integer reviewPoint;
    }
}
