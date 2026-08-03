package triplog.backend.batch.tourapi.facade;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TourismSyncFailureRetryFacadeService {

    private static final int IMAGE_PAGE_SIZE = 100;
    private final TourismSyncFailureService failureService;
    private final SelectedContentSyncFacadeService selectedContentSyncFacadeService;
    private final FestivalSyncFacadeService festivalSyncFacadeService;
    private final TourApiClient tourApiClient;
    private final TourismContentService tourismContentService;
    private final TourismContentImageService imageService;
    private final Clock clock;

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

    /**
     * 실패 유형에 맞는 동기화 기능으로 실패 이력 한 건을 재처리합니다.
     *
     * @param failure 재처리할 동기화 실패 이력
     */
    private void retryOne(TourismSyncFailure failure) {
        switch (failure.getSyncType()) {
            case LANDMARK, ATTRACTION -> selectedContentSyncFacadeService.retryOne(
                    failure.getSyncType(),
                    failure.getExternalContentId()
            );
            case FESTIVAL -> festivalSyncFacadeService.retryOne(failure.getExternalContentId());
            case IMAGE -> retryImages(failure.getExternalContentId());
            case REGION -> throw new IllegalStateException("Region 실패는 전체 Region Job으로 재처리해야 합니다.");
        }
    }

    /**
     * 관광 콘텐츠의 이미지 전체를 다시 조회해 동기화합니다.
     *
     * @param contentId 이미지를 재동기화할 TourAPI contentId
     */
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

    /**
     * 실패 이력에 저장할 수 있도록 예외 메시지를 안전한 문자열로 변환합니다.
     *
     * @param exception 메시지를 추출할 예외
     * @return 예외 메시지 또는 예외 클래스명
     */
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
