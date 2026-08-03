package triplog.backend.batch.tourapi.migration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Flyway 스키마와 선정 CSV가 실행 classpath에 올바르게 포함되는지 검증합니다.
 */
class FlywayMigrationResourceTest {

    /** V1 스키마에 TourAPI 동기화 핵심 테이블이 포함되는지 검증합니다. */
    @Test
    @DisplayName("실행 JAR classpath에 V1 스키마와 핵심 동기화 테이블이 포함된다")
    void 실행_JAR_classpath에_V1_스키마와_핵심_동기화_테이블이_포함된다() throws IOException {
        // Given
        String migrationPath = "db/migration/V1__create_initial_schema.sql";

        // When
        String migration = readClasspathResource(migrationPath);

        // Then
        assertThat(migration)
                .contains("CREATE TABLE region")
                .contains("CREATE TABLE tourism_content")
                .contains("CREATE TABLE landmark")
                .contains("CREATE TABLE attraction")
                .contains("CREATE TABLE event")
                .contains("CREATE TABLE tourism_content_image")
                .contains("CREATE TABLE tourism_sync_failure")
                .contains("CREATE TABLE tourism_sync_checkpoint")
                .contains("CREATE TABLE BATCH_JOB_INSTANCE");
    }

    /** 마이그레이션과 선정 CSV가 UTF-8 BOM 없이 패키징되는지 검증합니다. */
    @Test
    @DisplayName("선정 CSV와 Flyway V1은 UTF-8 BOM 없이 패키징된다")
    void 선정_CSV와_Flyway_V1은_UTF8_BOM_없이_패키징된다() throws IOException {
        // Given
        String migrationPath = "db/migration/V1__create_initial_schema.sql";
        String landmarkPath = "seed/landmarks.csv";
        String attractionPath = "seed/attractions.csv";

        // When
        byte[] migrationBytes = readClasspathResourceBytes(migrationPath);
        byte[] landmarkBytes = readClasspathResourceBytes(landmarkPath);
        byte[] attractionBytes = readClasspathResourceBytes(attractionPath);

        // Then
        assertThat(hasUtf8Bom(migrationBytes)).isFalse();
        assertThat(hasUtf8Bom(landmarkBytes)).isFalse();
        assertThat(hasUtf8Bom(attractionBytes)).isFalse();
    }

    private String readClasspathResource(String path) throws IOException {
        return new String(readClasspathResourceBytes(path), StandardCharsets.UTF_8);
    }

    private byte[] readClasspathResourceBytes(String path) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            assertThat(inputStream).as("classpath resource: " + path).isNotNull();
            return inputStream.readAllBytes();
        }
    }

    private boolean hasUtf8Bom(byte[] bytes) {
        return bytes.length >= 3
                && bytes[0] == (byte) 0xEF
                && bytes[1] == (byte) 0xBB
                && bytes[2] == (byte) 0xBF;
    }
}
