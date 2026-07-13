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

/**
 * 배지 조회 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public BadgeResponse.BadgeDetailResponse getBadgeDetail(String usersId, Long badgeId) {
        return badgeRepository.findBadgeDetail(badgeId, usersId)
                .map(BadgeResponse.BadgeDetailResponse::from)
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
