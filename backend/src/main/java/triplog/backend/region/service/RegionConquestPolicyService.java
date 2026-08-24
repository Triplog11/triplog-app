package triplog.backend.region.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.region.entity.RegionConquestPolicy;
import triplog.backend.region.repository.RegionConquestPolicyRepository;

/**
 * 데이터베이스에 등록된 지역 정복 정책을 조회하고 충족 여부를 판정합니다.
 */
@Service
@RequiredArgsConstructor
public class RegionConquestPolicyService {

    private final RegionConquestPolicyRepository regionConquestPolicyRepository;

    /**
     * 사용자의 고유 랜드마크 방문 수가 지역 정복 기준을 충족하는지 확인합니다.
     *
     * @param totalLandmarkCount 지역의 전체 랜드마크 수
     * @param visitedLandmarkCount 사용자가 방문한 고유 랜드마크 수
     * @return 랜드마크가 존재하고 정책 기준을 충족하면 {@code true}
     */
    @Transactional(readOnly = true)
    public boolean isSatisfied(long totalLandmarkCount, long visitedLandmarkCount) {
        if (totalLandmarkCount <= 0) {
            return false;
        }
        RegionConquestPolicy policy = regionConquestPolicyRepository
                .findApplicablePolicies(totalLandmarkCount)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Region conquest policy not found for landmark count: "
                                + totalLandmarkCount
                ));
        return visitedLandmarkCount >= policy.calculateRequiredVisitCount(totalLandmarkCount);
    }
}
