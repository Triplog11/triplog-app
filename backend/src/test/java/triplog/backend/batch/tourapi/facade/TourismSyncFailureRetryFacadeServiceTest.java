package triplog.backend.batch.tourapi.facade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.batch.tourapi.client.TourApiClient;
import triplog.backend.batch.tourapi.entity.TourismSyncFailure;
import triplog.backend.batch.tourapi.entity.TourismSyncType;
import triplog.backend.batch.tourapi.service.TourismSyncFailureService;
import triplog.backend.tourismcontent.service.TourismContentImageService;
import triplog.backend.tourismcontent.service.TourismContentService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TourismSyncFailureRetryFacadeServiceTest {

    @Mock private TourismSyncFailureService failureService;
    @Mock private SelectedContentSyncFacadeService selectedContentSyncFacadeService;
    @Mock private FestivalSyncFacadeService festivalSyncFacadeService;
    @Mock private TourApiClient tourApiClient;
    @Mock private TourismContentService tourismContentService;
    @Mock private TourismContentImageService imageService;

    @Test
    @DisplayName("랜드마크 실패를 재처리하고 성공하면 해결 상태로 변경한다")
    void 랜드마크_실패를_재처리하고_성공하면_해결_상태로_변경한다() {
        // Given
        TourismSyncFailure failure = new TourismSyncFailure(
                TourismSyncType.LANDMARK,
                "126508",
                "11",
                "110",
                "HTTP_REQUEST_FAILED",
                "요청 실패",
                LocalDateTime.of(2026, 7, 20, 9, 0)
        );
        when(failureService.findPendingFailures()).thenReturn(List.of(failure));
        Clock clock = Clock.fixed(
                Instant.parse("2026-07-21T00:00:00Z"),
                ZoneId.of("Asia/Seoul")
        );
        TourismSyncFailureRetryFacadeService facadeService = new TourismSyncFailureRetryFacadeService(
                failureService,
                selectedContentSyncFacadeService,
                festivalSyncFacadeService,
                tourApiClient,
                tourismContentService,
                imageService,
                clock
        );

        // When
        TourismSyncFailureRetryFacadeService.RetryResult result = facadeService.retryPending();

        // Then
        verify(selectedContentSyncFacadeService).retryOne(TourismSyncType.LANDMARK, "126508");
        verify(failureService).markRetrying(
                TourismSyncType.LANDMARK,
                "126508",
                LocalDateTime.of(2026, 7, 21, 9, 0)
        );
        verify(failureService).resolve(
                TourismSyncType.LANDMARK,
                "126508",
                LocalDateTime.of(2026, 7, 21, 9, 0)
        );
        assertThat(result).isEqualTo(new TourismSyncFailureRetryFacadeService.RetryResult(1, 0));
    }
}
