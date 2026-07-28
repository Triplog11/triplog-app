package triplog.backend.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.landmark.entity.Landmark;
import java.util.List;
import java.util.Optional;

/**
 * Landmark 영속성 처리를 담당하는 Repository입니다.
 */
public interface LandmarkRepository extends JpaRepository<Landmark, Long> {

    /**
     * TourismContent 내부 식별자로 Landmark를 조회합니다.
     *
     * @param tourismContentId TourismContent 내부 식별자
     * @return 연결된 Landmark
     */
    Optional<Landmark> findByTourismContentTourismContentId(Long tourismContentId);

    /**
     * 특정 지역에 속한 랜드마크 목록을 조회합니다.
     *
     * @param regionId 지역 식별자
     * @return 해당 지역의 랜드마크 목록
     */
    @Query("SELECT l FROM Landmark l JOIN FETCH l.tourismContent tc " +
            "WHERE tc.region.regionId = :regionId")
    List<Landmark> findByRegionId(@Param("regionId") Long regionId);

    /**
     * 랜드마크를 TourismContent 및 Region과 함께 조회합니다.
     *
     * @param landmarkId 랜드마크 식별자
     * @return 랜드마크 (TourismContent, Region FETCH JOIN)
     */
    @Query("SELECT l FROM Landmark l " +
            "JOIN FETCH l.tourismContent tc " +
            "JOIN FETCH tc.region " +
            "WHERE l.landmarkId = :landmarkId")
    Optional<Landmark> findByIdWithTourismContentAndRegion(@Param("landmarkId") Long landmarkId);

    /**
     * 지역별 전체 랜드마크 수를 조회합니다.
     *
     * @return 지역 ID와 랜드마크 수 목록
     */
    @Query("SELECT tc.region.regionId, COUNT(l) " +
            "FROM Landmark l JOIN l.tourismContent tc " +
            "GROUP BY tc.region.regionId")
    List<Object[]> countLandmarksByRegion();

    /**
     * 특정 사용자가 방문한 랜드마크 수를 지역별로 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 지역 ID와 방문 랜드마크 수 목록
     */
    @Query("SELECT tc.region.regionId, COUNT(DISTINCT ucl.landmark.landmarkId) " +
            "FROM UsersCardLandmark ucl " +
            "JOIN ucl.landmark l " +
            "JOIN l.tourismContent tc " +
            "WHERE ucl.usersId = :usersId " +
            "GROUP BY tc.region.regionId")
    List<Object[]> countVisitedLandmarksByRegionAndUser(@Param("usersId") String usersId);
}
