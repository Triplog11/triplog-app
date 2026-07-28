package triplog.backend.region.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.region.entity.UsersRegion;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 지역 방문 정보 영속성 처리를 담당하는 Repository입니다.
 */
public interface UsersRegionRepository extends JpaRepository<UsersRegion, Long> {

    /**
     * 특정 사용자의 모든 방문 지역 정보를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 사용자의 지역 방문 목록
     */
    List<UsersRegion> findByUsersId(String usersId);

    /**
     * 특정 사용자의 특정 지역 방문 정보를 조회합니다.
     *
     * @param usersId  사용자 식별자
     * @param regionId 지역 식별자
     * @return 사용자의 해당 지역 방문 정보
     */
    Optional<UsersRegion> findByUsersIdAndRegionRegionId(String usersId, Long regionId);
}
