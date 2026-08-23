package triplog.backend.mission.service;

import triplog.backend.mission.entity.Mission;

import java.time.LocalDateTime;

/**
 * 홈 화면에 노출할 활성 미션 정보입니다.
 *
 * @param missionId 미션 식별자
 * @param missionName 미션 이름
 * @param missionType 미션 유형
 * @param missionTarget 달성 조건의 집계 대상
 * @param missionOperator 달성 조건 연산자
 * @param missionValue 달성 기준값
 * @param missionFilter 달성 조건 필터
 * @param missionWeekStart 미션 시작 일시
 * @param missionWeekEnd 미션 종료 일시
 * @param missionScore 완료 보상 점수
 * @param missionXp 완료 보상 경험치
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

    /**
     * 미션 엔티티를 홈 미션 조회 모델로 변환합니다.
     *
     * @param mission 변환할 미션
     * @return 홈 화면 미션 정보
     */
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

    /**
     * JSON 문자열 값이 DB에서 따옴표를 포함해 조회되는 경우 API 표시값만 추출합니다.
     */
    private static String stripJsonQuotes(String value) {
        if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
