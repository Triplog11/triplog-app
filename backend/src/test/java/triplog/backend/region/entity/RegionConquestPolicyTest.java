package triplog.backend.region.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link RegionConquestPolicy}의 고정 방문 수와 비율 올림 계산을 검증합니다.
 */
class RegionConquestPolicyTest {

    @Test
    @DisplayName("랜드마크가 1개부터 10개인 지역은 seed의 고정 정복 기준을 적용한다")
    void calculateRequiredVisitCount_FixedCount() {
        // Given
        int[] requiredVisitCounts = {1, 2, 2, 3, 4, 4, 5, 6, 7, 7};

        // When & Then
        for (int totalLandmarkCount = 1;
             totalLandmarkCount <= requiredVisitCounts.length;
             totalLandmarkCount++) {
            RegionConquestPolicy policy = new RegionConquestPolicy(
                    "LANDMARK_COUNT_" + totalLandmarkCount,
                    totalLandmarkCount,
                    totalLandmarkCount,
                    requiredVisitCounts[totalLandmarkCount - 1],
                    null
            );

            assertThat(policy.calculateRequiredVisitCount(totalLandmarkCount))
                    .isEqualTo(requiredVisitCounts[totalLandmarkCount - 1]);
        }
    }

    @Test
    @DisplayName("랜드마크가 11개 이상인 지역은 전체의 70퍼센트를 올림한다")
    void calculateRequiredVisitCount_RateWithCeiling() {
        // Given
        RegionConquestPolicy policy = new RegionConquestPolicy(
                "LANDMARK_COUNT_11_OR_MORE",
                11,
                null,
                null,
                new BigDecimal("0.7000")
        );

        // When
        long requiredForEleven = policy.calculateRequiredVisitCount(11);
        long requiredForTwelve = policy.calculateRequiredVisitCount(12);
        long requiredForTwenty = policy.calculateRequiredVisitCount(20);

        // Then
        assertThat(requiredForEleven).isEqualTo(8);
        assertThat(requiredForTwelve).isEqualTo(9);
        assertThat(requiredForTwenty).isEqualTo(14);
    }
}
