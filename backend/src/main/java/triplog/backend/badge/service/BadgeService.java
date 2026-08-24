package triplog.backend.badge.service;

import org.springframework.data.domain.Pageable;
import triplog.backend.badge.dto.response.BadgeListResult;
import triplog.backend.badge.dto.response.BadgeResponse;
import triplog.backend.achievement.service.AchievementContext;

import java.util.List;
import java.util.Optional;

/**
 * 배지 목록 및 상세 조회 기능의 계약을 정의합니다.
 */
public interface BadgeService {

    /**
     * 로그인 사용자의 대표 배지를 조회합니다.
     *
     * @param usersId 로그인 사용자 식별자
     * @return 대표 배지, 지정하지 않았으면 빈 값
     */
    Optional<RepresentativeBadgeInfo> getRepresentativeBadge(String usersId);

    /**
     * 사용자가 획득한 배지 중 하나를 대표 배지로 지정합니다.
     *
     * @param usersId 로그인 사용자 식별자
     * @param badgeId 대표로 지정할 배지 식별자
     * @return 지정된 대표 배지 정보
     */
    BadgeResponse.RepresentativeResponse changeRepresentativeBadge(
            String usersId,
            Long badgeId
    );

    /**
     * 로그인 사용자의 획득 상태를 포함한 배지 목록을 조회합니다.
     *
     * @param usersId 로그인 사용자 ID
     * @param badgeType 필터링할 배지 타입
     * @param isAcquired 필터링할 획득 여부
     * @param pageable 페이지 정보
     * @return 조회 조건에 따른 전체 또는 획득 배지 목록 응답
     */
    BadgeListResult getBadges(String usersId, String badgeType, Boolean isAcquired, Pageable pageable);

    /**
     * 로그인 사용자의 획득 상태를 포함한 배지 상세 정보를 조회합니다.
     *
     * @param usersId 로그인 사용자 ID
     * @param badgeId 조회할 배지 ID
     * @return 배지 상세 응답
     * @throws triplog.backend.badge.exception.BadgeException 배지가 존재하지 않는 경우
     */
    BadgeResponse.BadgeDetailResponse getBadgeDetail(String usersId, Long badgeId);

    /**
     * 사용자가 획득한 배지 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 획득 배지 수
     */
    int countAcquiredBadges(String usersId);

    /**
     * 현재 활동 지표로 미획득 뱃지 조건을 판정하고 최초 획득 상태를 저장합니다.
     *
     * @param usersId 사용자 식별자
     * @param context 현재 사용자 활동 지표
     * @return 이번 호출에서 최초 획득한 뱃지 목록
     */
    List<AcquiredBadgeInfo> acquireEligibleBadges(
            String usersId,
            AchievementContext context
    );
}
