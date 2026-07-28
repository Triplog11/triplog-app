package triplog.backend.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.landmark.entity.UsersCardLandmark;
import java.util.Optional;
import java.util.Set;

/**
 * 사용자 랜드마크 카드 획득 정보 영속성 처리를 담당하는 Repository입니다.
 */
public interface UsersCardLandmarkRepository extends JpaRepository<UsersCardLandmark, Long> {

    /**
     * 특정 사용자가 획득한 랜드마크 ID 집합을 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 획득한 랜드마크 ID 집합
     */
    @Query("SELECT ucl.landmark.landmarkId FROM UsersCardLandmark ucl WHERE ucl.usersId = :usersId")
    Set<Long> findAcquiredLandmarkIdsByUsersId(@Param("usersId") String usersId);

    /**
     * 특정 사용자의 특정 랜드마크 카드 획득 정보를 조회합니다.
     *
     * @param usersId    사용자 식별자
     * @param landmarkId 랜드마크 식별자
     * @return 사용자의 해당 랜드마크 카드 획득 정보
     */
    Optional<UsersCardLandmark> findByUsersIdAndLandmarkLandmarkId(String usersId, Long landmarkId);
}
