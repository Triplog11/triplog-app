package triplog.backend.mission.service;

import triplog.backend.mission.dto.response.MissionResponse.MyMissionListResponse;
import triplog.backend.mission.dto.response.MissionResponse.MissionListResponse;

/**
 * 미션(Mission)과 관련된 비즈니스 로직을 정의하는 Service 인터페이스입니다.
 */
public interface MissionService {

    /**
     * 로그인 사용자의 미션 진행 현황을 조회합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @param missionType 미션 타입 (필터 조건)
     * @return 미션 진행 현황 목록
     */
    MyMissionListResponse getMyMissions(String usersId, String missionType);

    /**
     * 미션 타입별 미션 목록을 조회합니다.
     *
     * @param missionType 미션 타입 (필터 조건)
     * @return 미션 목록
     */
    MissionListResponse getMissions(String missionType);
}
