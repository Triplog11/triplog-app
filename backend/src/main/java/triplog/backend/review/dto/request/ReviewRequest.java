package triplog.backend.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import triplog.backend.review.entity.Review;
import triplog.backend.tourismcontent.entity.TourismContent;

/**
 * 여행 기록 관련 요청 DTO를 그룹화하는 클래스입니다.
 */
@Schema(description = "여행 기록 관련 요청 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewRequest {

    /**
     * 방문 인증과 여행 기록을 등록하는 요청 DTO입니다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "방문 인증 및 여행 기록 등록 요청")
    public static class CreateRequest {

        @Schema(description = "관광 콘텐츠 식별자", example = "301")
        private Long tourismContentId;

        @Schema(description = "법정 시도 코드", example = "41")
        private String legalRegionCode;

        @Schema(description = "법정 시군구 코드", example = "110")
        private String legalDistrictCode;

        @Schema(description = "여행 기록 제목", example = "수원화성 방문")
        private String reviewTitle;

        @Schema(description = "여행 기록 내용", example = "방문 인증 완료")
        private String reviewContent;

        @DecimalMin(value = "1.0", message = "별점은 1.0 이상이어야 합니다.")
        @DecimalMax(value = "5.0", message = "별점은 5.0 이하여야 합니다.")
        @Digits(integer = 1, fraction = 1, message = "별점은 소수점 첫째 자리까지만 입력할 수 있습니다.")
        @Schema(description = "별점(소수점 첫째 자리까지)", example = "4.5")
        private Float reviewScore;

        /**
         * 요청 값을 여행 기록 엔티티로 변환합니다.
         *
         * @param usersId 사용자 식별자
         * @param tourismContent 방문한 관광 콘텐츠
         * @return 생성된 여행 기록 엔티티
         */
        public Review toEntity(String usersId, TourismContent tourismContent) {
            return new Review(
                    usersId,
                    tourismContent,
                    reviewTitle == null ? "" : reviewTitle,
                    reviewContent == null ? "" : reviewContent,
                    reviewScore == null ? 0.0F : reviewScore
            );
        }
    }
}
