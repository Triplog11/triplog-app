package triplog.backend.region.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.region.entity.UsersRegion;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 지역 방문 정보 영속성 처리를 담당하는 Repository입니다.
 */
public interface UsersRegionRepository extends JpaRepository<UsersRegion, Long> {

    /**
     * 사용자의 최근 방문 지역을 방문 일시 역순으로 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param pageable 조회 범위
     * @return 최근 방문 지역 목록
     */
    @EntityGraph(attributePaths = "region")
    List<UsersRegion> findByUsersIdOrderByUsersRegionVisitedAtDescUsersRegionIdDesc(
            String usersId,
            Pageable pageable
    );

    /**
     * 특정 사용자의 모든 방문 지역 정보를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 사용자의 지역 방문 목록
     */
    List<UsersRegion> findByUsersId(String usersId);

    /**
     * 사용자가 방문한 서로 다른 지역 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 방문 지역 수
     */
    long countByUsersId(String usersId);

    /**
     * 특정 사용자의 특정 지역 방문 정보를 조회합니다.
     *
     * @param usersId  사용자 식별자
     * @param regionId 지역 식별자
     * @return 사용자의 해당 지역 방문 정보
     */
    Optional<UsersRegion> findByUsersIdAndRegionRegionId(String usersId, Long regionId);

    /**
     * 사용자의 지역 방문 횟수를 1 증가시킵니다.
     *
     * @param usersRegionId 사용자 지역 식별자
     */
    @Modifying
    @Query("UPDATE UsersRegion ur SET ur.usersRegionVisitedCount = ur.usersRegionVisitedCount + 1 WHERE ur.usersRegionId = :usersRegionId")
    void incrementVisitCount(@Param("usersRegionId") Long usersRegionId);
}
