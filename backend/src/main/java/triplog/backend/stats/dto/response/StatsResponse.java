package triplog.backend.stats.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

        @Schema(description = "누적 스코어", example = "1250")
        private Integer overallScore;

        @Schema(description = "월간 스코어", example = "220")
        private Integer monthScore;

        @Schema(description = "현재 레벨", example = "3")
        private Integer level;

        @Schema(description = "현재 티어", example = "BRONZE")
        private String tier;

        @Schema(description = "다음 티어", example = "SILVER", nullable = true)
        private String nextTier;

        @Schema(description = "다음 티어 달성 조건 점수", example = "500", nullable = true)
        private Integer requiredScore;
    }
}
