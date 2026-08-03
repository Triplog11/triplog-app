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
import triplog.backend.batch.tourapi.facade.SelectedContentSyncFacadeService;

/**
 * CSV로 선정한 랜드마크와 일반 관광지 동기화 Batch Job을 구성합니다.
 */
@Configuration
public class SelectedContentSyncJobConfig {

    /**
     * 선정 랜드마크·일반 관광지 동기화 Job 설정 객체를 생성합니다.
     */
    public SelectedContentSyncJobConfig() {
    }

    /**
     * 선정 랜드마크·일반 관광지 동기화 Step을 실행하는 Job을 등록합니다.
     *
     * @param jobRepository Batch 실행 이력 저장소
     * @param selectedContentSyncStep 선정 콘텐츠 동기화 Step
     * @return 선정 콘텐츠 동기화 Job
     */
    @Bean
    public Job selectedContentSyncJob(
            JobRepository jobRepository,
            Step selectedContentSyncStep
    ) {
        return new JobBuilder("selectedContentSyncJob", jobRepository)
                .start(selectedContentSyncStep)
                .build();
    }

    /**
     * INITIAL 또는 INCREMENTAL 모드로 선정 콘텐츠 Facade를 실행하는 Step을 등록합니다.
     * 외부 API 호출은 리소스리스 트랜잭션에서 수행하고 실제 저장은 도메인 서비스가 담당합니다.
     *
     * @param jobRepository Batch 실행 이력 저장소
     * @param facadeService 선정 콘텐츠 동기화 유스케이스
     * @return 선정 콘텐츠 동기화 Step
     */
    @Bean
    public Step selectedContentSyncStep(
            JobRepository jobRepository,
            SelectedContentSyncFacadeService facadeService
    ) {
        return new StepBuilder("selectedContentSyncStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Object modeValue = chunkContext.getStepContext()
                            .getJobParameters()
                            .getOrDefault("mode", "INCREMENTAL");
                    SelectedContentSyncFacadeService.SelectedContentSyncResult result =
                            "INITIAL".equalsIgnoreCase(String.valueOf(modeValue))
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
