package triplog.backend.batch.tourapi.seed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import triplog.backend.batch.tourapi.config.TourismSyncProperties;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LandmarkSeedReaderTest {

    @Test
    @DisplayName("classpath CSV에서 활성 랜드마크 시드를 읽는다")
    void classpath_CSV에서_활성_랜드마크_시드를_읽는다() {
        // Given
        TourismSyncProperties properties = new TourismSyncProperties(
                new TourismSyncProperties.Festival(30, 12),
                3,
                "classpath:seed/landmarks.csv",
                new TourismSyncProperties.Scheduling(false, "", "", "", "", "Asia/Seoul")
        );
        LandmarkSeedReader reader = new LandmarkSeedReader(
                new DefaultResourceLoader(),
                properties
        );

        // When
        List<LandmarkSeed> seeds = reader.readActiveSeeds();

        // Then
        assertThat(seeds).isNotEmpty();
        assertThat(seeds).allMatch(seed -> "12".equals(seed.expectedContentTypeId()));
        assertThat(seeds).extracting(LandmarkSeed::contentId).doesNotHaveDuplicates();
    }
}
