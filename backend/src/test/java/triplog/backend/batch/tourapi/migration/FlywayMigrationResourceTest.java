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
                .contains("CREATE TABLE attraction_visit_log")
                .contains("UNIQUE KEY uk_mission_week_name (mission_week_start, mission_name)")
                .contains("UNIQUE KEY uk_users_mission_users_mission (users_id, mission_id)")
                .contains("CREATE TABLE event")
                .contains("CREATE TABLE tourism_content_image")
                .contains("CREATE TABLE tourism_sync_failure")
                .contains("CREATE TABLE tourism_sync_checkpoint")
                .contains("CREATE TABLE BATCH_JOB_INSTANCE");
    }

    /** 마이그레이션과 선정 CSV가 UTF-8 BOM 없이 패키징되는지 검증합니다. */
    @Test
    @DisplayName("선정 CSV와 단일 Flyway V1은 UTF-8 BOM 없이 패키징된다")
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

    @Test
    @DisplayName("V1에 V2부터 V4까지의 스키마 변경이 포함된다")
    void V1에_V2부터_V4까지의_스키마_변경이_포함된다() throws IOException {
        // Given
        String migrationPath = "db/migration/V1__create_initial_schema.sql";

        // When
        String migration = readClasspathResource(migrationPath);

        // Then
        assertThat(migration)
                .contains("CREATE TABLE users_activity_log")
                .contains("CREATE TABLE region_conquest_policy")
                .contains("users_region_conquered BOOLEAN NOT NULL DEFAULT FALSE")
                .contains("uk_users_region_users_region (users_id, region_id)")
                .contains("uk_users_badge_users_badge (users_id, badge_id)")
                .contains("uk_users_appellation_users_appellation (users_id, appellation_id)")
                .contains("is_representative BOOLEAN NOT NULL DEFAULT FALSE COMMENT '대표 칭호 여부'")
                .contains("uk_users_activity_log_event")
                .contains("landmark_id BIGINT NULL COMMENT '카드가 속한 랜드마크 식별자'")
                .contains("UNIQUE KEY uk_card_landmark (landmark_id)")
                .contains("UNIQUE KEY uk_ucl_users_landmark (users_id, landmark_id)")
                .contains("CREATE TABLE users_reward_log")
                .doesNotContain("CREATE TABLE users_level_log")
                .doesNotContain("CREATE TABLE users_badge_log")
                .doesNotContain("CREATE TABLE users_region_log")
                .doesNotContain("CREATE TABLE users_card_landmark_log");
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
