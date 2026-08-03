package triplog.backend.batch.tourapi.job;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import triplog.backend.batch.tourapi.facade.FestivalSyncFacadeService;

import java.time.Clock;
import java.time.LocalDate;

/**
 * 설정 기간의 전국 축제를 동기화하는 Batch Job을 구성합니다.
 */
@Configuration
public class FestivalSyncJobConfig {

    /**
     * 축제 동기화 Job 설정 객체를 생성합니다.
     */
    public FestivalSyncJobConfig() {
    }

    /**
     * 축제 동기화 Step을 실행하는 Job을 등록합니다.
     *
     * @param jobRepository Batch 실행 이력 저장소
     * @param festivalSyncStep 축제 동기화 Step
     * @return 축제 동기화 Job
     */
    @Bean
    public Job festivalSyncJob(JobRepository jobRepository, Step festivalSyncStep) {
        return new JobBuilder("festivalSyncJob", jobRepository)
                .start(festivalSyncStep)
                .build();
    }

    /**
     * 외부 API 호출을 DB 트랜잭션 밖에서 실행하는 축제 동기화 Step을 등록합니다.
     * Job 파라미터 {@code baseDate}가 없으면 애플리케이션 현재 날짜를 사용합니다.
     *
     * @param jobRepository Batch 실행 이력 저장소
     * @param facadeService 축제 동기화 유스케이스
     * @param clock 애플리케이션 공용 시계
     * @return 축제 동기화 Step
     */
    @Bean
    public Step festivalSyncStep(
            JobRepository jobRepository,
            FestivalSyncFacadeService facadeService,
            Clock clock
    ) {
        return new StepBuilder("festivalSyncStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Object baseDateValue = chunkContext.getStepContext()
                            .getJobParameters()
                            .get("baseDate");
                    LocalDate baseDate = baseDateValue == null
                            ? LocalDate.now(clock)
                            : LocalDate.parse(String.valueOf(baseDateValue));
                    FestivalSyncFacadeService.FestivalSyncResult result = facadeService.synchronize(baseDate);
                    if (result.failed() > 0) {
                        contribution.setExitStatus(new ExitStatus(
                                "COMPLETED_WITH_FAILURES",
                                "failed=" + result.failed()
                        ));
                    }
                    return null;
                }, new ResourcelessTransactionManager())
                .build();
    }
}
