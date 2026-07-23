package triplog.backend.batch.tourapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TourAPI 관광정보 동기화 범위와 실행 방식을 관리하는 설정입니다.
 *
 * @param festival 축제 검색 기간 설정
 * @param missingThreshold 콘텐츠를 비활성 후보로 판단할 연속 누락 횟수
 * @param landmarkSeedPath 랜드마크 CSV 시드 리소스 경로
 * @param scheduling 자동 실행 설정
 */
@ConfigurationProperties(prefix = "tourism-sync")
public record TourismSyncProperties(
        Festival festival,
        int missingThreshold,
        String landmarkSeedPath,
        Scheduling scheduling
) {

    /**
     * 축제 검색 기간 설정입니다.
     *
     * @param pastDays 기준일 이전 검색 일수
     * @param futureMonths 기준일 이후 검색 개월 수
     */
    public record Festival(int pastDays, int futureMonths) {
    }

    /**
     * 관광정보 동기화 Job의 자동 실행 설정입니다.
     *
     * @param enabled 자동 실행 활성화 여부
     * @param regionCron Region 동기화 cron
     * @param landmarkCron Landmark 동기화 cron
     * @param festivalCron Festival 동기화 cron
     * @param failureRetryCron 실패 이력 재처리 cron
     * @param zone cron 계산에 사용할 시간대
     */
    public record Scheduling(
            boolean enabled,
            String regionCron,
            String landmarkCron,
            String festivalCron,
            String failureRetryCron,
            String zone
    ) {
    }
}
