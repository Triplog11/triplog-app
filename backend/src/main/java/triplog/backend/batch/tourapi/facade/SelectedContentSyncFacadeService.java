package triplog.backend.batch.tourapi.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import triplog.backend.attraction.service.AttractionService;
import triplog.backend.batch.tourapi.client.TourApiClient;
import triplog.backend.batch.tourapi.config.TourismSyncProperties;
import triplog.backend.batch.tourapi.dto.TourApiChangedContentItem;
import triplog.backend.batch.tourapi.dto.TourApiCommonItem;
import triplog.backend.batch.tourapi.dto.TourApiPage;
import triplog.backend.batch.tourapi.entity.TourismSyncType;
import triplog.backend.batch.tourapi.seed.SelectedContentSeedReader;
import triplog.backend.batch.tourapi.seed.SelectedContentSeeds;
import triplog.backend.batch.tourapi.service.TourismSyncCheckpointService;
import triplog.backend.batch.tourapi.service.TourismSyncFailureService;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.entity.Region;
import triplog.backend.region.service.RegionService;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.service.TourismContentService;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 선정 CSV와 TourAPI 공통정보를 조합해 랜드마크와 일반 관광지를 동기화합니다.
 * 외부 API 호출을 포함하므로 장기 트랜잭션을 선언하지 않습니다.
 */
@Service
@RequiredArgsConstructor
public class SelectedContentSyncFacadeService {

    private static final int PAGE_SIZE = 100;
    private static final Set<String> SELECTED_CONTENT_TYPE_IDS = Set.of("12", "14", "28");

    private final SelectedContentSeedReader seedReader;
    private final TourApiClient tourApiClient;
    private final RegionService regionService;
    private final TourismContentService tourismContentService;
    private final LandmarkService landmarkService;
    private final AttractionService attractionService;
    private final TourismSyncFailureService failureService;
    private final TourismSyncCheckpointService checkpointService;
    private final TourismSyncProperties syncProperties;
    private final Clock clock;

    /**
     * 두 CSV의 모든 선정 콘텐츠를 상세 조회해 최초 적재합니다.
     *
     * @return 성공·실패·건너뜀 건수
     */
    public SelectedContentSyncResult synchronizeInitial() {
        LocalDateTime startedAt = LocalDateTime.now(clock);
        SelectedContentSeeds seeds = seedReader.read();
        SelectedContentSyncResult result = synchronizeContentIds(seeds.allContentIds(), seeds);
        updateCheckpointsWhenSucceeded(result, startedAt);
        return result;
    }

    /**
     * 마지막 성공일 이후 변경된 항목과 두 선정 CSV의 합집합을 동기화합니다.
     *
     * @return 성공·실패·건너뜀 건수
     */
    public SelectedContentSyncResult synchronizeIncremental() {
        SelectedContentSeeds seeds = seedReader.read();
        LocalDate modifiedDate = earliestCheckpointDate();
        if (modifiedDate == null) {
            return synchronizeInitial();
        }

        LocalDateTime startedAt = LocalDateTime.now(clock);
        Map<String, TourApiChangedContentItem> changedItems = readChangedItems(modifiedDate);
        int succeeded = 0;
        int failed = 0;
        int skipped = 0;
        for (TourApiChangedContentItem changedItem : changedItems.values()) {
            String contentId = changedItem.contentId();
            if (!seeds.allContentIds().contains(contentId)
                    || !SELECTED_CONTENT_TYPE_IDS.contains(changedItem.contentTypeId())) {
                skipped++;
                continue;
            }
            TourismSyncType syncType = resolveSyncType(contentId, seeds);
            try {
                if (changedItem.hidden()) {
                    tourismContentService.markMissing(contentId, syncProperties.missingThreshold());
                    skipped++;
                    continue;
                }
                synchronizeOne(contentId, syncType);
                failureService.resolve(syncType, contentId, LocalDateTime.now(clock));
                succeeded++;
            } catch (RuntimeException exception) {
                recordFailure(syncType, contentId, null, null, exception);
                failed++;
            }
        }

        SelectedContentSyncResult result = new SelectedContentSyncResult(succeeded, failed, skipped);
        updateCheckpointsWhenSucceeded(result, startedAt);
        return result;
    }

