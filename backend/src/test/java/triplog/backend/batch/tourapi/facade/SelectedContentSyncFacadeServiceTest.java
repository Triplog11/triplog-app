package triplog.backend.batch.tourapi.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.attraction.service.AttractionService;
import triplog.backend.batch.tourapi.client.TourApiClient;
import triplog.backend.batch.tourapi.config.TourismSyncProperties;
import triplog.backend.batch.tourapi.dto.TourApiCommonItem;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectedContentSyncFacadeServiceTest {

    @Mock private SelectedContentSeedReader seedReader;
    @Mock private TourApiClient tourApiClient;
    @Mock private RegionService regionService;
    @Mock private TourismContentService tourismContentService;
    @Mock private LandmarkService landmarkService;
    @Mock private AttractionService attractionService;
    @Mock private TourismSyncFailureService failureService;
    @Mock private TourismSyncCheckpointService checkpointService;
    @Mock private Region region;
    @Mock private TourismContent landmarkContent;
    @Mock private TourismContent attractionContent;

    private SelectedContentSyncFacadeService facadeService;

    @BeforeEach
    void setUp() {
        TourismSyncProperties properties = new TourismSyncProperties(
                new TourismSyncProperties.Festival(30, 12),
                3,
                "classpath:seed/landmarks.csv",
                "classpath:seed/attractions.csv",
                new TourismSyncProperties.Scheduling(false, "", "", "", "", "Asia/Seoul")
        );
        facadeService = new SelectedContentSyncFacadeService(
                seedReader,
                tourApiClient,
                regionService,
                tourismContentService,
                landmarkService,
                attractionService,
                failureService,
                checkpointService,
                properties,
                Clock.fixed(Instant.parse("2026-07-21T00:00:00Z"), ZoneId.of("Asia/Seoul"))
        );
    }

    @Test
    @DisplayName("두 CSV의 contentId를 각각 랜드마크와 관광지로 저장한다")
    void 두_CSV의_contentId를_각_유형으로_저장한다() {
        // Given
        when(seedReader.read()).thenReturn(new SelectedContentSeeds(
                Set.of("126508"),
                Set.of("126081")
        ));
        TourApiCommonItem landmarkItem = commonItem("126508", "14", "경복궁");
        TourApiCommonItem attractionItem = commonItem("126081", "28", "해운대해수욕장");
        when(tourApiClient.getCommonDetail("126508")).thenReturn(landmarkItem);
        when(tourApiClient.getCommonDetail("126081")).thenReturn(attractionItem);
        when(regionService.findByLegalCode("11", "110")).thenReturn(region);
        when(tourismContentService.upsert(any(), any(), any()))
                .thenAnswer(invocation -> {
                    triplog.backend.tourismcontent.service.TourismContentSyncData syncData =
                            invocation.getArgument(1);
                    return "126508".equals(syncData.externalContentId())
                            ? landmarkContent
                            : attractionContent;
                });

        // When
        SelectedContentSyncFacadeService.SelectedContentSyncResult result =
                facadeService.synchronizeInitial();

        // Then
        verify(landmarkService).upsert(landmarkContent, "경복궁");
        verify(attractionService).upsert(attractionContent);
        verify(checkpointService).updateSucceededAt(
                TourismSyncType.LANDMARK,
                LocalDateTime.of(2026, 7, 21, 9, 0)
        );
        verify(checkpointService).updateSucceededAt(
                TourismSyncType.ATTRACTION,
                LocalDateTime.of(2026, 7, 21, 9, 0)
        );
        assertThat(result).isEqualTo(
                new SelectedContentSyncFacadeService.SelectedContentSyncResult(2, 0, 0)
        );
    }

    private TourApiCommonItem commonItem(String contentId, String contentTypeId, String title) {
        return new TourApiCommonItem(
                contentId, contentTypeId, title, "20260101000000", "20260721090000",
                null, null, null, null, null, null, "서울특별시 종로구", null,
                null, "126.0", "37.0", null, null, "11", "110", null, null, null
        );
    }
}
