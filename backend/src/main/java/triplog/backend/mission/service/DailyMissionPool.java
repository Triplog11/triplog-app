package triplog.backend.mission.service;

import java.util.List;

/**
 * 서비스에서 제공하는 일일 미션 템플릿 20개를 관리합니다.
 */
public final class DailyMissionPool {

    private DailyMissionPool() {
    }

    /**
     * 전체 일일 미션 템플릿을 반환합니다.
     *
     * @return 변경할 수 없는 일일 미션 템플릿 목록
     */
    public static List<DailyMissionTemplate> templates() {
        return List.of(
                template(1, "오늘의 첫걸음", MissionTarget.TOURISM_CONTENT_VISIT, 1, "{\"visitType\":\"ANY\"}", 10),
                template(1, "가볍게 둘러보기", MissionTarget.TOURISM_CONTENT_VISIT, 1, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"ANY\"}", 10),
                template(1, "오늘의 랜드마크", MissionTarget.LANDMARK_VISIT, 1, "{\"visitType\":\"ANY\"}", 15),
                template(1, "오늘의 여행 기록", MissionTarget.REVIEW, 1, "{\"imageRequired\":false}", 10),
                template(1, "사진으로 남기기", MissionTarget.REVIEW, 1, "{\"imageRequired\":true}", 15),
                template(1, "새로운 발자국", MissionTarget.TOURISM_CONTENT_VISIT, 1, "{\"visitType\":\"FIRST\"}", 15),
                template(1, "다시 만난 여행지", MissionTarget.TOURISM_CONTENT_VISIT, 1, "{\"visitType\":\"REVISIT\"}", 10),
                template(1, "느긋한 여행 시작", MissionTarget.TOURISM_CONTENT_VISIT, 2, "{\"visitType\":\"ANY\"}", 15),
                template(1, "관광지 두 걸음", MissionTarget.TOURISM_CONTENT_VISIT, 2, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"ANY\"}", 15),
                template(1, "랜드마크 산책", MissionTarget.LANDMARK_VISIT, 2, "{\"visitType\":\"ANY\"}", 20),
                template(1, "관광지 다시 보기", MissionTarget.TOURISM_CONTENT_VISIT, 1, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"REVISIT\"}", 10),
                template(1, "랜드마크와 재회", MissionTarget.LANDMARK_VISIT, 1, "{\"visitType\":\"REVISIT\"}", 15),
                template(1, "관광지 첫 발견", MissionTarget.TOURISM_CONTENT_VISIT, 1, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"FIRST\"}", 15),
                template(1, "랜드마크 첫 만남", MissionTarget.LANDMARK_VISIT, 1, "{\"visitType\":\"FIRST\"}", 15),
                template(1, "짧은 여행 일기", MissionTarget.REVIEW, 2, "{\"imageRequired\":false}", 15),
                template(1, "사진 일기 쓰기", MissionTarget.REVIEW, 2, "{\"imageRequired\":true}", 20),
                template(1, "새 동네에 인사", MissionTarget.REGION_VISIT, 1, "{\"visitType\":\"FIRST\"}", 20),
                template(1, "새로운 두 걸음", MissionTarget.TOURISM_CONTENT_VISIT, 2, "{\"visitType\":\"FIRST\"}", 20),
                template(1, "관광지 재방문 산책", MissionTarget.TOURISM_CONTENT_VISIT, 2, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"REVISIT\"}", 15),
                template(1, "랜드마크 재방문 산책", MissionTarget.LANDMARK_VISIT, 2, "{\"visitType\":\"REVISIT\"}", 20),
                template(2, "여행 한 바퀴", MissionTarget.TOURISM_CONTENT_VISIT, 2, "{\"visitType\":\"ANY\"}", 15),
                template(2, "관광지 탐험", MissionTarget.TOURISM_CONTENT_VISIT, 2, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"ANY\"}", 20),
                template(2, "랜드마크 여행", MissionTarget.LANDMARK_VISIT, 2, "{\"visitType\":\"ANY\"}", 25),
                template(2, "새로운 관광지 발견", MissionTarget.TOURISM_CONTENT_VISIT, 1, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"FIRST\"}", 20),
                template(2, "새로운 랜드마크 발견", MissionTarget.LANDMARK_VISIT, 1, "{\"visitType\":\"FIRST\"}", 20),
                template(2, "추억 다시 보기", MissionTarget.TOURISM_CONTENT_VISIT, 2, "{\"visitType\":\"REVISIT\"}", 15),
                template(2, "여행 기록가", MissionTarget.REVIEW, 2, "{\"imageRequired\":false}", 20),
                template(2, "새로운 동네 발견", MissionTarget.REGION_VISIT, 1, "{\"visitType\":\"FIRST\"}", 25),
                template(2, "세 번의 여행 발걸음", MissionTarget.TOURISM_CONTENT_VISIT, 3, "{\"visitType\":\"ANY\"}", 25),
                template(2, "일반 관광지 세 곳", MissionTarget.TOURISM_CONTENT_VISIT, 3, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"ANY\"}", 25),
                template(2, "랜드마크 세 걸음", MissionTarget.LANDMARK_VISIT, 3, "{\"visitType\":\"ANY\"}", 30),
                template(2, "오늘의 여행 작가", MissionTarget.REVIEW, 3, "{\"imageRequired\":false}", 25),
                template(2, "사진 기록 두 편", MissionTarget.REVIEW, 2, "{\"imageRequired\":true}", 25),
                template(2, "사진 기록 세 편", MissionTarget.REVIEW, 3, "{\"imageRequired\":true}", 30),
                template(2, "새로운 세 장소", MissionTarget.TOURISM_CONTENT_VISIT, 3, "{\"visitType\":\"FIRST\"}", 30),
                template(2, "새 관광지 두 곳", MissionTarget.TOURISM_CONTENT_VISIT, 2, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"FIRST\"}", 25),
                template(2, "새 관광지 세 곳", MissionTarget.TOURISM_CONTENT_VISIT, 3, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"FIRST\"}", 30),
                template(2, "새 랜드마크 두 곳", MissionTarget.LANDMARK_VISIT, 2, "{\"visitType\":\"FIRST\"}", 30),
                template(2, "세 번의 추억 여행", MissionTarget.TOURISM_CONTENT_VISIT, 3, "{\"visitType\":\"REVISIT\"}", 25),
                template(2, "관광지 재방문 두 곳", MissionTarget.TOURISM_CONTENT_VISIT, 2, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"REVISIT\"}", 20),
                template(3, "알찬 여행 하루", MissionTarget.TOURISM_CONTENT_VISIT, 3, "{\"visitType\":\"ANY\"}", 25),
                template(3, "새로운 여행길", MissionTarget.TOURISM_CONTENT_VISIT, 2, "{\"visitType\":\"FIRST\"}", 30),
                template(3, "랜드마크 정복의 하루", MissionTarget.LANDMARK_VISIT, 3, "{\"visitType\":\"ANY\"}", 35),
                template(3, "사진 여행가", MissionTarget.REVIEW, 2, "{\"imageRequired\":true}", 25),
                template(3, "다시 찾은 랜드마크", MissionTarget.LANDMARK_VISIT, 2, "{\"visitType\":\"REVISIT\"}", 25),
                template(3, "다섯 번의 여행", MissionTarget.TOURISM_CONTENT_VISIT, 5, "{\"visitType\":\"ANY\"}", 40),
                template(3, "관광지 집중 탐험", MissionTarget.TOURISM_CONTENT_VISIT, 5, "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"ANY\"}", 45),
                template(3, "랜드마크 집중 탐험", MissionTarget.LANDMARK_VISIT, 5, "{\"visitType\":\"ANY\"}", 50),
                template(3, "새로운 세상 탐험", MissionTarget.TOURISM_CONTENT_VISIT, 3, "{\"visitType\":\"FIRST\"}", 40),
                template(3, "사진 여행 작가", MissionTarget.REVIEW, 3, "{\"imageRequired\":true}", 35)
        );
    }

    /**
     * 일일 미션 템플릿을 생성합니다.
     */
    private static DailyMissionTemplate template(
            int group,
            String name,
            MissionTarget target,
            int value,
            String filter,
            int xp
    ) {
        return new DailyMissionTemplate(group, name, target, value, filter, xp);
    }
}
