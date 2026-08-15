package triplog.backend.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.stats.service.ActivityRewardInfo;

import java.util.List;

/**
 * 여행 기록 관련 응답 DTO를 그룹화하는 클래스입니다.
 */
@Schema(description = "여행 기록 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewResponse {

    /**
     * 방문 인증과 여행 기록 등록 결과를 반환하는 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "방문 인증 및 여행 기록 등록 응답")
    public static class CreateReviewResponse {

        @Schema(description = "인증 완료 여부", example = "true")
        private final Boolean isVerified;

        @Schema(description = "적용된 정책별 보상 내역")
        private final List<ActivityRewardInfo> rewards;

        @Schema(description = "총 지급 XP", example = "95")
        private final Integer totalXp;

        @Schema(description = "총 지급 Score", example = "50")
        private final Integer totalScore;

        /**
         * 정책 적용 결과로 방문 인증 응답을 생성합니다.
         *
         * @param rewards      정책별 보상 내역
         * @param totalXp      총 지급 XP
         * @param totalScore   총 지급 Score
         * @return 방문 인증 및 여행 기록 등록 응답
         */
        public static CreateReviewResponse toDto(
                List<ActivityRewardInfo> rewards,
                int totalXp,
                int totalScore
        ) {
            return new CreateReviewResponse(true, rewards, totalXp, totalScore);
        }
    }
}
