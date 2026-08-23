package triplog.backend.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import triplog.backend.review.repository.ReviewDetailQueryResult;
import triplog.backend.review.repository.ReviewListQueryResult;
import triplog.backend.stats.service.ActivityRewardInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 여행 기록 관련 응답 DTO를 그룹화하는 클래스입니다.
 */
@Schema(description = "여행 기록 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewResponse {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static String formatCreatedAt(LocalDateTime createdAt) {
        return createdAt == null ? null : createdAt.format(DATE_TIME_FORMATTER);
    }

    /**
     * 로그인 사용자의 방문 인증 상세 응답 DTO입니다.
     */
    @Getter
    @Schema(description = "방문 인증 상세 조회 응답")
    public static class DetailResponse {

        @Schema(description = "리뷰 ID", example = "7001")
        private final Long reviewId;

        @Schema(description = "랜드마크 ID", example = "301")
        private final Long landmarkId;

        @Schema(description = "랜드마크명", example = "수원화성")
        private final String landmarkName;

        @Schema(description = "지역 ID", example = "101")
        private final Long regionId;

        @Schema(description = "지역명", example = "수원시")
        private final String regionName;

        @Schema(description = "대표 인증 이미지 URL", nullable = true)
        private final String imageUrl;

        @Schema(description = "획득 경험치", example = "80")
        private final Integer acquiredXp;

        @Schema(description = "획득 스코어", example = "50")
        private final Integer acquiredScore;

        @Schema(description = "방문 인증 생성 일시", example = "2026-06-20T14:30:00")
        private final String createdAt;

        public DetailResponse(
                Long reviewId,
                Long landmarkId,
                String landmarkName,
                Long regionId,
                String regionName,
                String imageUrl,
                Integer acquiredXp,
                Integer acquiredScore,
                String createdAt
        ) {
            this.reviewId = reviewId;
            this.landmarkId = landmarkId;
            this.landmarkName = landmarkName;
            this.regionId = regionId;
            this.regionName = regionName;
            this.imageUrl = imageUrl;
            this.acquiredXp = acquiredXp;
            this.acquiredScore = acquiredScore;
            this.createdAt = createdAt;
        }

        /**
         * Repository 조회 결과를 방문 인증 상세 응답으로 변환합니다.
         *
         * @param result 방문 인증 상세 조회 결과
         * @return 방문 인증 상세 응답
         */
        public static DetailResponse toDto(ReviewDetailQueryResult result) {
            return new DetailResponse(
                    result.getReviewId(),
                    result.getLandmarkId(),
                    result.getLandmarkName(),
                    result.getRegionId(),
                    result.getRegionName(),
                    result.getImageUrl(),
                    result.getAcquiredXp(),
                    result.getAcquiredScore(),
                    formatCreatedAt(result.getCreatedAt())
            );
        }
    }

    /**
     * 로그인 사용자의 방문 인증 목록 응답 DTO입니다.
     */
    @Getter
    @Schema(description = "방문 인증 목록 조회 응답")
    public static class ListResponse {

        @Schema(description = "현재 페이지 번호", example = "0")
        private final int page;

        @Schema(description = "페이지 크기", example = "20")
        private final int size;

        @Schema(description = "전체 방문 인증 수", example = "12")
        private final long totalElements;

        @Schema(description = "전체 페이지 수", example = "1")
        private final int totalPages;

        @Schema(description = "방문 인증 목록")
        private final List<ListItem> items;

        public ListResponse(int page, int size, long totalElements, int totalPages, List<ListItem> items) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.items = items;
        }

        /**
         * Repository 조회 결과를 방문 인증 목록 응답으로 변환합니다.
         *
         * @param result 방문 인증 조회 페이지
         * @return 방문 인증 목록 응답
         */
        public static ListResponse toDto(Page<ReviewListQueryResult> result) {
            List<ListItem> items = result.getContent().stream()
                    .map(ListItem::toDto)
                    .toList();
            return new ListResponse(
                    result.getNumber(),
                    result.getSize(),
                    result.getTotalElements(),
                    result.getTotalPages(),
                    items
            );
        }
    }

    /**
     * 방문 인증 목록의 개별 항목 DTO입니다.
     */
    @Getter
    @Schema(description = "방문 인증 목록 항목")
    public static class ListItem {

        @Schema(description = "리뷰 ID", example = "7001")
        private final Long reviewId;

        @Schema(description = "관광 콘텐츠 ID", example = "1001")
        private final Long tourismContentId;

        @Schema(description = "관광 콘텐츠명", example = "수원화성")
        private final String contentTitle;

        @Schema(description = "리뷰 제목", example = "수원화성 방문")
        private final String reviewTitle;

        @Schema(description = "지역 ID", example = "101")
        private final Long regionId;

        @Schema(description = "지역명", example = "수원시")
        private final String regionName;

        @Schema(description = "대표 인증 이미지 URL", nullable = true)
        private final String imageUrl;

        @Schema(description = "획득 경험치", example = "80")
        private final Integer acquiredXp;

        @Schema(description = "획득 스코어", example = "50")
        private final Integer acquiredScore;

        @Schema(description = "방문 인증 생성 일시", example = "2026-06-20T14:30:00", nullable = true)
        private final String createdAt;

        public ListItem(
                Long reviewId,
                Long tourismContentId,
                String contentTitle,
                String reviewTitle,
                Long regionId,
                String regionName,
                String imageUrl,
                Integer acquiredXp,
                Integer acquiredScore,
                String createdAt
        ) {
            this.reviewId = reviewId;
            this.tourismContentId = tourismContentId;
            this.contentTitle = contentTitle;
            this.reviewTitle = reviewTitle;
            this.regionId = regionId;
            this.regionName = regionName;
            this.imageUrl = imageUrl;
            this.acquiredXp = acquiredXp;
            this.acquiredScore = acquiredScore;
            this.createdAt = createdAt;
        }

        private static ListItem toDto(ReviewListQueryResult result) {
            return new ListItem(
                    result.getReviewId(),
                    result.getTourismContentId(),
                    result.getContentTitle(),
                    result.getReviewTitle(),
                    result.getRegionId(),
                    result.getRegionName(),
                    result.getImageUrl(),
                    result.getAcquiredXp(),
                    result.getAcquiredScore(),
                    formatCreatedAt(result.getCreatedAt())
            );
        }
    }

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
