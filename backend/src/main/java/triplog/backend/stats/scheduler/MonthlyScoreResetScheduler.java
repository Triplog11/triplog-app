package triplog.backend.stats.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import triplog.backend.stats.service.StatsService;

/**
 * 월간 랭킹용 Score 초기화를 정해진 시각에 실행합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlyScoreResetScheduler {

    private final StatsService statsService;

    /**
     * 매월 1일 00:00(한국 시간)에 월간 Score만 초기화합니다.
     */
    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
    public void resetMonthlyScores() {
        int resetCount = statsService.resetMonthlyScores();
        log.info("월간 Score 초기화 완료: resetCount={}", resetCount);
    }
}
