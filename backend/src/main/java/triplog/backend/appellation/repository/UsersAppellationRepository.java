package triplog.backend.appellation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.appellation.entity.UsersAppellation;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * 사용자의 칭호 최초 획득을 원자적으로 저장합니다.
 */
public interface UsersAppellationRepository extends JpaRepository<UsersAppellation, Long> {

    /**
     * 로그인 사용자의 대표 칭호를 칭호 정보와 함께 조회합니다.
     */
    @Query("""
            select ua
            from UsersAppellation ua
            join fetch ua.appellation
            where ua.usersId = :usersId
              and ua.representative = true
            """)
    Optional<UsersAppellation> findRepresentativeByUsersId(
            @Param("usersId") String usersId
    );

    /**
     * 사용자가 획득한 칭호를 대표 칭호 우선, 획득 순서대로 조회합니다.
     */
    @Query("""
            select ua
            from UsersAppellation ua
            join fetch ua.appellation
            where ua.usersId = :usersId
            order by ua.representative desc, ua.usersAppellationId asc
            """)
    List<UsersAppellation> findAllAcquiredByUsersId(@Param("usersId") String usersId);

    /**
     * 대표 칭호 변경이 끝날 때까지 사용자의 획득 칭호를 잠가 동시 변경을 직렬화합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select ua
            from UsersAppellation ua
            join fetch ua.appellation
            where ua.usersId = :usersId
            order by ua.usersAppellationId asc
            """)
    List<UsersAppellation> findAllByUsersIdForUpdate(@Param("usersId") String usersId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            INSERT IGNORE INTO users_appellation (
                users_id, appellation_id, is_representative
            )
            VALUES (:usersId, :appellationId, FALSE)
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("usersId") String usersId,
            @Param("appellationId") Long appellationId
    );
}
