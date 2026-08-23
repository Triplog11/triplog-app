package triplog.backend.users.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.users.service.ActivityHistoryRecord;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 통합 활동 히스토리 native query의 실행 가능 여부를 검증합니다.
 */
@SpringBootTest(properties = {
        "spring.batch.job.enabled=false",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none",
        "mission.scheduling.enabled=false"
})
@Transactional
class ActivityHistoryRepositoryIntegrationTest {

    @Autowired
    private ActivityHistoryRepository activityHistoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("통합 활동 로그 쿼리를 페이지 단위로 실행한다")
    void findByUsersId() {
        // Given
        assumeActivityLogTableExists();
        String usersId = "00000000-0000-0000-0000-000000000000";
        PageRequest pageable = PageRequest.of(0, 10);

        // When
        var result = activityHistoryRepository.findByUsersId(usersId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("동일 이벤트 키의 활동 로그를 한 번만 저장한다")
    void insertIfAbsent() {
        // Given
        assumeActivityLogTableExists();
        List<String> usersIds = jdbcTemplate.query(
                "SELECT users_id FROM users ORDER BY users_id LIMIT 1",
                (resultSet, rowNumber) -> resultSet.getString("users_id")
        );
        Assumptions.assumeFalse(usersIds.isEmpty(), "활동 로그 FK 검증용 사용자가 필요합니다.");
        String usersId = usersIds.getFirst();
        String eventKey = "TEST:ACTIVITY_HISTORY:1";
        ActivityHistoryRecord record = new ActivityHistoryRecord(
                usersId,
                "LANDMARK",
                "TEST",
                "1",
                eventKey,
                "통합 활동 로그 테스트",
                "중복 이벤트 키 검증",
                50,
                30,
                10,
                LocalDateTime.of(2099, 1, 1, 0, 0)
        );

        // When
        activityHistoryRepository.insertIfAbsent(record);
        activityHistoryRepository.insertIfAbsent(record);

        // Then
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users_activity_log WHERE users_id = ? AND event_key = ?",
                Integer.class,
                usersId,
                eventKey
        );
        assertThat(count).isEqualTo(1);
        var result = activityHistoryRepository.findByUsersId(usersId, PageRequest.of(0, 10));
        assertThat(result.getContent().getFirst().title()).isEqualTo("통합 활동 로그 테스트");
        assertThat(result.getContent().getFirst().score()).isEqualTo(30);
    }

    private void assumeActivityLogTableExists() {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM information_schema.tables
                        WHERE table_schema = DATABASE()
                          AND table_name = 'users_activity_log'
                        """,
                Integer.class
        );
        Assumptions.assumeTrue(count != null && count == 1, "통합 Flyway V1 스키마 적용이 필요합니다.");
    }
}
