package triplog.backend.mission.service;

/**
 * 일일 미션 풀의 불변 템플릿입니다.
 *
 * @param group  난이도 그룹
 * @param name   미션 이름
 * @param target 미션 판정 대상
 * @param value  완료에 필요한 값
 * @param filter 미션 상세 조건 JSON
 * @param xp     완료 보상 경험치
 */
public record DailyMissionTemplate(
        int group,
        String name,
        MissionTarget target,
        int value,
        String filter,
        int xp
) {
}
