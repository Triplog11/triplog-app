package triplog.backend.levelpolicy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.levelpolicy.entity.LevelPolicy;
import triplog.backend.levelpolicy.exception.LevelPolicyException;
import triplog.backend.levelpolicy.repository.LevelPolicyRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LevelPolicyServiceImplTest {

    @Mock
    private LevelPolicyRepository levelPolicyRepository;

    private LevelPolicyServiceImpl levelPolicyService;

    @BeforeEach
    void setUp() {
        levelPolicyService = new LevelPolicyServiceImpl(levelPolicyRepository);
    }

    @Test
    @DisplayName("다음 레벨에 필요한 누적 XP와 현재 XP 구간의 레벨을 계산한다")
    void calculateLevelAndNextRequirement() {
        // Given
        when(levelPolicyRepository.findAllByOrderByLevelPolicyNumberAsc())
                .thenReturn(List.of(
                        new LevelPolicy(1, 100),
                        new LevelPolicy(2, 100),
                        new LevelPolicy(3, 100)
                ));

        // When
        LevelPolicyInfo nextPolicy = levelPolicyService
                .findNextLevelPolicy(3)
                .orElseThrow();

        // Then
        assertThat(nextPolicy.nextLevel()).isEqualTo(4);
        assertThat(nextPolicy.requiredXp()).isEqualTo(300);
        assertThat(levelPolicyService.calculateLevel(0)).isEqualTo(1);
        assertThat(levelPolicyService.calculateLevel(99)).isEqualTo(1);
        assertThat(levelPolicyService.calculateLevel(100)).isEqualTo(2);
        assertThat(levelPolicyService.calculateLevel(299)).isEqualTo(3);
        assertThat(levelPolicyService.calculateLevel(300)).isEqualTo(4);
    }

    @Test
    @DisplayName("마지막 정책 이후에는 마지막 구간 XP를 계속 적용한다")
    void extendLastPolicy() {
        // Given
        when(levelPolicyRepository.findAllByOrderByLevelPolicyNumberAsc())
                .thenReturn(List.of(
                        new LevelPolicy(1, 100),
                        new LevelPolicy(2, 150)
                ));

        // When
        LevelPolicyInfo nextPolicy = levelPolicyService
                .findNextLevelPolicy(3)
                .orElseThrow();

        // Then
        assertThat(nextPolicy.nextLevel()).isEqualTo(4);
        assertThat(nextPolicy.requiredXp()).isEqualTo(400);
        assertThat(levelPolicyService.calculateLevel(399)).isEqualTo(3);
        assertThat(levelPolicyService.calculateLevel(400)).isEqualTo(4);
    }

    @Test
    @DisplayName("레벨 정책 번호가 연속적이지 않으면 설정 예외가 발생한다")
    void rejectInvalidPolicyTable() {
        // Given
        when(levelPolicyRepository.findAllByOrderByLevelPolicyNumberAsc())
                .thenReturn(List.of(
                        new LevelPolicy(1, 100),
                        new LevelPolicy(3, 100)
                ));

        // When & Then
        assertThatThrownBy(() -> levelPolicyService.calculateLevel(100))
                .isInstanceOf(LevelPolicyException.class)
                .hasMessage("레벨 정책 설정이 올바르지 않습니다.");
    }
}
