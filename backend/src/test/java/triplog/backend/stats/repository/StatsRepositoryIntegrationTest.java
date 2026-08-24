package triplog.backend.stats.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.stats.entity.Stats;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 활동 동점 기준을 포함한 랭킹 native query의 실행 가능 여부를 검증합니다.
 */
@SpringBootTest(properties = {
        "spring.batch.job.enabled=false",
        "spring.flyway.enabled=true",
        "spring.flyway.validate-on-migrate=false",
        "spring.jpa.hibernate.ddl-auto=validate",
        "mission.scheduling.enabled=false"
})
@Transactional(readOnly = true)
class StatsRepositoryIntegrationTest {

    @Autowired
    private StatsRepository statsRepository;

    @Test
    @DisplayName("전체·월간 랭킹에 기간별 동점 정렬 쿼리를 실행한다")
    void findRankings() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<Stats> total = statsRepository.findRankings("TOTAL", null, pageable);
        Page<Stats> monthly = statsRepository.findRankings(
                "MONTHLY", LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay(), pageable
        );

        // Then
        assertThat(total).isNotNull();
        assertThat(monthly).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 랭킹 위치는 빈 값으로 조회한다")
    void findRankingPosition() {
        // Given
        String usersId = "00000000-0000-0000-0000-000000000000";

        // When & Then
        assertThat(statsRepository.findRankingPosition(usersId, "TOTAL", null))
                .isEmpty();
    }
}
