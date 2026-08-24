package triplog.backend.region.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.region.entity.RegionConquestPolicy;

import java.util.List;

/**
 * 지역 정복 기준 정책의 영속성 처리를 담당합니다.
 */
public interface RegionConquestPolicyRepository
        extends JpaRepository<RegionConquestPolicy, String> {

    /**
     * 전체 랜드마크 수에 적용할 정복 정책을 조회합니다.
     *
     * @param totalLandmarkCount 지역의 전체 랜드마크 수
     * @return 적용 범위의 최솟값이 큰 순서로 정렬한 정책 목록
     */
    @Query("SELECT p FROM RegionConquestPolicy p "
            + "WHERE p.minimumLandmarkCount <= :totalLandmarkCount "
            + "AND (p.maximumLandmarkCount IS NULL "
            + "OR p.maximumLandmarkCount >= :totalLandmarkCount) "
            + "ORDER BY p.minimumLandmarkCount DESC")
    List<RegionConquestPolicy> findApplicablePolicies(
            @Param("totalLandmarkCount") long totalLandmarkCount
    );
}
