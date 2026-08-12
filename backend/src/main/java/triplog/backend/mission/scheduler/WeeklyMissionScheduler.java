package triplog.backend.mission.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import triplog.backend.mission.service.WeeklyMissionService;

import java.time.LocalDateTime;

/**
 * 주간 미션 생성을 실행하는 스케줄러입니다.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "mission.scheduling.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class WeeklyMissionScheduler {

    private final WeeklyMissionService weeklyMissionService;

    /**
     * 애플리케이션 시작 시 현재 주의 기본 미션을 생성합니다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeWeeklyMissions() {
        weeklyMissionService.ensureWeeklyMissions(LocalDateTime.now());
    }

    /**
     * 매주 월요일 자정에 해당 주의 기본 미션을 생성합니다.
     */
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void createWeeklyMissions() {
        weeklyMissionService.ensureWeeklyMissions(LocalDateTime.now());
    }
}
