package triplog.backend.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.mission.entity.Mission;
import triplog.backend.mission.repository.MissionRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * {@link WeeklyMissionService}의 기본 구현체입니다.
 * 매주 제공할 기본 미션 세 개를 생성합니다.
 */
@Service
@RequiredArgsConstructor
public class WeeklyMissionServiceImpl implements WeeklyMissionService {

    private static final int WEEKLY_MISSION_GROUP = 2;
    private static final int WEEKLY_MISSION_XP = 30;
    private static final String VISIT_FILTER = """
            {"visitType":"ANY"}
            """;
    private static final String FIRST_VISIT_FILTER = """
            {"visitType":"FIRST"}
            """;
    private static final String REVIEW_FILTER = """
            {"tourismContentIds":[]}
            """;

    private final MissionRepository missionRepository;

    /**
     * 기준 시각이 포함된 주의 기본 미션을 중복 없이 생성합니다.
     *
     * @param now 기준 시각
     */
    @Override
    @Transactional
    public void ensureWeeklyMissions(LocalDateTime now) {
        LocalDate monday = now.toLocalDate().with(DayOfWeek.MONDAY);
        LocalDateTime weekStart = monday.atStartOfDay();
        LocalDateTime weekEnd = monday.plusDays(6).atTime(LocalTime.MAX);

        createIfAbsent("여행 한 걸음", MissionTarget.TOURISM_CONTENT_VISIT, VISIT_FILTER, weekStart, weekEnd);
        createIfAbsent("여행 기록 남기기", MissionTarget.REVIEW, REVIEW_FILTER, weekStart, weekEnd);
        createIfAbsent("새로운 곳 발견하기", MissionTarget.TOURISM_CONTENT_VISIT, FIRST_VISIT_FILTER, weekStart, weekEnd);
    }

    /**
     * 동일 주간과 이름의 미션이 없을 때만 생성합니다.
     *
     * @param name      미션 이름
     * @param target    미션 판정 대상
     * @param filter    미션 상세 조건
     * @param weekStart 주간 시작 시각
     * @param weekEnd   주간 종료 시각
     */
    private void createIfAbsent(
            String name,
            MissionTarget target,
            String filter,
            LocalDateTime weekStart,
            LocalDateTime weekEnd
    ) {
        if (!missionRepository.existsByMissionWeekStartAndMissionName(weekStart, name)) {
            missionRepository.save(new Mission(
                    name,
                    WEEKLY_MISSION_GROUP,
                    target.name(),
                    1,
                    filter,
                    weekStart,
                    weekEnd,
                    WEEKLY_MISSION_XP
            ));
        }
    }
}
