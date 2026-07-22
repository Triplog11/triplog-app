package triplog.backend.batch.tourapi.facade;

import org.springframework.stereotype.Service;
import triplog.backend.batch.tourapi.client.TourApiClient;
import triplog.backend.batch.tourapi.dto.TourApiCommonItem;
import triplog.backend.batch.tourapi.dto.TourApiChangedContentItem;
import triplog.backend.batch.tourapi.dto.TourApiPage;
import triplog.backend.batch.tourapi.config.TourismSyncProperties;
import triplog.backend.batch.tourapi.entity.TourismSyncType;
import triplog.backend.batch.tourapi.seed.LandmarkSeed;
import triplog.backend.batch.tourapi.seed.LandmarkSeedReader;
import triplog.backend.batch.tourapi.service.TourismSyncFailureService;
import triplog.backend.batch.tourapi.service.TourismSyncCheckpointService;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.entity.Region;
import triplog.backend.region.service.RegionService;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.service.TourismContentService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CSV 선정 목록, TourAPI 공통정보, Region 및 Landmark 저장 순서를 조합합니다.
 * 외부 호출을 포함하므로 클래스 또는 메서드 수준의 장기 트랜잭션을 선언하지 않습니다.
 */
@Service
public class LandmarkSyncFacadeService {

    private static final int PAGE_SIZE = 100;

    private final LandmarkSeedReader seedReader;
    private final TourApiClient tourApiClient;
    private final RegionService regionService;
    private final TourismContentService tourismContentService;
    private final LandmarkService landmarkService;
    private final TourismSyncFailureService failureService;
    private final TourismSyncCheckpointService checkpointService;
    private final TourismSyncProperties syncProperties;
    private final Clock clock;

    /**
     * 랜드마크 동기화에 필요한 경계 객체와 도메인 서비스를 주입받습니다.
     */
    public LandmarkSyncFacadeService(
            LandmarkSeedReader seedReader,
            TourApiClient tourApiClient,
            RegionService regionService,
            TourismContentService tourismContentService,
            LandmarkService landmarkService,
            TourismSyncFailureService failureService,
            TourismSyncCheckpointService checkpointService,
            TourismSyncProperties syncProperties,
            Clock clock
    ) {
        this.seedReader = seedReader;
        this.tourApiClient = tourApiClient;
        this.regionService = regionService;
        this.tourismContentService = tourismContentService;
        this.landmarkService = landmarkService;
        this.failureService = failureService;
        this.checkpointService = checkpointService;
        this.syncProperties = syncProperties;
        this.clock = clock;
    }

    /**
     * 활성 CSV 행을 순서대로 동기화하며 개별 실패는 이력에 남기고 다음 행을 계속 처리합니다.
     *
     * @return 성공 및 실패 건수
     */
    public LandmarkSyncResult synchronizeInitial() {
        LocalDateTime startedAt = LocalDateTime.now(clock);
        LandmarkSyncResult result = synchronizeSeeds(seedReader.readActiveSeeds());
        if (result.failed() == 0) {
            checkpointService.updateSucceededAt(TourismSyncType.LANDMARK, startedAt);
        }
        return result;
    }

    /**
     * 마지막 성공일 하루 전부터 변경 목록을 조회하고 활성 CSV와 겹치는 항목만 갱신합니다.
     * 체크포인트가 없으면 안전하게 최초 동기화를 수행합니다.
     *
     * @return 성공, 실패 및 변경 대상 제외 건수
     */
    public LandmarkSyncResult synchronizeIncremental() {
        LocalDateTime startedAt = LocalDateTime.now(clock);
        LocalDate modifiedDate = checkpointService.findLastSucceededAt(TourismSyncType.LANDMARK)
                .map(lastSucceededAt -> lastSucceededAt.toLocalDate().minusDays(1))
                .orElse(null);
        if (modifiedDate == null) {
            return synchronizeInitial();
        }

        Map<String, LandmarkSeed> activeSeeds = new LinkedHashMap<>();
        seedReader.readActiveSeeds().forEach(seed -> activeSeeds.put(seed.contentId(), seed));
        Map<String, TourApiChangedContentItem> changedItems = readChangedItems(modifiedDate);
        int skipped = 0;
        int stateUpdateFailures = 0;
        java.util.List<LandmarkSeed> targets = new java.util.ArrayList<>();
        for (TourApiChangedContentItem changedItem : changedItems.values()) {
            LandmarkSeed seed = activeSeeds.get(changedItem.contentId());
            if (seed == null || !"12".equals(changedItem.contentTypeId())) {
                skipped++;
                continue;
            }
            if (changedItem.hidden()) {
                try {
                    tourismContentService.markMissing(
                            seed.contentId(),
                            syncProperties.missingThreshold()
                    );
                } catch (RuntimeException exception) {
                    recordFailure(seed, exception);
                    stateUpdateFailures++;
                }
                skipped++;
                continue;
            }
            targets.add(seed);
        }

        LandmarkSyncResult processed = synchronizeSeeds(targets);
        LandmarkSyncResult result = new LandmarkSyncResult(
                processed.succeeded(),
                processed.failed() + stateUpdateFailures,
                skipped
        );
        if (result.failed() == 0) {
            checkpointService.updateSucceededAt(TourismSyncType.LANDMARK, startedAt);
        }
        return result;
    }

