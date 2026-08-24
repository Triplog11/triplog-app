package triplog.backend.badge.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.badge.dto.response.BadgeListResult;
import triplog.backend.badge.dto.response.BadgeResponse;
import triplog.backend.badge.exception.BadgeErrorCode;
import triplog.backend.badge.exception.BadgeException;
import triplog.backend.badge.repository.BadgeQueryResult;
import triplog.backend.badge.repository.BadgeRepository;
import triplog.backend.badge.repository.UsersBadgeRepository;
import triplog.backend.badge.entity.UsersBadge;
import triplog.backend.achievement.service.AchievementContext;

import static triplog.backend.achievement.service.AchievementConditionEvaluator.isSatisfied;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 배지 조회 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UsersBadgeRepository usersBadgeRepository;

    @Override
    public Optional<RepresentativeBadgeInfo> getRepresentativeBadge(String usersId) {
        return usersBadgeRepository.findRepresentativeByUsersId(usersId)
                .map(RepresentativeBadgeInfo::from);
    }

    /**
     * 획득한 배지 행을 잠근 뒤 기존 대표 배지를 해제하고 요청 배지만 대표로 지정합니다.
     *
     * @throws BadgeException 사용자가 요청한 배지를 획득하지 않은 경우
     */
    @Override
    @Transactional
    public BadgeResponse.RepresentativeResponse changeRepresentativeBadge(
            String usersId,
            Long badgeId
    ) {
        List<UsersBadge> acquiredBadges =
                usersBadgeRepository.findAllByUsersIdForUpdate(usersId);

        UsersBadge representativeBadge = acquiredBadges.stream()
                .filter(usersBadge -> usersBadge.getBadge().getBadgeId().equals(badgeId))
                .findFirst()
                .orElseThrow(() -> new BadgeException(
                        BadgeErrorCode.BADGE_NOT_ACQUIRED
                ));

        acquiredBadges.forEach(usersBadge ->
                usersBadge.changeRepresentative(usersBadge == representativeBadge));

        return BadgeResponse.RepresentativeResponse.toDto(
                representativeBadge.getBadge()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BadgeResponse.BadgeDetailResponse getBadgeDetail(String usersId, Long badgeId) {
        return badgeRepository.findBadgeDetail(badgeId, usersId)
                .map(BadgeResponse.BadgeDetailResponse::toDto)
                .orElseThrow(() -> new BadgeException(BadgeErrorCode.BADGE_DETAIL_NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BadgeListResult getBadges(String usersId, String badgeType, Boolean isAcquired, Pageable pageable) {
        String normalizedBadgeType = normalize(badgeType);
        Page<BadgeQueryResult> result = badgeRepository.findBadges(
                usersId, normalizedBadgeType, isAcquired, pageable);

        if (pageable.getPageNumber() > 0 && pageable.getPageNumber() >= result.getTotalPages()) {
            throw new BadgeException(BadgeErrorCode.BADGES_NOT_FOUND);
        }

        if (Boolean.TRUE.equals(isAcquired)) {
            return BadgeResponse.BadgeListAcquiredResponse.toDto(result);
        }
        return BadgeResponse.BadgeListResponse.toDto(result);
    }

    /**
     * 사용자가 획득한 배지 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 획득 배지 수
     */
    @Override
    public int countAcquiredBadges(String usersId) {
        return Math.toIntExact(badgeRepository.countAcquiredBadgesByUsersId(usersId));
    }

    /**
     * DB에 등록된 미획득 뱃지 정책을 판정하고 조건을 충족한 뱃지를 최초 획득 처리합니다.
     *
     * @param usersId 사용자 식별자
     * @param context 현재 사용자 활동 지표
     * @return 이번 이벤트에서 최초 획득한 뱃지 목록
     */
    @Override
    @Transactional
    public List<AcquiredBadgeInfo> acquireEligibleBadges(
            String usersId,
            AchievementContext context
    ) {
        List<AcquiredBadgeInfo> acquiredBadges = new ArrayList<>();
        for (triplog.backend.badge.entity.Badge badge
                : badgeRepository.findUnacquiredBadges(usersId)) {
            if (!isSatisfied(
                    context,
                    badge.getBadgeTarget(),
                    badge.getBadgeOperator(),
                    badge.getBadgeValue()
            )) {
                continue;
            }
            if (usersBadgeRepository.insertIfAbsent(usersId, badge.getBadgeId()) == 1) {
                acquiredBadges.add(new AcquiredBadgeInfo(
                        badge.getBadgeId(), badge.getBadgeName()
                ));
            }
        }
        return List.copyOf(acquiredBadges);
    }

    /**
     * 빈 타입 조건은 전체 조회를 의미하는 {@code null}로 바꾸고,
     * 값이 있으면 앞뒤 공백 제거 후 대문자로 정규화합니다.
     *
     * @param badgeType 요청으로 받은 배지 타입
     * @return 정규화된 배지 타입
     */
    private String normalize(String badgeType) {
        if (badgeType == null || badgeType.isBlank()) {
            return null;
        }
        return badgeType.trim().toUpperCase();
    }
}
