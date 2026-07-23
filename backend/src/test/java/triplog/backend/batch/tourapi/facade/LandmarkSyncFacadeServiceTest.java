package triplog.backend.batch.tourapi.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.batch.tourapi.client.TourApiClient;
import triplog.backend.batch.tourapi.config.TourismSyncProperties;
import triplog.backend.batch.tourapi.dto.TourApiChangedContentItem;
import triplog.backend.batch.tourapi.dto.TourApiCommonItem;
import triplog.backend.batch.tourapi.dto.TourApiPage;
import triplog.backend.batch.tourapi.entity.TourismSyncType;
import triplog.backend.batch.tourapi.seed.LandmarkSeed;
import triplog.backend.batch.tourapi.seed.LandmarkSeedReader;
import triplog.backend.batch.tourapi.service.TourismSyncCheckpointService;
import triplog.backend.batch.tourapi.service.TourismSyncFailureService;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.entity.Region;
import triplog.backend.region.service.RegionService;
import triplog.backend.tourismcontent.entity.TourismContent;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LandmarkSyncFacadeServiceTest {

    @Mock private LandmarkSeedReader seedReader;
    @Mock private TourApiClient tourApiClient;
    @Mock private RegionService regionService;
    @Mock private TourismContentService tourismContentService;
    @Mock private LandmarkService landmarkService;
    @Mock private TourismSyncFailureService failureService;
    @Mock private TourismSyncCheckpointService checkpointService;
    @Mock private Region region;
    @Mock private TourismContent tourismContent;

    private LandmarkSyncFacadeService facadeService;

    @BeforeEach
    void setUp() {
        TourismSyncProperties properties = new TourismSyncProperties(
                new TourismSyncProperties.Festival(30, 12),
                3,
                "classpath:seed/landmarks.csv",
                new TourismSyncProperties.Scheduling(false, "", "", "", "", "Asia/Seoul")
        );
        Clock clock = Clock.fixed(
                Instant.parse("2026-07-21T00:00:00Z"),
                ZoneId.of("Asia/Seoul")
        );
        facadeService = new LandmarkSyncFacadeService(
                seedReader,
                tourApiClient,
                regionService,
                tourismContentService,
                landmarkService,
                failureService,
                checkpointService,
                properties,
                clock
        );
    }

    @Test
    @DisplayName("활성 CSV와 겹치는 변경 랜드마크만 상세 동기화한다")
    void 활성_CSV와_겹치는_변경_랜드마크만_상세_동기화한다() {
        // Given
        LandmarkSeed seed = new LandmarkSeed("126508", "경복궁", "12", "11", "110", true);
        TourApiChangedContentItem changedItem = new TourApiChangedContentItem(
                "126508", "12", "20260721090000", "1"
        );
        TourApiCommonItem commonItem = commonItem("126508");
        when(checkpointService.findLastSucceededAt(TourismSyncType.LANDMARK))
                .thenReturn(Optional.of(LocalDateTime.of(2026, 7, 20, 3, 0)));
        when(seedReader.readActiveSeeds()).thenReturn(List.of(seed));
        when(tourApiClient.getChangedContents(LocalDate.of(2026, 7, 19), 1, 100))
                .thenReturn(new TourApiPage<>(List.of(changedItem), 1, 100, 1));
        when(tourApiClient.getCommonDetail("126508")).thenReturn(commonItem);
        when(regionService.findByLegalCode("11", "110")).thenReturn(region);
        when(tourismContentService.upsert(any(), any(), any())).thenReturn(tourismContent);

        // When
        LandmarkSyncFacadeService.LandmarkSyncResult result = facadeService.synchronizeIncremental();

        // Then
        verify(landmarkService).upsert(tourismContent, "경복궁");
        verify(checkpointService).updateSucceededAt(
                TourismSyncType.LANDMARK,
                LocalDateTime.of(2026, 7, 21, 9, 0)
        );
        assertThat(result).isEqualTo(new LandmarkSyncFacadeService.LandmarkSyncResult(1, 0, 0));
    }

    @Test
    @DisplayName("비표출 변경 랜드마크는 상세 조회하지 않고 누락 횟수를 반영한다")
    void 비표출_변경_랜드마크는_상세_조회하지_않고_누락_횟수를_반영한다() {
        // Given
        LandmarkSeed seed = new LandmarkSeed("126508", "경복궁", "12", "11", "110", true);
        TourApiChangedContentItem hiddenItem = new TourApiChangedContentItem(
                "126508", "12", "20260721090000", "0"
        );
        when(checkpointService.findLastSucceededAt(TourismSyncType.LANDMARK))
                .thenReturn(Optional.of(LocalDateTime.of(2026, 7, 20, 3, 0)));
        when(seedReader.readActiveSeeds()).thenReturn(List.of(seed));
        when(tourApiClient.getChangedContents(LocalDate.of(2026, 7, 19), 1, 100))
                .thenReturn(new TourApiPage<>(List.of(hiddenItem), 1, 100, 1));

        // When
        LandmarkSyncFacadeService.LandmarkSyncResult result = facadeService.synchronizeIncremental();

        // Then
        verify(tourismContentService).markMissing("126508", 3);
        verify(tourApiClient, never()).getCommonDetail(any());
        assertThat(result).isEqualTo(new LandmarkSyncFacadeService.LandmarkSyncResult(0, 0, 1));
    }

    private TourApiCommonItem commonItem(String contentId) {
        return new TourApiCommonItem(
                contentId, "12", "경복궁", "20260101000000", "20260721090000",
                null, null, null, null, null, null, "서울특별시 종로구", null,
                null, "126.0", "37.0", null, null, "11", "110", null, null, null
        );
    }
}
