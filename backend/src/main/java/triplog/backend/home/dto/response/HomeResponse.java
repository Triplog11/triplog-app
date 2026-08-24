package triplog.backend.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.landmark.service.LandmarkHomeCardInfo;
import triplog.backend.mission.service.MissionHomeInfo;
import triplog.backend.region.service.RegionHomeInfo;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 홈 화면 관련 응답 DTO를 그룹화하는 클래스입니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HomeResponse {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * 홈 화면을 구성하는 사용자 통계와 콘텐츠 요약 응답입니다.
     */
    @Getter
    @Schema(description = "홈 정보 조회 응답")
    public static class HomeInfoResponse {

        @Schema(description = "사용자 레벨 정보")
        private final List<LevelInformation> levelInformation;

        @Schema(description = "사용자 랭크 정보")
        private final RankInformation rankInformation;

        @Schema(description = "현재 활성 미션 목록")
        private final List<MissionInformation> missionInformation;

        @Schema(description = "최근 획득 카드 목록")
        private final List<CardInformation> cardInformation;

        @Schema(description = "최근 방문 지역 목록")
        private final List<RegionInformation> regionInformation;

        public HomeInfoResponse(
                List<LevelInformation> levelInformation,
                RankInformation rankInformation,
                List<MissionInformation> missionInformation,
                List<CardInformation> cardInformation,
                List<RegionInformation> regionInformation
        ) {
            this.levelInformation = levelInformation;
            this.rankInformation = rankInformation;
            this.missionInformation = missionInformation;
            this.cardInformation = cardInformation;
            this.regionInformation = regionInformation;
        }

        /**
         * 각 도메인의 홈 조회 모델을 하나의 API 응답으로 변환합니다.
         *
         * @param nickname 사용자 닉네임
         * @param stats 사용자 레벨·랭크 통계
         * @param missions 활성 미션 목록
         * @param cards 최근 획득 카드 목록
         * @param regions 최근 방문 지역 목록
         * @return 홈 화면 정보 응답
         */
        public static HomeInfoResponse toDto(
                String nickname,
                MyStatsResponse stats,
                List<MissionHomeInfo> missions,
                List<LandmarkHomeCardInfo> cards,
                List<RegionHomeInfo> regions
        ) {
            return new HomeInfoResponse(
                    List.of(LevelInformation.toDto(nickname, stats)),
                    RankInformation.toDto(stats),
                    missions.stream().map(MissionInformation::toDto).toList(),
                    cards.stream().map(CardInformation::toDto).toList(),
                    regions.stream().map(RegionInformation::toDto).toList()
            );
        }
    }

    /**
     * 홈 화면에 표시할 사용자 레벨 요약입니다.
     */
    @Getter
    @Schema(description = "홈 사용자 레벨 정보")
    public static class LevelInformation {

        private final Integer level;
        private final String nickname;
        private final Integer xp;
        private final Integer levelPolicy;

        public LevelInformation(Integer level, String nickname, Integer xp, Integer levelPolicy) {
            this.level = level;
            this.nickname = nickname;
            this.xp = xp;
            this.levelPolicy = levelPolicy;
        }

        /** 사용자명과 통계로 홈 레벨 정보를 생성합니다. */
        private static LevelInformation toDto(String nickname, MyStatsResponse stats) {
            return new LevelInformation(
                    stats.getLevel(),
                    nickname,
                    stats.getXp(),
                    stats.getRequiredXp()
            );
        }
    }

    /**
     * 홈 화면에 표시할 사용자 랭크와 점수 요약입니다.
     */
    @Getter
    @Schema(description = "홈 사용자 랭크 정보")
    public static class RankInformation {

        private final String currentTier;
        private final Integer monthScore;
        private final Integer overallScore;

        public RankInformation(String currentTier, Integer monthScore, Integer overallScore) {
            this.currentTier = currentTier;
            this.monthScore = monthScore;
            this.overallScore = overallScore;
        }

        /** 사용자 통계로 홈 랭크 정보를 생성합니다. */
        private static RankInformation toDto(MyStatsResponse stats) {
            return new RankInformation(
                    stats.getCurrentTier(),
                    stats.getMonthScore(),
                    stats.getOverallScore()
            );
        }
    }

    /**
     * 홈 화면에 표시할 활성 미션 항목입니다.
     */
    @Getter
    @Schema(description = "홈 미션 정보")
    public static class MissionInformation {

        private final Long missionId;
        private final String missionName;
        private final String missionType;
        private final String missionTarget;
        private final String missionOperator;
        private final Integer missionValue;
        private final String missionFilter;
        private final String missionWeekStart;
        private final String missionWeekEnd;
        private final Integer missionScore;
        private final Integer missionXp;

        public MissionInformation(
                Long missionId,
                String missionName,
                String missionType,
                String missionTarget,
                String missionOperator,
                Integer missionValue,
                String missionFilter,
                String missionWeekStart,
                String missionWeekEnd,
                Integer missionScore,
                Integer missionXp
        ) {
            this.missionId = missionId;
            this.missionName = missionName;
            this.missionType = missionType;
            this.missionTarget = missionTarget;
            this.missionOperator = missionOperator;
            this.missionValue = missionValue;
            this.missionFilter = missionFilter;
            this.missionWeekStart = missionWeekStart;
            this.missionWeekEnd = missionWeekEnd;
            this.missionScore = missionScore;
            this.missionXp = missionXp;
        }

        /** 미션 조회 정보를 홈 미션 항목으로 변환합니다. */
        private static MissionInformation toDto(MissionHomeInfo mission) {
            return new MissionInformation(
                    mission.missionId(),
                    mission.missionName(),
                    mission.missionType(),
                    mission.missionTarget(),
                    mission.missionOperator(),
                    mission.missionValue(),
                    mission.missionFilter(),
                    format(mission.missionWeekStart()),
                    format(mission.missionWeekEnd()),
                    mission.missionScore(),
                    mission.missionXp()
            );
        }
    }

    /**
     * 홈 화면에 표시할 최근 획득 카드 항목입니다.
     */
    @Getter
    @Schema(description = "홈 최근 획득 카드 정보")
    public static class CardInformation {

        private final Long landmarkId;
        private final String landmarkName;
        private final String landmarkZipcode;
        private final String cardTier;
        private final String cardName;
        private final String cardUrl;

        public CardInformation(
                Long landmarkId,
                String landmarkName,
                String landmarkZipcode,
                String cardTier,
                String cardName,
                String cardUrl
        ) {
            this.landmarkId = landmarkId;
            this.landmarkName = landmarkName;
            this.landmarkZipcode = landmarkZipcode;
            this.cardTier = cardTier;
            this.cardName = cardName;
            this.cardUrl = cardUrl;
        }

        /** 최근 획득 카드 정보를 홈 카드 항목으로 변환합니다. */
        private static CardInformation toDto(LandmarkHomeCardInfo card) {
            return new CardInformation(
                    card.landmarkId(),
                    card.landmarkName(),
                    card.landmarkZipcode(),
                    card.cardTier(),
                    card.cardName(),
                    card.cardUrl()
            );
        }
    }

    /**
     * 홈 화면에 표시할 최근 방문 지역 항목입니다.
     */
    @Getter
    @Schema(description = "홈 최근 방문 지역 정보")
    public static class RegionInformation {

        private final Long regionId;
        private final String regionName;
        private final String regionOverview;
        private final String regionZipcode;
        private final String visitedAt;
        private final Integer visitedCount;

        public RegionInformation(
                Long regionId,
                String regionName,
                String regionOverview,
                String regionZipcode,
                String visitedAt,
                Integer visitedCount
        ) {
            this.regionId = regionId;
            this.regionName = regionName;
            this.regionOverview = regionOverview;
            this.regionZipcode = regionZipcode;
            this.visitedAt = visitedAt;
            this.visitedCount = visitedCount;
        }

        /** 최근 방문 지역 정보를 홈 지역 항목으로 변환합니다. */
        private static RegionInformation toDto(RegionHomeInfo region) {
            return new RegionInformation(
                    region.regionId(),
                    region.regionName(),
                    region.regionOverview(),
                    region.regionZipcode(),
                    format(region.visitedAt()),
                    region.visitedCount()
            );
        }
    }

    /** 날짜·시간 값을 API 문자열로 변환합니다. */
    private static String format(LocalDateTime value) {
        return value == null ? null : value.format(DATE_TIME_FORMATTER);
    }
}
