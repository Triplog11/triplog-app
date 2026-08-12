package triplog.backend.mission.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import triplog.backend.mission.service.DailyMissionService;

import java.time.LocalDateTime;

/**
 * 일일 미션 생성을 실행하는 스케줄러입니다.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "mission.scheduling.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class DailyMissionScheduler {

    private final DailyMissionService dailyMissionService;

    /**
     * 애플리케이션 시작 시 현재 날짜의 일일 미션을 생성합니다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeDailyMissions() {
        dailyMissionService.ensureDailyMissions(LocalDateTime.now());
    }

    /**
     * 매일 자정에 해당 날짜의 일일 미션을 생성합니다.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void createDailyMissions() {
        dailyMissionService.ensureDailyMissions(LocalDateTime.now());
    }
}
