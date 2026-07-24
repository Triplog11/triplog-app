package triplog.backend.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * 지정한 누적 점수보다 높은 점수를 보유한 사용자 수를 조회합니다.
     *
     * @param overallScore 기준 누적 점수
     * @return 기준 점수보다 높은 사용자 수
     */
    long countByOverallScoreGreaterThan(int overallScore);

    /**
     * 지정한 월간 점수보다 높은 점수를 보유한 사용자 수를 조회합니다.
     *
     * @param monthScore 기준 월간 점수
     * @return 기준 점수보다 높은 사용자 수
     */
    long countByMonthScoreGreaterThan(int monthScore);

    /**
     * 사용자 주소 프로필 정보를 수정합니다.
     * <p>
     * 요청에서 전달되지 않은 필드는 {@code null}로 들어오며 기존 값을 유지합니다.
     *
     * @param usersId 수정할 사용자 ID
     * @param addressSi 변경할 시
     * @param addressDoGun 변경할 도/군
     * @param addressGu 변경할 구
     * @return 수정된 행 수
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Stats s
            set s.addressSi = coalesce(:addressSi, s.addressSi),
                s.addressDoGun = coalesce(:addressDoGun, s.addressDoGun),
                s.addressGu = coalesce(:addressGu, s.addressGu)
            where s.users.usersId = :usersId
            """)
    int updateProfileAddress(
            @Param("usersId") String usersId,
            @Param("addressSi") String addressSi,
            @Param("addressDoGun") String addressDoGun,
            @Param("addressGu") String addressGu
    );
}
