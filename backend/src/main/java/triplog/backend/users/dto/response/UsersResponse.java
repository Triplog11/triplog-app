package triplog.backend.users.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;
import triplog.backend.appellation.service.RepresentativeAppellationInfo;
import triplog.backend.badge.service.RepresentativeBadgeInfo;
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

        @Schema(description = "현재 누적 경험치", example = "240")
        private final Integer xp;

        @Schema(description = "현재 티어", example = "SILVER")
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

        @Schema(description = "대표 칭호", nullable = true)
        private final RepresentativeAppellationResponse representativeAppellation;

        @Schema(description = "대표 배지", nullable = true)
        private final RepresentativeBadgeResponse representativeBadge;

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
                Integer collectedCardCount,
                RepresentativeAppellationResponse representativeAppellation,
                RepresentativeBadgeResponse representativeBadge
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
            this.representativeAppellation = representativeAppellation;
            this.representativeBadge = representativeBadge;
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
         * @param representativeAppellation 대표 칭호 정보, 미지정 시 {@code null}
         * @param representativeBadge 대표 배지 정보, 미지정 시 {@code null}
         * @return 마이페이지 정보 응답
         */
        public static MyPageInfoResponse toDto(
                Users users,
                MyStatsResponse stats,
                int totalCertificationCount,
                int visitedRegionCount,
                int acquiredBadgeCount,
                int collectedCardCount,
                RepresentativeAppellationInfo representativeAppellation,
                RepresentativeBadgeInfo representativeBadge
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
                    collectedCardCount,
                    RepresentativeAppellationResponse.toDto(representativeAppellation),
                    RepresentativeBadgeResponse.toDto(representativeBadge)
            );
        }
    }

    /**
     * 마이페이지에 표시할 대표 배지 정보입니다.
     */
    @Getter
    @Schema(description = "대표 배지 정보")
    public static class RepresentativeBadgeResponse {

        @Schema(description = "대표 배지 ID", example = "1")
        private final Long badgeId;

        @Schema(description = "대표 배지 이름", example = "첫 발자국")
        private final String badgeName;

        @Schema(description = "대표 배지 이미지 URL",
                example = "https://cdn.triplog.com/badges/first-step.png")
        private final String badgeUrl;

        /** 대표 배지 응답을 생성합니다. */
        private RepresentativeBadgeResponse(
                Long badgeId,
                String badgeName,
                String badgeUrl
        ) {
            this.badgeId = badgeId;
            this.badgeName = badgeName;
            this.badgeUrl = badgeUrl;
        }

        /**
         * 대표 배지 조회 결과를 마이페이지 응답으로 변환합니다.
         *
         * @param representativeBadge 대표 배지 조회 결과
         * @return 대표 배지 응답, 미지정 시 {@code null}
         */
        public static RepresentativeBadgeResponse toDto(
                RepresentativeBadgeInfo representativeBadge
        ) {
            if (representativeBadge == null) {
                return null;
            }
            return new RepresentativeBadgeResponse(
                    representativeBadge.badgeId(),
                    representativeBadge.badgeName(),
                    representativeBadge.badgeUrl()
            );
        }
    }

    /**
     * 마이페이지에 표시할 대표 칭호 정보입니다.
     */
    @Getter
    @Schema(description = "대표 칭호 정보")
    public static class RepresentativeAppellationResponse {

        @Schema(description = "대표 칭호 ID", example = "2")
        private final Long appellationId;

        @Schema(description = "대표 칭호 이름", example = "랜드마크 탐험가")
        private final String appellationName;

        /** 대표 칭호 응답을 생성합니다. */
        private RepresentativeAppellationResponse(
                Long appellationId,
                String appellationName
        ) {
            this.appellationId = appellationId;
            this.appellationName = appellationName;
        }

        /**
         * 대표 칭호 조회 결과를 마이페이지 응답으로 변환합니다.
         *
         * @param representativeAppellation 대표 칭호 조회 결과
         * @return 대표 칭호 응답, 미지정 시 {@code null}
         */
        public static RepresentativeAppellationResponse toDto(
                RepresentativeAppellationInfo representativeAppellation
        ) {
            if (representativeAppellation == null) {
                return null;
            }
            return new RepresentativeAppellationResponse(
                    representativeAppellation.appellationId(),
                    representativeAppellation.appellationName()
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
     * 회원 탈퇴 처리 결과를 전달하는 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "회원 탈퇴 응답")
    public static class WithdrawalResponse {

        @Schema(description = "탈퇴 처리 여부", example = "true")
        private Boolean deleted;

        @Schema(description = "탈퇴한 계정의 이메일", example = "test@test.com")
        private String email;

        @Schema(description = "탈퇴 처리 일시", example = "2026-06-25T10:50:00")
        private String deletedAt;
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

