package triplog.backend.batch.tourapi.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.batch.tourapi.client.TourApiClient;
import triplog.backend.batch.tourapi.config.TourismSyncProperties;
import triplog.backend.batch.tourapi.dto.TourApiFestivalItem;
import triplog.backend.batch.tourapi.dto.TourApiPage;
import triplog.backend.batch.tourapi.entity.TourismSyncType;
import triplog.backend.batch.tourapi.service.TourismSyncCheckpointService;
import triplog.backend.batch.tourapi.service.TourismSyncFailureService;
import triplog.backend.event.service.EventService;
import triplog.backend.region.service.RegionService;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.service.TourismContentImageService;
import triplog.backend.tourismcontent.service.TourismContentService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FestivalSyncFacadeServiceTest {

    @Mock private TourApiClient tourApiClient;
    @Mock private RegionService regionService;
    @Mock private TourismContentService tourismContentService;
    @Mock private EventService eventService;
    @Mock private TourismContentImageService imageService;
    @Mock private TourismSyncFailureService failureService;
    @Mock private TourismSyncCheckpointService checkpointService;
    @Mock private TourismContent tourismContent;

    private FestivalSyncFacadeService facadeService;

    @BeforeEach
    void setUp() {
        TourismSyncProperties properties = new TourismSyncProperties(
                new TourismSyncProperties.Festival(30, 12),
                3,
                "classpath:seed/landmarks.csv",
                new TourismSyncProperties.Scheduling(false, "", "", "", "", "Asia/Seoul")
        );
        facadeService = new FestivalSyncFacadeService(
                tourApiClient,
                regionService,
                tourismContentService,
                eventService,
                imageService,
                failureService,
                checkpointService,
                properties,
                Clock.fixed(Instant.parse("2026-07-21T00:00:00Z"), ZoneId.of("Asia/Seoul"))
        );
        when(tourApiClient.searchFestivals(any(), any(), eq(1), eq(100)))
                .thenReturn(new TourApiPage<>(List.of(), 1, 100, 0));
    }

    @Test
    @DisplayName("수정시각이 같은 기존 축제는 상세 API를 호출하지 않는다")
    void 수정시각이_같은_기존_축제는_상세_API를_호출하지_않는다() {
        // Given
        TourApiFestivalItem festival = new TourApiFestivalItem(
                "300001", "15", "20260721090000", "20260720", "20260725", "11", "110"
        );
        when(tourApiClient.searchFestivals(
                LocalDate.of(2026, 6, 21),
                LocalDate.of(2026, 7, 20),
                1,
                100
        )).thenReturn(new TourApiPage<>(List.of(festival), 1, 100, 1));
        when(tourismContentService.findOptionalByExternalContentId("300001"))
                .thenReturn(Optional.of(tourismContent));
        when(tourismContent.getProviderModifiedAt())
                .thenReturn(LocalDateTime.of(2026, 7, 21, 9, 0));

        // When
        FestivalSyncFacadeService.FestivalSyncResult result = facadeService.synchronize(
                LocalDate.of(2026, 7, 21)
        );

        // Then
        verify(tourApiClient, never()).getCommonDetail("300001");
        verify(checkpointService).updateSucceededAt(
                TourismSyncType.FESTIVAL,
                LocalDateTime.of(2026, 7, 21, 9, 0)
        );
        assertThat(result).isEqualTo(new FestivalSyncFacadeService.FestivalSyncResult(0, 0, 1));
    }
}
