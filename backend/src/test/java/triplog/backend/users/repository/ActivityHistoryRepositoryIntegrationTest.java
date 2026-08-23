package triplog.backend.users.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.users.service.ActivityHistoryRecord;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 통합 활동 히스토리 native query의 실행 가능 여부를 검증합니다.
 */
@SpringBootTest(properties = {
        "spring.batch.job.enabled=false",
        "spring.flyway.enabled=true",
        "spring.flyway.validate-on-migrate=false",
        "spring.jpa.hibernate.ddl-auto=validate",
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
        String usersId = "00000000-0000-0000-0000-000000000001";
        jdbcTemplate.update(
                """
                        INSERT INTO users (
                            users_id, login_type, nickname, profile_url, email, password
                        ) VALUES (?, 'LOCAL', ?, ?, ?, ?)
                        """,
                usersId,
                "활동로그테스트",
                "https://example.com/profile.png",
                "activity-history@example.com",
                "test-password"
        );
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

}
