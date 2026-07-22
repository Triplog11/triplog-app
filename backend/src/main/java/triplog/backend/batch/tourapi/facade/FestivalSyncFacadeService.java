package triplog.backend.batch.tourapi.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import triplog.backend.batch.tourapi.client.TourApiClient;
import triplog.backend.batch.tourapi.config.TourismSyncProperties;
import triplog.backend.batch.tourapi.dto.TourApiCommonItem;
import triplog.backend.batch.tourapi.dto.TourApiEventIntroItem;
import triplog.backend.batch.tourapi.dto.TourApiFestivalItem;
import triplog.backend.batch.tourapi.dto.TourApiImageItem;
import triplog.backend.batch.tourapi.dto.TourApiPage;
import triplog.backend.batch.tourapi.entity.TourismSyncType;
import triplog.backend.batch.tourapi.service.TourismSyncCheckpointService;
import triplog.backend.batch.tourapi.service.TourismSyncFailureService;
import triplog.backend.event.service.EventService;
import triplog.backend.region.entity.Region;
import triplog.backend.region.service.RegionService;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.service.TourismContentImageService;
import triplog.backend.tourismcontent.service.TourismContentService;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 축제 검색, 공통·소개·이미지 조회와 각 도메인 저장 순서를 조합합니다.
 * 외부 API 호출을 포함하므로 장기 DB 트랜잭션을 선언하지 않습니다.
 */
@Service
@Slf4j
public class FestivalSyncFacadeService {

    private static final String FESTIVAL_CONTENT_TYPE_ID = "15";
    private static final int PAGE_SIZE = 100;
    private static final int PROGRESS_LOG_INTERVAL = 10;
    private static final DateTimeFormatter PROVIDER_DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final TourApiClient tourApiClient;
    private final RegionService regionService;
    private final TourismContentService tourismContentService;
    private final EventService eventService;
    private final TourismContentImageService imageService;
    private final TourismSyncFailureService failureService;
    private final TourismSyncCheckpointService checkpointService;
    private final TourismSyncProperties properties;
    private final Clock clock;

