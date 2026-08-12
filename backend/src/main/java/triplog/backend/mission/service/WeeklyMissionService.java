package triplog.backend.mission.service;

import java.time.LocalDateTime;

/**
 * 주간 미션 생성 기능을 정의하는 도메인 서비스입니다.
 */
public interface WeeklyMissionService {

    /**
     * 기준 시각이 포함된 주의 기본 미션을 중복 없이 생성합니다.
     *
     * @param now 기준 시각
     */
    void ensureWeeklyMissions(LocalDateTime now);
}
