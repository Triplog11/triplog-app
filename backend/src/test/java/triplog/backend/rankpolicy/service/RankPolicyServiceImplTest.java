package triplog.backend.rankpolicy.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.rankpolicy.entity.RankPolicy;
import triplog.backend.rankpolicy.repository.RankPolicyRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankPolicyServiceImplTest {

    @Mock
    private RankPolicyRepository rankPolicyRepository;

    @InjectMocks
    private RankPolicyServiceImpl rankPolicyService;

    @Test
    @DisplayName("누적 Score 이하의 최고 티어와 다음 티어를 조회한다")
    void findCurrentAndNextRankPolicy() {
        // Given
        when(rankPolicyRepository
                .findFirstByRankPolicyConditionLessThanEqualOrderByRankPolicyConditionDesc(1250))
                .thenReturn(Optional.of(new RankPolicy("SILVER", 500)));
        when(rankPolicyRepository
                .findFirstByRankPolicyConditionGreaterThanOrderByRankPolicyConditionAsc(1250))
                .thenReturn(Optional.of(new RankPolicy("GOLD", 1500)));

        // When
        RankPolicyInfo current = rankPolicyService.findCurrentRankPolicy(1250);
        RankPolicyInfo next = rankPolicyService.findNextRankPolicy(1250).orElseThrow();

        // Then
        assertThat(current.tier()).isEqualTo("SILVER");
        assertThat(current.requiredScore()).isEqualTo(500);
        assertThat(next.tier()).isEqualTo("GOLD");
        assertThat(next.requiredScore()).isEqualTo(1500);
    }
}
