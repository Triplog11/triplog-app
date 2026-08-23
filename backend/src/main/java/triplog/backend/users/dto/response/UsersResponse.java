package triplog.backend.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;
import triplog.backend.users.entity.Users;

/**
 * 사용자(Users) API 응답 DTO를 그룹화하는 클래스입니다.
 * <p>
 * 사용자 정보 조회, 변경, 중복 확인 등 사용자 도메인에서 반환하는 응답 DTO를
 * 내부 정적 클래스로 정의합니다.
 */
@Schema(description = "사용자 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsersResponse {

    /**
     * 마이페이지 정보 조회 응답 DTO입니다.
     */
    @Getter
    @Schema(description = "마이페이지 정보 조회 응답")
    public static class MyPageInfoResponse {

        @Schema(description = "닉네임", example = "여행자")
        private final String nickname;

        @Schema(description = "프로필 이미지 URL", nullable = true,
                example = "https://example.com/profile.png")
        private final String profileUrl;

        @Schema(description = "현재 레벨", example = "3")
        private final Integer level;

        @Schema(description = "현재 경험치", example = "340")
        private final Integer xp;

        @Schema(description = "현재 티어", example = "BRONZE")
        private final String tier;

        @Schema(description = "누적 스코어", example = "1250")
        private final Integer overallScore;

        @Schema(description = "월간 스코어", example = "220")
        private final Integer monthScore;

        @Schema(description = "총 방문 인증 수", example = "12")
        private final Integer totalCertificationCount;

        @Schema(description = "방문 지역 수", example = "5")
        private final Integer visitedRegionCount;

        @Schema(description = "획득 배지 수", example = "4")
        private final Integer acquiredBadgeCount;

        @Schema(description = "수집 카드 수", example = "8")
        private final Integer collectedCardCount;

        public MyPageInfoResponse(
                String nickname,
                String profileUrl,
                Integer level,
                Integer xp,
                String tier,
                Integer overallScore,
                Integer monthScore,
                Integer totalCertificationCount,
                Integer visitedRegionCount,
                Integer acquiredBadgeCount,
                Integer collectedCardCount
        ) {
            this.nickname = nickname;
            this.profileUrl = profileUrl;
            this.level = level;
            this.xp = xp;
            this.tier = tier;
            this.overallScore = overallScore;
            this.monthScore = monthScore;
            this.totalCertificationCount = totalCertificationCount;
            this.visitedRegionCount = visitedRegionCount;
            this.acquiredBadgeCount = acquiredBadgeCount;
            this.collectedCardCount = collectedCardCount;
        }

        /**
         * 사용자·통계 정보와 활동 집계값을 마이페이지 응답으로 변환합니다.
         *
         * @param users 사용자 정보
         * @param stats 통계 정보
         * @param totalCertificationCount 총 방문 인증 수
         * @param visitedRegionCount 방문 지역 수
         * @param acquiredBadgeCount 획득 배지 수
         * @param collectedCardCount 수집 카드 수
         * @return 마이페이지 정보 응답
         */
        public static MyPageInfoResponse toDto(
                Users users,
                MyStatsResponse stats,
                int totalCertificationCount,
                int visitedRegionCount,
                int acquiredBadgeCount,
                int collectedCardCount
        ) {
            return new MyPageInfoResponse(
                    users.getNickname(),
                    users.getProfileUrl(),
                    stats.getLevel(),
                    stats.getXp(),
                    stats.getCurrentTier(),
                    stats.getOverallScore(),
                    stats.getMonthScore(),
                    totalCertificationCount,
                    visitedRegionCount,
                    acquiredBadgeCount,
                    collectedCardCount
            );
        }
    }

    /**
     * 닉네임 중복 확인 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "닉네임 중복 확인 응답")
    public static class NicknameCheckResponse {

        @Schema(description = "사용 가능 여부", example = "true")
        private Boolean available;

        @Schema(description = "결과 메시지", example = "사용 가능한 닉네임입니다.")
        private String message;

        /**
         * 닉네임 사용 가능 여부를 기반으로 응답 DTO를 생성합니다.
         *
         * @param available 닉네임 사용 가능 여부
         * @return 닉네임 중복 확인 응답 DTO
         */
        public static NicknameCheckResponse toDto(Boolean available) {
            String message = available ? "사용 가능한 닉네임입니다." : "중복된 닉네임입니다.";
            return new NicknameCheckResponse(available, message);
        }
    }

    /**
     * 프로필 수정 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "프로필 수정 응답")
    public static class ProfileUpdateResponse {

        @Schema(description = "유저 식별자", example = "550e8400-e29b-41d4-a716-446655440000")
        private String usersId;

        @Schema(description = "닉네임", example = "여행자")
        private String nickname;

        @Schema(description = "시", example = "수원시")
        private String addressSi;

        @Schema(description = "도/군", example = "경기도")
        private String addressDoGun;

        @Schema(description = "구", example = "팔달구")
        private String addressGu;

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png", nullable = true)
        private String profileUrl;
    }
  
   /**
     * 이메일 중복 확인 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "이메일 중복 확인 응답")
    public static class EmailCheckResponse {

        @Schema(description = "사용 가능 여부", example = "true")
        private Boolean available;

        @Schema(description = "결과 메시지", example = "사용 가능한 이메일입니다.")
        private String message;

        /**
         * 이메일 사용 가능 여부를 기반으로 응답 DTO를 생성합니다.
         *
         * @param available 이메일 사용 가능 여부
         * @return 이메일 중복 확인 응답 DTO
         */
        public static EmailCheckResponse toDto(Boolean available) {
            String message = available ? "사용 가능한 이메일입니다." : "중복된 이메일입니다.";
            return new EmailCheckResponse(available, message);
        }
    }
}

