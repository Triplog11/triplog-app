package triplog.backend.badge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.badge.entity.Badge;

import java.util.Optional;

/**
 * 배지 엔티티와 로그인 사용자의 배지 획득 정보를 조회하는 Repository입니다.
 */
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    /**
     * 배지 ID와 사용자 ID로 배지 상세 정보 및 사용자 획득 상태를 조회합니다.
     *
     * @param badgeId 조회할 배지 ID
     * @param usersId 로그인 사용자 ID
     * @return 배지 상세 조회 결과, 배지가 없으면 빈 값
     */
    @Query("""
            select new triplog.backend.badge.repository.BadgeDetailQueryResult(
                b.badgeId, b.badgeName, b.badgeUrl, b.badgeGroup, b.badgeType,
                b.badgeTarget, b.badgeOperator, b.badgeValue,
                case when ub.usersBadgeId is not null then 1 else 0 end,
                case when ub.usersBadgeId is not null and ub.representative = true then 1 else 0 end)
            from Badge b
            left join UsersBadge ub on ub.badge = b and ub.users.usersId = :usersId
            where b.badgeId = :badgeId
            """)
    Optional<BadgeDetailQueryResult> findBadgeDetail(@Param("badgeId") Long badgeId,
                                                     @Param("usersId") String usersId);

    /**
     * 배지 타입과 획득 여부 조건을 적용하여 사용자의 배지 목록을 페이지 단위로 조회합니다.
     *
     * @param usersId 로그인 사용자 ID
     * @param badgeType 배지 타입, 전체 타입 조회 시 {@code null}
     * @param isAcquired 획득 여부, 전체 조회 시 {@code null}
     * @param pageable 페이지 정보
     * @return 조건에 맞는 배지 조회 결과
     */
    @Query(value = """
            select new triplog.backend.badge.repository.BadgeQueryResult(
                b.badgeId, b.badgeName, b.badgeUrl, b.badgeType, b.badgeTarget, b.badgeValue,
                case when ub.usersBadgeId is not null then 1 else 0 end,
                case when ub.usersBadgeId is not null and ub.representative = true then 1 else 0 end)
            from Badge b
            left join UsersBadge ub on ub.badge = b and ub.users.usersId = :usersId
            where (:badgeType is null or b.badgeType = :badgeType)
              and (:isAcquired is null
                   or (:isAcquired = true and ub.usersBadgeId is not null)
                   or (:isAcquired = false and ub.usersBadgeId is null))
            order by b.badgeId asc
            """,
            countQuery = """
            select count(b)
            from Badge b
            left join UsersBadge ub on ub.badge = b and ub.users.usersId = :usersId
            where (:badgeType is null or b.badgeType = :badgeType)
              and (:isAcquired is null
                   or (:isAcquired = true and ub.usersBadgeId is not null)
                   or (:isAcquired = false and ub.usersBadgeId is null))
            """)
    Page<BadgeQueryResult> findBadges(@Param("usersId") String usersId,
                                      @Param("badgeType") String badgeType,
                                      @Param("isAcquired") Boolean isAcquired,
                                      Pageable pageable);
}
