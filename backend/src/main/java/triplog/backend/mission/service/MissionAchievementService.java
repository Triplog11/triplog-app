package triplog.backend.mission.service;

import triplog.backend.mission.entity.Mission;

import java.util.List;

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
     * @return 이번 판정에서 최초 완료된 미션 목록
     */
    List<MissionCompletionInfo> evaluateVisit(String usersId, String contentType);

    /**
     * 여행 기록 작성으로 달성 가능한 미션을 판정합니다.
     *
     * @param usersId 사용자 식별자
     * @return 이번 판정에서 최초 완료된 미션 목록
     */
    List<MissionCompletionInfo> evaluateReview(String usersId);

    /**
     * 지역 방문으로 달성 가능한 미션을 판정합니다.
     *
     * @param usersId 사용자 식별자
     * @return 이번 판정에서 최초 완료된 미션 목록
     */
    List<MissionCompletionInfo> evaluateRegion(String usersId);
}
