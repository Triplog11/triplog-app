package triplog.backend.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 미션 관련 응답 데이터를 전달하기 위한 DTO입니다.
 */
@Schema(description = "미션 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MissionResponse {

    /**
     * 내 미션 진행 현황 목록 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "내 미션 진행 현황 조회 응답")
    public static class MyMissionListResponse {

        @Schema(description = "미션 목록")
        private List<MissionEntry> missions;
    }

    /**
     * 개별 미션 진행 현황 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "미션 항목")
    public static class MissionEntry {

        @Schema(description = "미션 ID", example = "10")
        private Long missionId;

        @Schema(description = "미션명", example = "이번 주 지역 3곳 방문")
        private String missionName;

        @Schema(description = "미션 타입", example = "WEEKLY")
        private String missionType;

        @Schema(description = "미션 대상", example = "REGION_VISIT")
        private String missionTarget;

        @Schema(description = "미션 상세 조건", example = "주간 내 랜드마크 1곳 인증", nullable = true)
        private String missionCondition;

        @Schema(description = "주간 시작일", example = "2026-06-22T00:00:00", nullable = true)
        private String weekStart;

        @Schema(description = "주간 종료일", example = "2026-06-28T23:59:59", nullable = true)
        private String weekEnd;

        @Schema(description = "보상 스코어", example = "100")
        private Integer rewardScore;

        @Schema(description = "보상 경험치", example = "150")
        private Integer rewardXp;

        @Schema(description = "완료 여부", example = "false")
        private Boolean completed;

        @Schema(description = "완료 일시", example = "2026-06-22T00:00:00", nullable = true)
        private String completedAt;
    }

    /**
     * 미션 목록 조회 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "미션 목록 조회 응답")
    public static class MissionListResponse {

        @Schema(description = "미션 목록")
        private List<MissionSummary> missions;
    }

    /**
     * 개별 미션 요약 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "미션 요약 항목")
    public static class MissionSummary {

        @Schema(description = "미션 ID", example = "10")
        private Long missionId;

        @Schema(description = "미션명", example = "이번 주 지역 3곳 방문")
        private String missionName;

        @Schema(description = "미션 타입", example = "WEEKLY")
        private String missionType;

        @Schema(description = "미션 대상", example = "REGION_VISIT")
        private String missionTarget;

        @Schema(description = "미션 상세 조건", example = "주간 내 랜드마크 1곳 인증", nullable = true)
        private String missionCondition;

        @Schema(description = "주간 시작일", example = "2026-06-22T00:00:00", nullable = true)
        private String weekStart;

        @Schema(description = "주간 종료일", example = "2026-06-28T23:59:59", nullable = true)
        private String weekEnd;

        @Schema(description = "보상 스코어", example = "100")
        private Integer rewardScore;

        @Schema(description = "보상 경험치", example = "150")
        private Integer rewardXp;
    }
}
