package triplog.backend.batch.tourapi.facade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.batch.tourapi.client.TourApiClient;
import triplog.backend.batch.tourapi.dto.TourApiLegalDistrictItem;
import triplog.backend.batch.tourapi.dto.TourApiPage;
import triplog.backend.region.service.RegionService;
import triplog.backend.region.service.RegionSyncData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionSyncFacadeServiceTest {

    @Mock
    private TourApiClient tourApiClient;

    @Mock
    private RegionService regionService;

    @Test
    @DisplayName("시도와 시군구 명칭 및 코드를 조합하여 Region을 동기화한다")
    void 시도와_시군구_명칭_및_코드를_조합하여_Region을_동기화한다() {
        // Given
        TourApiLegalDistrictItem region = new TourApiLegalDistrictItem("11", "서울특별시");
        TourApiLegalDistrictItem district = new TourApiLegalDistrictItem("110", "종로구");
        when(tourApiClient.getLegalRegions(1, 100))
                .thenReturn(new TourApiPage<>(List.of(region), 1, 100, 1));
        when(tourApiClient.getLegalDistricts("11", 1, 100))
                .thenReturn(new TourApiPage<>(List.of(district), 1, 100, 1));
        RegionSyncFacadeService facadeService = new RegionSyncFacadeService(
                tourApiClient,
                regionService
        );

        // When
        int savedCount = facadeService.synchronizeAll();

        // Then
        ArgumentCaptor<RegionSyncData> captor = ArgumentCaptor.forClass(RegionSyncData.class);
        verify(regionService).upsert(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new RegionSyncData(
                "서울특별시 종로구",
                "11",
                "110"
        ));
        assertThat(savedCount).isEqualTo(1);
    }
}