    /**
     * 실패 이력 한 건을 지정된 선정 유형으로 재동기화합니다.
     *
     * @param syncType LANDMARK 또는 ATTRACTION 동기화 유형
     * @param contentId TourAPI 콘텐츠 식별자
     * @throws IllegalArgumentException 유형과 CSV 선정 목록이 일치하지 않는 경우
     */
    public void retryOne(TourismSyncType syncType, String contentId) {
        SelectedContentSeeds seeds = seedReader.read();
        if (syncType == TourismSyncType.LANDMARK && !seeds.isLandmark(contentId)) {
            throw new IllegalArgumentException("랜드마크 CSV에 contentId가 없습니다: " + contentId);
        }
        if (syncType == TourismSyncType.ATTRACTION && !seeds.isAttraction(contentId)) {
            throw new IllegalArgumentException("관광지 CSV에 contentId가 없습니다: " + contentId);
        }
        synchronizeOne(contentId, syncType);
    }

    /**
     * 전달받은 선정 contentId를 순회하며 개별 동기화 결과를 집계합니다.
     *
     * @param contentIds 동기화할 선정 contentId
     * @param seeds 랜드마크·일반 관광지 분류 기준
     * @return 성공·실패·건너뜀 건수
     */
    private SelectedContentSyncResult synchronizeContentIds(
            Set<String> contentIds,
            SelectedContentSeeds seeds
    ) {
        int succeeded = 0;
        int failed = 0;
        for (String contentId : contentIds) {
            TourismSyncType syncType = resolveSyncType(contentId, seeds);
            try {
                synchronizeOne(contentId, syncType);
                failureService.resolve(syncType, contentId, LocalDateTime.now(clock));
                succeeded++;
            } catch (RuntimeException exception) {
                recordFailure(syncType, contentId, null, null, exception);
                failed++;
            }
        }
        return new SelectedContentSyncResult(succeeded, failed, 0);
    }

    /**
     * 선정 콘텐츠 한 건의 공통정보와 유형별 하위 엔티티를 동기화합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @param syncType LANDMARK 또는 ATTRACTION 동기화 유형
     * @throws IllegalArgumentException 지원하지 않는 콘텐츠 유형 또는 동기화 유형인 경우
     */
    private void synchronizeOne(String contentId, TourismSyncType syncType) {
        TourApiCommonItem item = tourApiClient.getCommonDetail(contentId);
        validateContentType(item);
        Region region = regionService.findByLegalCode(
                item.legalRegionCode(),
                item.legalDistrictCode()
        );
        TourismContent content = tourismContentService.upsert(
                region,
                item.toSyncData(),
                LocalDateTime.now(clock)
        );
        if (syncType == TourismSyncType.LANDMARK) {
            landmarkService.upsert(content, item.title());
            return;
        }
        if (syncType == TourismSyncType.ATTRACTION) {
            attractionService.upsert(content);
            return;
        }
        throw new IllegalArgumentException("지원하지 않는 선정 콘텐츠 유형입니다: " + syncType);
    }

    /**
     * contentId가 등록된 CSV를 기준으로 동기화 유형을 결정합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @param seeds 랜드마크·일반 관광지 분류 기준
     * @return LANDMARK 또는 ATTRACTION 동기화 유형
     * @throws IllegalArgumentException 두 CSV 어디에도 contentId가 없는 경우
     */
    private TourismSyncType resolveSyncType(String contentId, SelectedContentSeeds seeds) {
        if (seeds.isLandmark(contentId)) {
            return TourismSyncType.LANDMARK;
        }
        if (seeds.isAttraction(contentId)) {
            return TourismSyncType.ATTRACTION;
        }
        throw new IllegalArgumentException("선정 CSV에 contentId가 없습니다: " + contentId);
    }

