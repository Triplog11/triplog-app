package triplog.backend.mission.service;

import triplog.backend.mission.entity.Mission;

/**
 * 사용자 행동에 따른 미션 완료 판정 기능을 정의하는 도메인 서비스입니다.
 */
public interface MissionAchievementService {

    /**
     * 사용자의 미션 진행 값을 실제 활동 로그에서 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param mission 미션
     * @return 현재 진행 값
     */
    long getProgress(String usersId, Mission mission);

    /**
     * 관광 콘텐츠 방문으로 달성 가능한 미션을 판정합니다.
     *
     * @param usersId   사용자 식별자
     * @param contentType 방문 콘텐츠 유형
     * @param firstVisit 최초 방문 여부
     */
    void evaluateVisit(String usersId, String contentType, boolean firstVisit);

    /**
     * 여행 기록 작성으로 달성 가능한 미션을 판정합니다.
     *
     * @param usersId 사용자 식별자
     */
    void evaluateReview(String usersId);

    /**
     * 지역 방문으로 달성 가능한 미션을 판정합니다.
     *
     * @param usersId 사용자 식별자
     */
    void evaluateRegion(String usersId);
}
