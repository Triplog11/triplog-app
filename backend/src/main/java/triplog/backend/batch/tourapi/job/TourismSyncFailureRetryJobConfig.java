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
import triplog.backend.batch.tourapi.facade.TourismSyncFailureRetryFacadeService;

/**
 * 미해결 TourAPI 동기화 실패를 재처리하는 Batch Job을 구성합니다.
 */
@Configuration
public class TourismSyncFailureRetryJobConfig {

    /**
     * TourAPI 동기화 실패 재처리 Job 설정 객체를 생성합니다.
     */
    public TourismSyncFailureRetryJobConfig() {
    }

    /**
     * 실패 재처리 Step을 실행하는 Job을 등록합니다.
     *
     * @param jobRepository Batch 실행 이력 저장소
     * @param tourismSyncFailureRetryStep 실패 재처리 Step
     * @return 실패 재처리 Job
     */
    @Bean
    public Job tourismSyncFailureRetryJob(
            JobRepository jobRepository,
            Step tourismSyncFailureRetryStep
    ) {
        return new JobBuilder("tourismSyncFailureRetryJob", jobRepository)
                .start(tourismSyncFailureRetryStep)
                .build();
    }

    /**
     * 외부 API 호출을 DB 트랜잭션 밖에서 실행하는 실패 재처리 Step을 등록합니다.
     *
     * @param jobRepository Batch 실행 이력 저장소
     * @param facadeService 실패 재처리 유스케이스
     * @return 실패 재처리 Step
     */
    @Bean
    public Step tourismSyncFailureRetryStep(
            JobRepository jobRepository,
            TourismSyncFailureRetryFacadeService facadeService
    ) {
        return new StepBuilder("tourismSyncFailureRetryStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    TourismSyncFailureRetryFacadeService.RetryResult result = facadeService.retryPending();
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