    /**
     * 두 선정 유형의 체크포인트 중 더 이른 날짜에서 하루를 뺀 증분 기준일을 계산합니다.
     *
     * @return 증분 조회 기준일 또는 체크포인트가 하나라도 없으면 null
     */
    private LocalDate earliestCheckpointDate() {
        LocalDateTime landmarkCheckpoint = checkpointService
                .findLastSucceededAt(TourismSyncType.LANDMARK)
                .orElse(null);
        LocalDateTime attractionCheckpoint = checkpointService
                .findLastSucceededAt(TourismSyncType.ATTRACTION)
                .orElse(null);
        if (landmarkCheckpoint == null || attractionCheckpoint == null) {
            return null;
        }
        LocalDateTime earliest = landmarkCheckpoint.isBefore(attractionCheckpoint)
                ? landmarkCheckpoint
                : attractionCheckpoint;
        return earliest.toLocalDate().minusDays(1);
    }

    /**
     * 기준일 이후 변경된 관광 콘텐츠를 모든 페이지에서 조회해 contentId별로 정리합니다.
     *
     * @param modifiedDate TourAPI 변경 목록 조회 기준일
     * @return contentId를 키로 하는 변경 항목 Map
     */
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

    /**
     * TourAPI 상세 응답이 랜드마크·일반 관광지 허용 유형인지 검증합니다.
     *
     * @param item TourAPI 공통정보 응답
     * @throws IllegalArgumentException contentTypeId가 12, 14, 28 중 하나가 아닌 경우
     */
    private void validateContentType(TourApiCommonItem item) {
        if (!SELECTED_CONTENT_TYPE_IDS.contains(item.contentTypeId())) {
            throw new IllegalArgumentException(
                    "랜드마크와 관광지는 TourAPI contentTypeId 12, 14, 28만 등록할 수 있습니다."
            );
        }
    }

    /**
     * 전체 처리가 성공한 경우 두 선정 유형의 체크포인트를 같은 시각으로 갱신합니다.
     *
     * @param result 동기화 집계 결과
     * @param startedAt 동기화 실행 시작 시각
     */
    private void updateCheckpointsWhenSucceeded(
            SelectedContentSyncResult result,
            LocalDateTime startedAt
    ) {
        if (result.failed() == 0) {
            checkpointService.updateSucceededAt(TourismSyncType.LANDMARK, startedAt);
            checkpointService.updateSucceededAt(TourismSyncType.ATTRACTION, startedAt);
        }
    }

    /**
     * 선정 콘텐츠 한 건의 최종 실패 정보를 유형별 실패 이력에 기록합니다.
     *
     * @param syncType 실패한 선정 콘텐츠 유형
     * @param contentId TourAPI 콘텐츠 식별자
     * @param legalRegionCode 법정동 시도 코드
     * @param legalDistrictCode 법정동 시군구 코드
     * @param exception 발생한 예외
     */
    private void recordFailure(
            TourismSyncType syncType,
            String contentId,
            String legalRegionCode,
            String legalDistrictCode,
            RuntimeException exception
    ) {
        failureService.recordFailure(
                syncType,
                contentId,
                legalRegionCode,
                legalDistrictCode,
                exception.getClass().getSimpleName(),
                safeMessage(exception),
                LocalDateTime.now(clock)
        );
    }

    /**
     * 실패 이력 컬럼 길이에 맞게 예외 메시지를 안전하게 변환합니다.
     *
     * @param exception 발생한 예외
     * @return 최대 500자의 예외 메시지 또는 예외 클래스명
     */
    private String safeMessage(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }
        return message.substring(0, Math.min(message.length(), 500));
    }

    /**
     * 선정 랜드마크·일반 관광지 동기화 집계 결과입니다.
     *
     * @param succeeded 성공 건수
     * @param failed 실패 건수
     * @param skipped 선정 대상이 아니거나 비표출되어 건너뛴 건수
     */
    public record SelectedContentSyncResult(int succeeded, int failed, int skipped) {
    }
}