    /**
     * 축제 동기화에 필요한 외부 Client, 도메인 서비스 및 설정을 주입받습니다.
     */
    public FestivalSyncFacadeService(
            TourApiClient tourApiClient,
            RegionService regionService,
            TourismContentService tourismContentService,
            EventService eventService,
            TourismContentImageService imageService,
            TourismSyncFailureService failureService,
            TourismSyncCheckpointService checkpointService,
            TourismSyncProperties properties,
            Clock clock
    ) {
        this.tourApiClient = tourApiClient;
        this.regionService = regionService;
        this.tourismContentService = tourismContentService;
        this.eventService = eventService;
        this.imageService = imageService;
        this.failureService = failureService;
        this.checkpointService = checkpointService;
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * 기준일의 과거·미래 설정 범위에 포함되는 전국 축제를 동기화합니다.
     *
     * @param baseDate 검색 기간 기준일
     * @return 성공, 실패 및 변경 없음 건수
     */
    public FestivalSyncResult synchronize(LocalDate baseDate) {
        LocalDateTime startedAt = LocalDateTime.now(clock);
        LocalDate startDate = baseDate.minusDays(properties.festival().pastDays());
        LocalDate endDate = baseDate.plusMonths(properties.festival().futureMonths());
        Map<String, TourApiFestivalItem> festivals = readFestivals(startDate, endDate);
        int succeeded = 0;
        int failed = 0;
        int skipped = 0;
        int processed = 0;

        log.info("축제 상세 동기화 시작: totalCount={}", festivals.size());

        for (TourApiFestivalItem festival : festivals.values()) {
            if (!FESTIVAL_CONTENT_TYPE_ID.equals(festival.contentTypeId())) {
                skipped++;
                processed++;
                logProgress(processed, festivals.size(), succeeded, failed, skipped);
                continue;
            }
            if (isUnchanged(festival)) {
                skipped++;
                processed++;
                logProgress(processed, festivals.size(), succeeded, failed, skipped);
                continue;
            }
            try {
                synchronizeOne(festival);
                failureService.resolve(TourismSyncType.FESTIVAL, festival.contentId(), LocalDateTime.now(clock));
                succeeded++;
            } catch (RuntimeException exception) {
                recordFailure(festival, exception);
                failed++;
            }
            processed++;
            logProgress(processed, festivals.size(), succeeded, failed, skipped);
        }

        if (failed == 0) {
            checkpointService.updateSucceededAt(TourismSyncType.FESTIVAL, startedAt);
        }
        return new FestivalSyncResult(succeeded, failed, skipped);
    }

    private void logProgress(int processed, int total, int succeeded, int failed, int skipped) {
        if (processed % PROGRESS_LOG_INTERVAL == 0 || processed == total) {
            log.info(
                    "축제 상세 동기화 진행: processed={}, total={}, succeeded={}, failed={}, skipped={}",
                    processed,
                    total,
                    succeeded,
                    failed,
                    skipped
            );
        }
    }

    /**
     * 실패 이력의 축제 한 건을 공통·소개·이미지 API에서 다시 조회합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     */
    public void retryOne(String contentId) {
        synchronizeOne(contentId);
    }

    private Map<String, TourApiFestivalItem> readFestivals(LocalDate startDate, LocalDate endDate) {
        Map<String, TourApiFestivalItem> festivals = new LinkedHashMap<>();
        LocalDate windowStartDate = startDate;
        while (!windowStartDate.isAfter(endDate)) {
            LocalDate windowEndDate = windowStartDate.plusMonths(1).minusDays(1);
            if (windowEndDate.isAfter(endDate)) {
                windowEndDate = endDate;
            }
            log.info(
                    "축제 목록 구간 조회 시작: startDate={}, endDate={}",
                    windowStartDate,
                    windowEndDate
            );
            readFestivalWindow(windowStartDate, windowEndDate, festivals);
            log.info(
                    "축제 목록 구간 조회 완료: startDate={}, endDate={}, accumulatedCount={}",
                    windowStartDate,
                    windowEndDate,
                    festivals.size()
            );
            windowStartDate = windowEndDate.plusDays(1);
        }
        return festivals;
    }

    private void readFestivalWindow(
            LocalDate startDate,
            LocalDate endDate,
            Map<String, TourApiFestivalItem> festivals
    ) {
        int pageNumber = 1;
        TourApiPage<TourApiFestivalItem> page;
        do {
            page = tourApiClient.searchFestivals(startDate, endDate, pageNumber, PAGE_SIZE);
            page.items().forEach(item -> festivals.put(item.contentId(), item));
            pageNumber++;
        } while (!page.isLastPage());
    }

    private boolean isUnchanged(TourApiFestivalItem festival) {
        if (festival.modifiedTime() == null || festival.modifiedTime().isBlank()) {
            return false;
        }
        LocalDateTime modifiedAt = LocalDateTime.parse(festival.modifiedTime(), PROVIDER_DATE_TIME);
        return tourismContentService.findOptionalByExternalContentId(festival.contentId())
                .map(TourismContent::getProviderModifiedAt)
                .map(modifiedAt::equals)
                .orElse(false);
    }

    private void synchronizeOne(TourApiFestivalItem festival) {
        synchronizeOne(festival.contentId());
    }

    private void synchronizeOne(String contentId) {
        TourApiCommonItem commonItem = tourApiClient.getCommonDetail(contentId);
        if (!FESTIVAL_CONTENT_TYPE_ID.equals(commonItem.contentTypeId())) {
            throw new IllegalArgumentException("축제 contentTypeId는 15여야 합니다.");
        }
        Region region = regionService.findByLegalCode(
                commonItem.legalRegionCode(),
                commonItem.legalDistrictCode()
        );
        TourApiEventIntroItem introItem = tourApiClient.getIntroDetail(
                contentId,
                FESTIVAL_CONTENT_TYPE_ID
        );
        TourismContent content = tourismContentService.upsert(
                region,
                commonItem.toSyncData(),
                LocalDateTime.now(clock)
        );
        eventService.upsert(content, introItem.toSyncData());
        imageService.synchronize(content, readImages(contentId).stream()
                .map(TourApiImageItem::toSyncData)
                .toList());
    }

    private List<TourApiImageItem> readImages(String contentId) {
        List<TourApiImageItem> images = new ArrayList<>();
        int pageNumber = 1;
        TourApiPage<TourApiImageItem> page;
        do {
            page = tourApiClient.getImages(contentId, pageNumber, PAGE_SIZE);
            images.addAll(page.items());
            pageNumber++;
        } while (!page.isLastPage());
        return images;
    }

    private void recordFailure(TourApiFestivalItem festival, RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = exception.getClass().getSimpleName();
        }
        failureService.recordFailure(
                TourismSyncType.FESTIVAL,
                festival.contentId(),
                festival.legalRegionCode(),
                festival.legalDistrictCode(),
                exception.getClass().getSimpleName(),
                message.substring(0, Math.min(message.length(), 500)),
                LocalDateTime.now(clock)
        );
    }

    /**
     * 축제 동기화 처리 결과입니다.
     *
     * @param succeeded 성공 건수
     * @param failed 실패 건수
     * @param skipped 타입 불일치 또는 변경 없음으로 상세 조회하지 않은 건수
     */
    public record FestivalSyncResult(int succeeded, int failed, int skipped) {
    }
}
