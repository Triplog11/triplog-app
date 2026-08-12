package triplog.backend.mission.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link DailyMissionPool}의 템플릿 구성 규칙을 검증하는 테스트입니다.
 */
class DailyMissionPoolTest {

    /**
     * 일일 미션 풀이 50개의 고유한 이름으로 구성되는지 검증합니다.
     */
    @Test
    @DisplayName("일일 미션 풀은 고유한 이름의 템플릿 50개를 제공한다")
    void templates_HasFiftyUniqueNames() {
        List<DailyMissionTemplate> templates = DailyMissionPool.templates();

        assertThat(templates).hasSize(50);
        assertThat(templates)
                .extracting(DailyMissionTemplate::name)
                .doesNotHaveDuplicates();
    }

    /**
     * 일일 미션 풀이 난이도별 목표 개수를 충족하는지 검증합니다.
     */
    @Test
    @DisplayName("일일 미션 풀은 EASY 20개 NORMAL 20개 HARD 10개로 구성된다")
    void templates_HasExpectedDifficultyDistribution() {
        List<DailyMissionTemplate> templates = DailyMissionPool.templates();

        assertThat(templates.stream().filter(template -> template.group() == 1)).hasSize(20);
        assertThat(templates.stream().filter(template -> template.group() == 2)).hasSize(20);
        assertThat(templates.stream().filter(template -> template.group() == 3)).hasSize(10);
    }
}
