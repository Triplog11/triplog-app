package triplog.backend.appellation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.appellation.entity.Appellation;

import java.util.List;

/**
 * 칭호 정책을 저장하고 사용자의 미획득 칭호를 조회합니다.
 */
public interface AppellationRepository extends JpaRepository<Appellation, Long> {

    @Query("""
            select a
            from Appellation a
            where not exists (
                select 1
                from UsersAppellation ua
                where ua.appellation = a
                  and ua.usersId = :usersId
            )
            order by a.appellationId asc
            """)
    List<Appellation> findUnacquiredAppellations(@Param("usersId") String usersId);
}