    /**
     * 실패 이력의 랜드마크 한 건을 활성 CSV 기준으로 다시 동기화합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @throws IllegalArgumentException 활성 CSV에 대상이 없는 경우
     */
    public void retryOne(String contentId) {
        LandmarkSeed seed = seedReader.findActiveSeed(contentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "활성 랜드마크 CSV에 contentId가 없습니다: " + contentId
                ));
        synchronizeOne(seed);
    }

    private LandmarkSyncResult synchronizeSeeds(Iterable<LandmarkSeed> seeds) {
        int succeeded = 0;
        int failed = 0;
        for (LandmarkSeed seed : seeds) {
            try {
                synchronizeOne(seed);
                failureService.resolve(TourismSyncType.LANDMARK, seed.contentId(), LocalDateTime.now(clock));
                succeeded++;
            } catch (RuntimeException exception) {
                recordFailure(seed, exception);
                failed++;
            }
        }
        return new LandmarkSyncResult(succeeded, failed, 0);
    }

    private void recordFailure(LandmarkSeed seed, RuntimeException exception) {
        failureService.recordFailure(
                TourismSyncType.LANDMARK,
                seed.contentId(),
                seed.legalRegionCode(),
                seed.legalDistrictCode(),
                exception.getClass().getSimpleName(),
                safeMessage(exception),
                LocalDateTime.now(clock)
        );
    }

    private Map<String, TourApiChangedContentItem> readChangedItems(LocalDate modifiedDate) {
        Map<String, TourApiChangedContentItem> changedItems = new LinkedHashMap<>();
        int pageNumber = 1;
        TourApiPage<TourApiChangedContentItem> page;
        do {
            page = tourApiClient.getChangedContents(modifiedDate, pageNumber, PAGE_SIZE);
            page.items().forEach(item -> changedItems.put(item.contentId(), item));
            pageNumber++;
        } while (!page.isLastPage());
        return changedItems;
    }

    private void synchronizeOne(LandmarkSeed seed) {
        TourApiCommonItem item = tourApiClient.getCommonDetail(seed.contentId());
        validateExpectedValues(seed, item);
        Region region = regionService.findByLegalCode(seed.legalRegionCode(), seed.legalDistrictCode());
        TourismContent content = tourismContentService.upsert(region, item.toSyncData(), LocalDateTime.now(clock));
        landmarkService.upsert(content, seed.displayName());
    }

    private void validateExpectedValues(LandmarkSeed seed, TourApiCommonItem item) {
        if (!seed.expectedContentTypeId().equals(item.contentTypeId())
                || !seed.legalRegionCode().equals(item.legalRegionCode())
                || !seed.legalDistrictCode().equals(item.legalDistrictCode())) {
            throw new IllegalArgumentException("TourAPI 응답이 CSV 예상 타입 또는 지역 코드와 다릅니다.");
        }
    }

    private String safeMessage(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }
        return message.substring(0, Math.min(message.length(), 500));
    }

    /**
     * 랜드마크 최초 동기화 처리 결과입니다.
     *
     * @param succeeded 성공 건수
     * @param failed 실패 건수
     * @param skipped 활성 CSV 대상이 아니거나 비표출되어 상세 조회하지 않은 건수
     */
    public record LandmarkSyncResult(int succeeded, int failed, int skipped) {
    }
}
