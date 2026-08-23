package triplog.backend.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import triplog.backend.users.repository.ActivityHistoryQueryResult;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 마이페이지 API 응답 DTO를 그룹화합니다.
 */
@Schema(description = "마이페이지 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageResponse {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * 활동 히스토리 목록 응답입니다.
     */
    @Getter
    @Schema(description = "활동 히스토리 목록 응답")
    public static class ActivityHistoryResponse {

        @Schema(description = "현재 페이지 번호(0부터 시작)", example = "0")
        private final Integer page;

        @Schema(description = "페이지 크기", example = "10")
        private final Integer size;

        @Schema(description = "전체 활동 수", example = "24")
        private final Long totalElements;

        @Schema(description = "전체 페이지 수", example = "3")
        private final Integer totalPages;

        @Schema(description = "활동 목록")
        private final List<ActivityItem> activities;

        public ActivityHistoryResponse(
                Integer page,
                Integer size,
                Long totalElements,
                Integer totalPages,
                List<ActivityItem> activities
        ) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.activities = activities;
        }

        /**
         * 통합 활동 조회 페이지를 API 응답으로 변환합니다.
         *
         * @param result 통합 활동 조회 페이지
         * @return 활동 히스토리 목록 응답
         */
        public static ActivityHistoryResponse toDto(Page<ActivityHistoryQueryResult> result) {
            List<ActivityItem> activities = result.getContent().stream()
                    .map(ActivityItem::toDto)
                    .toList();
            return new ActivityHistoryResponse(
                    result.getNumber(),
                    result.getSize(),
                    result.getTotalElements(),
                    result.getTotalPages(),
                    activities
            );
        }
    }

    /**
     * 활동 히스토리의 개별 항목입니다.
     */
    @Getter
    @Schema(description = "활동 히스토리 항목")
    public static class ActivityItem {

        @Schema(description = "활동 로그 식별자", example = "101")
        private final Long activityId;

        @Schema(description = "활동 유형", allowableValues = {"BADGE", "LEVEL", "REGION", "LANDMARK"},
                example = "BADGE")
        private final String activityType;

        @Schema(description = "활동 제목", example = "여행 입문자 뱃지 획득")
        private final String title;

        @Schema(description = "활동 내용", nullable = true, example = "첫 방문 인증을 완료했습니다.")
        private final String content;

        @Schema(description = "획득 점수", example = "100")
        private final Integer score;

        @Schema(description = "획득 경험치", nullable = true, example = "30")
        private final Integer xp;

        @Schema(description = "활동 생성 일시(yyyy-MM-dd'T'HH:mm:ss)",
                example = "2026-08-23T14:30:00")
        private final String createdAt;

        public ActivityItem(
                Long activityId,
                String activityType,
                String title,
                String content,
                Integer score,
                Integer xp,
                String createdAt
        ) {
            this.activityId = activityId;
            this.activityType = activityType;
            this.title = title;
            this.content = content;
            this.score = score;
            this.xp = xp;
            this.createdAt = createdAt;
        }

        /**
         * 통합 조회 결과를 활동 항목으로 변환합니다.
         *
         * @param result 통합 활동 조회 결과
         * @return 활동 히스토리 항목
         */
        public static ActivityItem toDto(ActivityHistoryQueryResult result) {
            return new ActivityItem(
                    result.activityId(),
                    result.activityType(),
                    result.title(),
                    result.content(),
                    result.score(),
                    result.xp(),
                    result.createdAt().format(DATE_TIME_FORMATTER)
            );
        }
    }
}
