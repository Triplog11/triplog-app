package triplog.backend.mission.service;

import java.time.LocalDateTime;

/**
 * 일일 미션 생성 기능을 정의하는 도메인 서비스입니다.
 */
public interface DailyMissionService {

    /**
     * 기준 시각에 해당하는 일일 미션 세 개를 중복 없이 생성합니다.
     *
     * @param now 기준 시각
     */
    void ensureDailyMissions(LocalDateTime now);
}
