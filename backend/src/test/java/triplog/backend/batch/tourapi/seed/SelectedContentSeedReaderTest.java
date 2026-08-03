package triplog.backend.batch.tourapi.seed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.DefaultResourceLoader;
import triplog.backend.batch.tourapi.config.TourismSyncProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelectedContentSeedReaderTest {

    @TempDir
    Path tempDirectory;

    @Test
    @DisplayName("content_id 전용 CSV에서 랜드마크와 관광지 선정 목록을 읽는다")
    void content_id_전용_CSV에서_선정_목록을_읽는다() {
        // Given
        TourismSyncProperties properties = new TourismSyncProperties(
                new TourismSyncProperties.Festival(30, 12),
                3,
                "classpath:seed/landmarks.csv",
                "classpath:seed/attractions.csv",
                new TourismSyncProperties.Scheduling(false, "", "", "", "", "Asia/Seoul")
        );
        SelectedContentSeedReader reader = new SelectedContentSeedReader(
                new DefaultResourceLoader(),
                properties
        );

        // When
        SelectedContentSeeds seeds = reader.read();

        // Then
        assertThat(seeds.landmarkContentIds())
                .hasSize(102)
                .contains("127642", "126508", "129263");
        assertThat(seeds.attractionContentIds())
                .hasSize(208)
                .contains("130182", "126509", "322836");
        assertThat(seeds.allContentIds()).hasSize(310);
        assertThat(seeds.allContentIds()).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("같은 contentId가 랜드마크와 관광지 CSV에 동시에 있으면 실패한다")
    void 같은_contentId가_두_CSV에_있으면_실패한다() throws IOException {
        // Given
        Path landmarks = tempDirectory.resolve("landmarks.csv");
        Path attractions = tempDirectory.resolve("attractions.csv");
        Files.writeString(landmarks, "content_id\n126508\n");
        Files.writeString(attractions, "content_id\n126508\n");
        TourismSyncProperties properties = new TourismSyncProperties(
                new TourismSyncProperties.Festival(30, 12),
                3,
                landmarks.toUri().toString(),
                attractions.toUri().toString(),
                new TourismSyncProperties.Scheduling(false, "", "", "", "", "Asia/Seoul")
        );
        SelectedContentSeedReader reader = new SelectedContentSeedReader(
                new DefaultResourceLoader(),
                properties
        );

        // When
        // Then
        assertThatThrownBy(reader::read)
                .isInstanceOf(InvalidSelectedContentSeedException.class)
                .hasMessageContaining("중복 content_id")
                .hasMessageContaining("126508");
    }
}
