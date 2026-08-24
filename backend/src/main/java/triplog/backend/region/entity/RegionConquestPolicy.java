package triplog.backend.region.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.RoundingMode;

/**
 * 지역의 전체 랜드마크 수에 적용할 정복 기준 정책입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "region_conquest_policy")
public class RegionConquestPolicy {

    @Id
    @Column(name = "region_conquest_policy_id", nullable = false, length = 50)
    private String regionConquestPolicyId;

    @Column(name = "minimum_landmark_count", nullable = false)
    private Integer minimumLandmarkCount;

    @Column(name = "maximum_landmark_count")
    private Integer maximumLandmarkCount;

    @Column(name = "required_visit_count")
    private Integer requiredVisitCount;

    @Column(name = "required_visit_rate", precision = 5, scale = 4)
    private java.math.BigDecimal requiredVisitRate;

    /**
     * 지역 정복 정책을 생성합니다.
     *
     * @param regionConquestPolicyId 정책 식별자
     * @param minimumLandmarkCount 적용 최소 랜드마크 수
     * @param maximumLandmarkCount 적용 최대 랜드마크 수. 상한이 없으면 null
     * @param requiredVisitCount 고정 필요 방문 수
     * @param requiredVisitRate 필요 방문 비율
     */
    public RegionConquestPolicy(
            String regionConquestPolicyId,
            Integer minimumLandmarkCount,
            Integer maximumLandmarkCount,
            Integer requiredVisitCount,
            java.math.BigDecimal requiredVisitRate
    ) {
        this.regionConquestPolicyId = regionConquestPolicyId;
        this.minimumLandmarkCount = minimumLandmarkCount;
        this.maximumLandmarkCount = maximumLandmarkCount;
        this.requiredVisitCount = requiredVisitCount;
        this.requiredVisitRate = requiredVisitRate;
    }

    /**
     * 현재 정책에 따른 정복 필요 방문 수를 계산합니다.
     * 고정 방문 수가 없으면 전체 랜드마크 수와 비율을 곱한 값을 올림합니다.
     *
     * @param totalLandmarkCount 지역의 전체 랜드마크 수
     * @return 정복에 필요한 고유 랜드마크 방문 수
     */
    public long calculateRequiredVisitCount(long totalLandmarkCount) {
        if (requiredVisitCount != null) {
            return requiredVisitCount;
        }
        if (requiredVisitRate == null) {
            throw new IllegalStateException(
                    "Region conquest policy must define visit count or rate: "
                            + regionConquestPolicyId
            );
        }
        return requiredVisitRate
                .multiply(java.math.BigDecimal.valueOf(totalLandmarkCount))
                .setScale(0, RoundingMode.CEILING)
                .longValueExact();
    }
}
