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

/**
 * {@link SelectedContentSeedReader}의 CSV 읽기와 중복 검증을 확인합니다.
 */
class SelectedContentSeedReaderTest {

    @TempDir
    Path tempDirectory;

    /** 랜드마크와 일반 관광지 contentId 목록을 각각 읽는지 검증합니다. */
    @Test
    @DisplayName("카드 정보가 포함된 랜드마크 CSV와 관광지 선정 목록을 읽는다")
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
        assertThat(seeds.getLandmarkSeed("127642").cardTier())
                .isEqualTo(triplog.backend.landmark.entity.CardTier.LEGENDARY);
        assertThat(seeds.attractionContentIds())
                .hasSize(208)
                .contains("130182", "126509", "322836");
        assertThat(seeds.allContentIds()).hasSize(310);
        assertThat(seeds.allContentIds()).doesNotHaveDuplicates();
    }

    /** 두 CSV 사이에 같은 contentId가 있으면 실패하는지 검증합니다. */
    @Test
    @DisplayName("같은 contentId가 랜드마크와 관광지 CSV에 동시에 있으면 실패한다")
    void 같은_contentId가_두_CSV에_있으면_실패한다() throws IOException {
        // Given
        Path landmarks = tempDirectory.resolve("landmarks.csv");
        Path attractions = tempDirectory.resolve("attractions.csv");
        Files.writeString(landmarks, "content_id,rarity,card_url\n126508,RARE,\n");
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
