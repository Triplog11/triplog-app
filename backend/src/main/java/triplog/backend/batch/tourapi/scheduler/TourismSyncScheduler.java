package triplog.backend.batch.tourapi.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 자동 실행 설정이 활성화된 환경에서 등록된 Batch Job을 예약 실행합니다.
 * 개발 기본값은 비활성이며 수동 실행과 같은 Job을 사용합니다.
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "tourism-sync.scheduling", name = "enabled", havingValue = "true")
public class TourismSyncScheduler {

    private final JobOperator jobOperator;
    private final Job regionSyncJob;
    private final Job selectedContentSyncJob;
    private final Job festivalSyncJob;
    private final Job tourismSyncFailureRetryJob;

    /**
     * 공용 JobOperator와 TourAPI 동기화 Job을 주입받습니다.
     *
     * @param jobOperator Batch Job 실행 및 관리 기능
     * @param regionSyncJob 지역 동기화 Job
     * @param selectedContentSyncJob 선정 랜드마크·관광지 동기화 Job
     * @param festivalSyncJob 축제 동기화 Job
     * @param tourismSyncFailureRetryJob 동기화 실패 이력 재처리 Job
     */
    public TourismSyncScheduler(
            JobOperator jobOperator,
            @Qualifier("regionSyncJob") Job regionSyncJob,
            @Qualifier("selectedContentSyncJob") Job selectedContentSyncJob,
            @Qualifier("festivalSyncJob") Job festivalSyncJob,
            @Qualifier("tourismSyncFailureRetryJob") Job tourismSyncFailureRetryJob
    ) {
        this.jobOperator = jobOperator;
        this.regionSyncJob = regionSyncJob;
        this.selectedContentSyncJob = selectedContentSyncJob;
        this.festivalSyncJob = festivalSyncJob;
        this.tourismSyncFailureRetryJob = tourismSyncFailureRetryJob;
    }

    /**
     * 설정된 cron과 시간대에 Region 동기화 Job을 실행합니다.
     */
    @Scheduled(
            cron = "${tourism-sync.scheduling.region-cron}",
            zone = "${tourism-sync.scheduling.zone:Asia/Seoul}"
    )
    public void synchronizeRegions() {
        run(regionSyncJob, "Region");
    }

    /**
     * 설정된 cron과 시간대에 선정 랜드마크·관광지 동기화 Job을 실행합니다.
     */
    @Scheduled(
            cron = "${tourism-sync.scheduling.selected-content-cron}",
            zone = "${tourism-sync.scheduling.zone:Asia/Seoul}"
    )
    public void synchronizeSelectedContents() {
        run(selectedContentSyncJob, "선정 랜드마크·관광지");
    }

    /**
     * 설정된 cron과 시간대에 축제 동기화 Job을 실행합니다.
     */
    @Scheduled(
            cron = "${tourism-sync.scheduling.festival-cron}",
            zone = "${tourism-sync.scheduling.zone:Asia/Seoul}"
    )
    public void synchronizeFestivals() {
        run(festivalSyncJob, "축제");
    }

    /**
     * 설정된 cron과 시간대에 미해결 실패 이력 재처리 Job을 실행합니다.
     */
    @Scheduled(
            cron = "${tourism-sync.scheduling.failure-retry-cron}",
            zone = "${tourism-sync.scheduling.zone:Asia/Seoul}"
    )
    public void retryFailures() {
        run(tourismSyncFailureRetryJob, "TourAPI 실패 재처리");
    }

    /**
     * 현재 시각을 고유 파라미터로 사용해 지정한 배치 작업을 실행합니다.
     *
     * @param job 실행할 Spring Batch 작업
     * @param jobName 로그에 표시할 작업 이름
     */
    private void run(Job job, String jobName) {
        try {
            jobOperator.start(
                    job,
                    new JobParametersBuilder()
                            .addLong("requestedAt", System.currentTimeMillis())
                            .toJobParameters()
            );
        } catch (Exception exception) {
            log.error("{} 예약 동기화 Job 실행 실패", jobName, exception);
        }
    }
}
