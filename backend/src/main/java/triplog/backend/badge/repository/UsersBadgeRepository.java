package triplog.backend.badge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.badge.entity.UsersBadge;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * 사용자의 뱃지 최초 획득을 원자적으로 저장합니다.
 */
public interface UsersBadgeRepository extends JpaRepository<UsersBadge, Long> {

    /**
     * 로그인 사용자의 대표 배지를 배지 정보와 함께 조회합니다.
     */
    @Query("""
            select ub
            from UsersBadge ub
            join fetch ub.badge
            where ub.users.usersId = :usersId
              and ub.representative = true
            """)
    Optional<UsersBadge> findRepresentativeByUsersId(
            @Param("usersId") String usersId
    );

    /**
     * 대표 배지 변경이 끝날 때까지 사용자의 획득 배지를 잠가 동시 변경을 직렬화합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select ub
            from UsersBadge ub
            join fetch ub.badge
            where ub.users.usersId = :usersId
            order by ub.usersBadgeId asc
            """)
    List<UsersBadge> findAllByUsersIdForUpdate(@Param("usersId") String usersId);

    /**
     * 사용자가 아직 획득하지 않은 뱃지만 저장합니다.
     *
     * @param usersId 사용자 식별자
     * @param badgeId 뱃지 식별자
     * @return 새로 저장한 경우 1, 이미 획득한 경우 0
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            INSERT IGNORE INTO users_badge (users_id, badge_id, is_representative)
            VALUES (:usersId, :badgeId, FALSE)
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("usersId") String usersId,
            @Param("badgeId") Long badgeId
    );
}
