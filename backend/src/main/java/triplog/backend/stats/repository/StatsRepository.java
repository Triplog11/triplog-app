package triplog.backend.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import triplog.backend.stats.entity.Stats;

import java.util.Optional;

/**
 * 사용자 통계(Stats) 엔티티의 데이터 접근을 담당하는 JPA Repository입니다.
 * <p>
 * Spring Data JPA를 기반으로 CRUD 기능을 제공하며,
 * 메서드 이름 기반 쿼리, JPQL, Query Method 등을 통해 사용자 통계 데이터를 조회하고 관리합니다.
 */
@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    /**
     * 사용자 ID로 통계 정보를 조회합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 사용자 통계 정보, 존재하지 않으면 빈 값
     */
    Optional<Stats> findByUsersUsersId(String usersId);
}
