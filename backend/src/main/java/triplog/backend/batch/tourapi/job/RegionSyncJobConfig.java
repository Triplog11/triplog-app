package triplog.backend.batch.tourapi.job;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import triplog.backend.batch.tourapi.facade.RegionSyncFacadeService;

/**
 * 전국 법정동 시군구 Region 동기화 Batch Job을 구성합니다.
 */
@Configuration
public class RegionSyncJobConfig {

    /**
     * Region 동기화 Job 설정 객체를 생성합니다.
     */
    public RegionSyncJobConfig() {
    }

    /**
     * Region 동기화 Step을 실행하는 Job을 등록합니다.
     *
     * @param jobRepository Batch 실행 이력 저장소
     * @param regionSyncStep Region 동기화 Step
     * @return Region 동기화 Job
     */
    @Bean
    public Job regionSyncJob(JobRepository jobRepository, Step regionSyncStep) {
        return new JobBuilder("regionSyncJob", jobRepository)
                .start(regionSyncStep)
                .build();
    }

    /**
     * 외부 API 호출을 DB 트랜잭션 밖에서 실행하는 Region 동기화 Step을 등록합니다.
     *
     * @param jobRepository Batch 실행 이력 저장소
     * @param facadeService Region 동기화 유스케이스
     * @return Region 동기화 Step
     */
    @Bean
    public Step regionSyncStep(
            JobRepository jobRepository,
            RegionSyncFacadeService facadeService
    ) {
        return new StepBuilder("regionSyncStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    facadeService.synchronizeAll();
                    return null;
                }, new ResourcelessTransactionManager())
                .build();
    }
}
