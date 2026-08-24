package triplog.backend.stats.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;
import triplog.backend.stats.service.StatsService;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonthlyScoreResetSchedulerTest {

    @Mock
    private StatsService statsService;

    @InjectMocks
    private MonthlyScoreResetScheduler scheduler;

    @Test
    @DisplayName("매월 1일 한국 시간 자정에 월간 Score 초기화를 실행한다")
    void resetMonthlyScores() throws NoSuchMethodException {
        // Given
        when(statsService.resetMonthlyScores()).thenReturn(12);
        Method method = MonthlyScoreResetScheduler.class
                .getDeclaredMethod("resetMonthlyScores");
        Scheduled scheduled = method.getAnnotation(Scheduled.class);

        // When
        scheduler.resetMonthlyScores();

        // Then
        verify(statsService).resetMonthlyScores();
        assertThat(scheduled.cron()).isEqualTo("0 0 0 1 * *");
        assertThat(scheduled.zone()).isEqualTo("Asia/Seoul");
    }
}
