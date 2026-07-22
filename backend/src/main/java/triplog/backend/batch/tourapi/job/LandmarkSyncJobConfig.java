package triplog.backend.batch.tourapi.job;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import triplog.backend.batch.tourapi.facade.LandmarkSyncFacadeService;

/**
 * CSV 기반 랜드마크 최초 동기화를 수동 또는 예약 실행 가능한 Batch Job으로 구성합니다.
 */
@Configuration
public class LandmarkSyncJobConfig {

    /**
     * 랜드마크 동기화 Step을 단일 Job으로 등록합니다.
     *
     * @param jobRepository Batch 실행 이력 저장소
     * @param landmarkSyncStep 랜드마크 동기화 Step
     * @return 랜드마크 동기화 Job
     */
    @Bean
    public Job landmarkSyncJob(JobRepository jobRepository, Step landmarkSyncStep) {
        return new JobBuilder("landmarkSyncJob", jobRepository)
                .start(landmarkSyncStep)
                .build();
    }

    /**
     * 외부 API 호출을 DB 트랜잭션에 포함하지 않는 랜드마크 동기화 Step을 등록합니다.
     * 실제 도메인 저장은 각 Service의 짧은 트랜잭션으로 처리됩니다.
     *
     * @param jobRepository Batch 실행 이력 저장소
     * @param facadeService 랜드마크 동기화 유스케이스
     * @return 랜드마크 동기화 Step
     */
    @Bean
    public Step landmarkSyncStep(
            JobRepository jobRepository,
            LandmarkSyncFacadeService facadeService
    ) {
        return new StepBuilder("landmarkSyncStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Object modeValue = chunkContext.getStepContext()
                            .getJobParameters()
                            .getOrDefault("mode", "INCREMENTAL");
                    LandmarkSyncFacadeService.LandmarkSyncResult result = "INITIAL".equalsIgnoreCase(
                            String.valueOf(modeValue)
                    )
                            ? facadeService.synchronizeInitial()
                            : facadeService.synchronizeIncremental();
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
