package triplog.backend.tourismcontent.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import triplog.backend.region.entity.Region;
import triplog.backend.tourismcontent.service.TourismContentSyncData;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TourismContentTest {

    @Test
    @DisplayName("세 번 연속 누락되면 논리적으로 비활성화하고 정상 확인 시 복구한다")
    void 세_번_연속_누락되면_논리적으로_비활성화하고_정상_확인_시_복구한다() {
        // Given
        TourismContent content = new TourismContent(
                new Region("서울특별시 종로구", "11", "110"),
                syncData(),
                LocalDateTime.of(2026, 7, 21, 9, 0)
        );

        // When
        content.markMissing(3);
        content.markMissing(3);
        content.markMissing(3);

        // Then
        assertThat(content.isActive()).isFalse();
        assertThat(content.getSyncStatus()).isEqualTo(TourismSyncStatus.INACTIVE_CANDIDATE);
        assertThat(content.getConsecutiveMissingCount()).isEqualTo(3);

        // When
        content.clearMissing();

        // Then
        assertThat(content.isActive()).isTrue();
        assertThat(content.getSyncStatus()).isEqualTo(TourismSyncStatus.COMPLETED);
        assertThat(content.getConsecutiveMissingCount()).isZero();
    }

    private TourismContentSyncData syncData() {
        return new TourismContentSyncData(
                "126508", "12", "경복궁", null, null, null, null, null, null,
                null, null, null, "11", "110", null, null, null, null, null, null,
                null, null
        );
    }
}
