package triplog.backend.batch.tourapi.facade;

import org.springframework.stereotype.Service;
import triplog.backend.batch.tourapi.client.TourApiClient;
import triplog.backend.batch.tourapi.entity.TourismSyncFailure;
import triplog.backend.batch.tourapi.entity.TourismSyncType;
import triplog.backend.batch.tourapi.dto.TourApiImageItem;
import triplog.backend.batch.tourapi.dto.TourApiPage;
import triplog.backend.batch.tourapi.service.TourismSyncFailureService;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.service.TourismContentImageService;
import triplog.backend.tourismcontent.service.TourismContentService;
import triplog.backend.tourismcontent.service.TourismContentImageSyncData;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 미해결 동기화 실패를 작업 유형에 맞는 유스케이스로 다시 전달합니다.
 */
@Service
public class TourismSyncFailureRetryFacadeService {

    private static final int IMAGE_PAGE_SIZE = 100;
    private final TourismSyncFailureService failureService;
    private final LandmarkSyncFacadeService landmarkSyncFacadeService;
    private final FestivalSyncFacadeService festivalSyncFacadeService;
    private final TourApiClient tourApiClient;
    private final TourismContentService tourismContentService;
    private final TourismContentImageService imageService;
    private final Clock clock;

    /**
     * 실패 이력과 유형별 재처리에 필요한 서비스를 주입받습니다.
     */
    public TourismSyncFailureRetryFacadeService(
            TourismSyncFailureService failureService,
            LandmarkSyncFacadeService landmarkSyncFacadeService,
            FestivalSyncFacadeService festivalSyncFacadeService,
            TourApiClient tourApiClient,
            TourismContentService tourismContentService,
            TourismContentImageService imageService,
            Clock clock
    ) {
        this.failureService = failureService;
        this.landmarkSyncFacadeService = landmarkSyncFacadeService;
        this.festivalSyncFacadeService = festivalSyncFacadeService;
        this.tourApiClient = tourApiClient;
        this.tourismContentService = tourismContentService;
        this.imageService = imageService;
        this.clock = clock;
    }

    /**
     * 현재 PENDING 상태인 실패를 한 번씩 재처리하고 결과 상태를 반영합니다.
     *
     * @return 해결 및 재실패 건수
     */
    public RetryResult retryPending() {
        int resolved = 0;
        int failed = 0;
        for (TourismSyncFailure failure : failureService.findPendingFailures()) {
            try {
                failureService.markRetrying(
                        failure.getSyncType(),
                        failure.getExternalContentId(),
                        LocalDateTime.now(clock)
                );
                retryOne(failure);
                failureService.resolve(
                        failure.getSyncType(),
                        failure.getExternalContentId(),
                        LocalDateTime.now(clock)
                );
                resolved++;
            } catch (RuntimeException exception) {
                failureService.recordFailure(
                        failure.getSyncType(),
                        failure.getExternalContentId(),
                        failure.getLegalRegionCode(),
                        failure.getLegalDistrictCode(),
                        exception.getClass().getSimpleName(),
                        safeMessage(exception),
                        LocalDateTime.now(clock)
                );
                failed++;
            }
        }
        return new RetryResult(resolved, failed);
    }

    private void retryOne(TourismSyncFailure failure) {
        switch (failure.getSyncType()) {
            case LANDMARK -> landmarkSyncFacadeService.retryOne(failure.getExternalContentId());
            case FESTIVAL -> festivalSyncFacadeService.retryOne(failure.getExternalContentId());
            case IMAGE -> retryImages(failure.getExternalContentId());
            case REGION -> throw new IllegalStateException("Region 실패는 전체 Region Job으로 재처리해야 합니다.");
        }
    }

    private void retryImages(String contentId) {
        TourismContent content = tourismContentService.findByExternalContentId(contentId);
        List<TourismContentImageSyncData> images = new ArrayList<>();
        int pageNumber = 1;
        TourApiPage<TourApiImageItem> page;
        do {
            page = tourApiClient.getImages(contentId, pageNumber, IMAGE_PAGE_SIZE);
            page.items().stream().map(TourApiImageItem::toSyncData)
                    .forEach(images::add);
            pageNumber++;
        } while (!page.isLastPage());
        imageService.synchronize(content, images);
    }

    private String safeMessage(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }
        return message.substring(0, Math.min(message.length(), 500));
    }

    /**
     * 실패 재처리 결과입니다.
     *
     * @param resolved 해결 건수
     * @param failed 재실패 건수
     */
    public record RetryResult(int resolved, int failed) {
    }
}
