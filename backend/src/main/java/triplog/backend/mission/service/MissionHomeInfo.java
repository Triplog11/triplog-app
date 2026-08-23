package triplog.backend.mission.service;

import triplog.backend.mission.entity.Mission;

import java.time.LocalDateTime;

/**
 * 홈 화면에 노출할 활성 미션 정보입니다.
 */
public record MissionHomeInfo(
        Long missionId,
        String missionName,
        String missionType,
        String missionTarget,
        String missionOperator,
        Integer missionValue,
        String missionFilter,
        LocalDateTime missionWeekStart,
        LocalDateTime missionWeekEnd,
        Integer missionScore,
        Integer missionXp
) {

    public static MissionHomeInfo from(Mission mission) {
        return new MissionHomeInfo(
                mission.getMissionId(),
                mission.getMissionName(),
                mission.getMissionType(),
                mission.getMissionTarget(),
                mission.getMissionOperator(),
                mission.getMissionValue(),
                stripJsonQuotes(mission.getMissionFilter()),
                mission.getMissionWeekStart(),
                mission.getMissionWeekEnd(),
                mission.getMissionScore(),
                mission.getMissionXp()
        );
    }

    private static String stripJsonQuotes(String value) {
        if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
