package triplog.backend.region.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.region.entity.RegionConquestPolicy;
import triplog.backend.region.repository.RegionConquestPolicyRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * {@link RegionConquestPolicyService}의 정책 조회와 정복 판정을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class RegionConquestPolicyServiceTest {

    @Mock
    private RegionConquestPolicyRepository regionConquestPolicyRepository;

    private RegionConquestPolicyService regionConquestPolicyService;

    @BeforeEach
    void setUp() {
        regionConquestPolicyService = new RegionConquestPolicyService(
                regionConquestPolicyRepository
        );
    }

    @Test
    @DisplayName("방문한 랜드마크 수가 seed 정책 기준에 도달하면 정복 조건을 충족한다")
    void isSatisfied() {
        // Given
        RegionConquestPolicy policy = new RegionConquestPolicy(
                "LANDMARK_COUNT_4", 4, 4, 3, null
        );
        given(regionConquestPolicyRepository.findApplicablePolicies(4))
                .willReturn(List.of(policy));

        // When
        boolean result = regionConquestPolicyService.isSatisfied(4, 3);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("랜드마크가 없는 지역은 정복하지 않고 정책을 조회하지 않는다")
    void isSatisfied_NoLandmark() {
        // Given
        long totalLandmarkCount = 0;

        // When
        boolean result = regionConquestPolicyService.isSatisfied(totalLandmarkCount, 0);

        // Then
        assertThat(result).isFalse();
        verifyNoInteractions(regionConquestPolicyRepository);
    }

    @Test
    @DisplayName("적용 가능한 정복 정책 seed가 없으면 설정 오류를 발생시킨다")
    void isSatisfied_PolicyNotFound() {
        // Given
        given(regionConquestPolicyRepository.findApplicablePolicies(4))
                .willReturn(List.of());

        // When & Then
        assertThatThrownBy(() -> regionConquestPolicyService.isSatisfied(4, 3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("landmark count: 4");
    }
}
