package triplog.backend.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리뷰(Review) 관련 응답 데이터를 전달하기 위한 DTO입니다.
 */
@Schema(description = "리뷰 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewResponse {

    /**
     * 방문 인증 등록 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "방문 인증 등록 응답")
    public static class CreateReviewResponse {

        @Schema(description = "인증 완료 여부", example = "true")
        private final Boolean isVerified;

        /**
         * 방문 인증 성공 응답을 생성합니다.
         *
         * @return 방문 인증 등록 응답 DTO
         */
        public static CreateReviewResponse toDto() {
            return new CreateReviewResponse(true);
        }
    }
}
