package triplog.backend.appellation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.appellation.entity.Appellation;
import triplog.backend.appellation.entity.UsersAppellation;

import java.util.List;

/**
 * 칭호 API 응답 DTO를 그룹화합니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppellationResponse {

    /**
     * 로그인 사용자가 획득한 칭호 목록입니다.
     *
     * @param totalElements 전체 획득 칭호 수
     * @param items 획득 칭호 목록
     */
    @Schema(name = "AcquiredAppellationListResponse", description = "획득 칭호 목록 응답")
    public record AcquiredListResponse(
            @Schema(description = "전체 획득 칭호 수", example = "2")
            long totalElements,
            @Schema(description = "획득 칭호 목록")
            List<AcquiredItem> items
    ) {
        /**
         * 사용자 칭호 엔티티 목록을 획득 칭호 목록 응답으로 변환합니다.
         *
         * @param usersAppellations 사용자 칭호 목록
         * @return 획득 칭호 목록 응답
         */
        public static AcquiredListResponse toDto(
                List<UsersAppellation> usersAppellations
        ) {
            List<AcquiredItem> items = usersAppellations.stream()
                    .map(AcquiredItem::toDto)
                    .toList();
            return new AcquiredListResponse(items.size(), items);
        }
    }

    /**
     * 획득 칭호 목록의 개별 항목입니다.
     *
     * @param appellationId 칭호 식별자
     * @param appellationName 칭호 이름
     * @param representative 대표 칭호 여부
     */
    @Schema(name = "AcquiredAppellationItem", description = "획득 칭호 목록 항목")
    public record AcquiredItem(
            @Schema(description = "칭호 ID", example = "2")
            Long appellationId,
            @Schema(description = "칭호 이름", example = "랜드마크 탐험가")
            String appellationName,
            @Schema(description = "대표 칭호 여부", example = "true")
            boolean representative
    ) {
        /** 사용자 칭호 엔티티를 목록 항목으로 변환합니다. */
        private static AcquiredItem toDto(UsersAppellation usersAppellation) {
            Appellation appellation = usersAppellation.getAppellation();
            return new AcquiredItem(
                    appellation.getAppellationId(),
                    appellation.getAppellationName(),
                    usersAppellation.isRepresentative()
            );
        }
    }

    /**
     * 대표 칭호 변경 결과입니다.
     */
    @Getter
    @Schema(description = "대표 칭호 변경 응답")
    public static class RepresentativeResponse {

        @Schema(description = "대표 칭호 ID", example = "2")
        private final Long appellationId;

        @Schema(description = "대표 칭호 이름", example = "랜드마크 탐험가")
        private final String appellationName;

        @Schema(description = "대표 칭호 여부", example = "true")
        private final boolean representative;

        /** 대표 칭호 응답을 생성합니다. */
        private RepresentativeResponse(
                Long appellationId,
                String appellationName,
                boolean representative
        ) {
            this.appellationId = appellationId;
            this.appellationName = appellationName;
            this.representative = representative;
        }

        /**
         * 칭호 엔티티를 대표 칭호 변경 응답으로 변환합니다.
         *
         * @param appellation 대표로 지정된 칭호
         * @return 대표 칭호 변경 응답
         */
        public static RepresentativeResponse toDto(Appellation appellation) {
            return new RepresentativeResponse(
                    appellation.getAppellationId(),
                    appellation.getAppellationName(),
                    true
            );
        }
    }
}
