package triplog.backend.stats.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 사용자 통계(Stats) 관련 응답 데이터를 전달하기 위한 DTO입니다.
 * <p>
 * 서비스 계층에서 처리된 사용자 통계 정보를 클라이언트에 반환할 때 사용됩니다.
 */
@Schema(description = "사용자 통계 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatsResponse {

    /**
     * 로그인 사용자의 전체 및 월간 랭킹 정보를 반환하는 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "내 랭킹 조회 응답")
    public static class MyRankingResponse {

        @Schema(description = "닉네임", example = "여행자")
        private String nickname;

        @Schema(description = "프로필 이미지 URL",
                example = "https://cdn.triplog.com/profiles/user-001.png", nullable = true)
        private String profileUrl;

        @Schema(description = "전체 순위", example = "120")
        private Integer totalRank;

        @Schema(description = "월간 순위", example = "34")
        private Integer monthlyRank;

        @Schema(description = "분기 순위", example = "56")
        private Integer quarterRank;

        @Schema(description = "누적 스코어", example = "1250")
        private Integer overallScore;

        @Schema(description = "월간 스코어", example = "220")
        private Integer monthScore;

        @Schema(description = "분기 스코어", example = "680")
        private Integer quarterScore;

        @Schema(description = "현재 레벨", example = "3")
        private Integer level;

        @Schema(description = "현재 티어", example = "BRONZE")
        private String tier;

        @Schema(description = "다음 티어", example = "SILVER", nullable = true)
        private String nextTier;

        @Schema(description = "다음 티어 달성 조건 점수", example = "500", nullable = true)
        private Integer requiredScore;
    }

    /**
     * 전체 랭킹 조회 응답 DTO입니다.
     * <p>
     * 페이지네이션 정보와 랭킹 목록을 포함합니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "전체 랭킹 조회 응답")
    public static class RankingListResponse {

        @Schema(description = "랭킹 타입", example = "MONTHLY")
        private String rankingType;

        @Schema(description = "현재 페이지 (0부터 시작)", example = "0")
        private Integer page;

        @Schema(description = "페이지 크기", example = "10")
        private Integer size;

        @Schema(description = "전체 사용자 수", example = "1200")
        private Long totalElements;

        @Schema(description = "전체 페이지 수", example = "120")
        private Integer totalPages;

        @Schema(description = "랭킹 목록")
        private List<RankingEntry> rankings;
    }

    /**
     * 개별 랭킹 항목 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "랭킹 항목")
    public static class RankingEntry {

        @Schema(description = "순위", example = "1")
        private Integer rank;

        @Schema(description = "유저 식별자", example = "550e8400-e29b-41d4-a716-446655440000")
        private String usersId;

        @Schema(description = "닉네임", example = "여행자")
        private String nickname;

        @Schema(description = "프로필 이미지 URL",
                example = "https://cdn.triplog.com/profiles/user-001.png", nullable = true)
        private String profileUrl;

        @Schema(description = "기준 점수", example = "3200")
        private Integer score;

        @Schema(description = "레벨", example = "7")
        private Integer level;

        @Schema(description = "티어", example = "GOLD")
        private String tier;
    }

    /**
     * 내 스탯 정보 조회 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "내 스탯 정보 조회 응답")
    public static class MyStatsResponse {

        @Schema(description = "현재 레벨", example = "3")
        private Integer level;

        @Schema(description = "현재 경험치", example = "340")
        private Integer xp;

        @Schema(description = "현재 티어", example = "BRONZE")
        private String currentTier;

        @Schema(description = "누적 스코어", example = "1250")
        private Integer overallScore;

        @Schema(description = "월간 스코어", example = "220")
        private Integer monthScore;

        @Schema(description = "다음 레벨", example = "4", nullable = true)
        private Integer nextLevel;

        @Schema(description = "다음 레벨 필요 경험치", example = "500", nullable = true)
        private Integer requiredXp;

        @Schema(description = "남은 경험치", example = "160", nullable = true)
        private Integer remainingXp;

        @Schema(description = "다음 티어", example = "SILVER", nullable = true)
        private String nextTier;

        @Schema(description = "다음 티어 필요 점수", example = "500", nullable = true)
        private Integer requiredScore;
    }
}
