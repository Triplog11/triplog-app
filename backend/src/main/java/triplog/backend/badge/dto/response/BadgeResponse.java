package triplog.backend.badge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import triplog.backend.badge.repository.BadgeDetailQueryResult;
import triplog.backend.badge.repository.BadgeQueryResult;

import java.util.List;

/**
 * 배지 조회 API 응답 DTO를 그룹화하는 클래스입니다.
 */
public final class BadgeResponse {

    private BadgeResponse() {
    }

    /**
     * Repository가 반환한 숫자형 Boolean 값을 API Boolean 값으로 변환합니다.
     *
     * @param value {@code 1} 또는 {@code 0}으로 조회된 값
     * @return 값이 {@code 1}이면 {@code true}, 그 외에는 {@code false}
     */
    private static boolean toBoolean(Integer value) {
        return value != null && value == 1;
    }

    @Schema(name = "BadgeDetailResponse", description = "배지 상세 조회 응답")
    public record BadgeDetailResponse(
            @Schema(description = "배지 ID", example = "1") Long badgeId,
            @Schema(description = "배지명", example = "첫 발자국") String badgeName,
            @Schema(description = "배지 이미지 URL", example = "https://cdn.triplog.com/badges/first-step.png") String badgeUrl,
            @Schema(description = "배지 그룹", example = "1", nullable = true) Integer badgeGroup,
            @Schema(description = "배지 타입", example = "REVIEW") String badgeType,
            @Schema(description = "배지 대상", example = "REVIEW_COUNT") String badgeTarget,
            @Schema(description = "조건 연산자", example = ">=") String badgeOperator,
            @Schema(description = "배지 조건값", example = "1", nullable = true) Integer badgeValue,
            @Schema(description = "로그인 사용자의 획득 여부", example = "true") Boolean acquired,
            @Schema(description = "대표 배지 여부", example = "false") Boolean representative
    ) {
        /**
         * 상세 조회 결과를 API 응답 DTO로 변환합니다.
         *
         * @param result Repository 상세 조회 결과
         * @return 배지 상세 응답
         */
        public static BadgeDetailResponse from(BadgeDetailQueryResult result) {
            return new BadgeDetailResponse(
                    result.badgeId(), result.badgeName(), result.badgeUrl(), result.badgeGroup(),
                    result.badgeType(), result.badgeTarget(), result.badgeOperator(), result.badgeValue(),
                    toBoolean(result.acquired()), toBoolean(result.representative()));
        }
    }

    @Schema(name = "BadgeListResponse", description = "전체 또는 미획득 배지 목록 응답")
    public record BadgeListResponse(
            @Schema(description = "현재 페이지 번호", example = "0") int page,
            @Schema(description = "페이지 크기", example = "10") int size,
            @Schema(description = "조회된 전체 배지 수", example = "14") long totalElements,
            @Schema(description = "전체 페이지 수", example = "2") int totalPages,
            @Schema(description = "배지 목록") List<BadgeItem> items
    ) implements BadgeListResult {
        /**
         * 페이징된 조회 결과를 전체 배지 목록 응답으로 변환합니다.
         *
         * @param result 페이징된 배지 조회 결과
         * @return 전체 배지 목록 응답
         */
        public static BadgeListResponse toDto(Page<BadgeQueryResult> result) {
            List<BadgeItem> items = result.getContent().stream().map(BadgeItem::toDto).toList();
            return new BadgeListResponse(result.getNumber(), result.getSize(),
                    result.getTotalElements(), result.getTotalPages(), items);
        }
    }

    @Schema(name = "BadgeItem", description = "전체 또는 미획득 배지 목록 항목")
    public record BadgeItem(
            @Schema(description = "배지 ID", example = "1") Long badgeId,
            @Schema(description = "배지명", example = "첫 발자국") String badgeName,
            @Schema(description = "배지 이미지 URL", example = "https://cdn.triplog.com/badges/first-step.png") String badgeUrl,
            @Schema(description = "배지 타입", example = "REVIEW") String badgeType,
            @Schema(description = "배지 대상", example = "REVIEW_COUNT") String badgeTarget,
            @Schema(description = "배지 조건값", example = "1", nullable = true) Integer badgeValue,
            @Schema(description = "로그인 사용자의 획득 여부", example = "true") Boolean acquired,
            @Schema(description = "대표 배지 여부", example = "false") Boolean representative
    ) {
        /**
         * Repository 목록 조회 결과를 배지 항목으로 변환합니다.
         */
        private static BadgeItem toDto(BadgeQueryResult result) {
            return new BadgeItem(result.badgeId(), result.badgeName(), result.badgeUrl(),
                    result.badgeType(), result.badgeTarget(), result.badgeValue(),
                    toBoolean(result.acquired()), toBoolean(result.representative()));
        }
    }

    @Schema(name = "BadgeListAcquiredResponse", description = "획득 배지 목록 응답")
    public record BadgeListAcquiredResponse(
            @Schema(description = "현재 페이지 번호", example = "0") int page,
            @Schema(description = "페이지 크기", example = "10") int size,
            @Schema(description = "조회된 전체 획득 배지 수", example = "4") long totalElements,
            @Schema(description = "전체 페이지 수", example = "1") int totalPages,
            @Schema(description = "획득 배지 목록") List<AcquiredBadgeItem> items
    ) implements BadgeListResult {
        /**
         * 페이징된 조회 결과를 획득 배지 목록 응답으로 변환합니다.
         *
         * @param result 페이징된 획득 배지 조회 결과
         * @return 획득 배지 목록 응답
         */
        public static BadgeListAcquiredResponse toDto(Page<BadgeQueryResult> result) {
            List<AcquiredBadgeItem> items = result.getContent().stream().map(AcquiredBadgeItem::toDto).toList();
            return new BadgeListAcquiredResponse(result.getNumber(), result.getSize(),
                    result.getTotalElements(), result.getTotalPages(), items);
        }
    }

    @Schema(name = "AcquiredBadgeItem", description = "획득 배지 목록 항목")
    public record AcquiredBadgeItem(
            @Schema(description = "배지 ID", example = "1") Long badgeId,
            @Schema(description = "배지명", example = "첫 발자국") String badgeName,
            @Schema(description = "배지 이미지 URL", example = "https://cdn.triplog.com/badges/first-step.png") String badgeUrl,
            @Schema(description = "대표 배지 여부", example = "false") Boolean representative
    ) {
        /**
         * Repository 목록 조회 결과를 획득 배지 항목으로 변환합니다.
         */
        private static AcquiredBadgeItem toDto(BadgeQueryResult result) {
            return new AcquiredBadgeItem(result.badgeId(), result.badgeName(),
                    result.badgeUrl(), toBoolean(result.representative()));
        }
    }
}
