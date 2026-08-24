package triplog.backend.badge.service;

import triplog.backend.badge.entity.UsersBadge;

/**
 * 다른 도메인에서 사용할 대표 배지 조회 결과입니다.
 *
 * @param badgeId 대표 배지 식별자
 * @param badgeName 대표 배지 이름
 * @param badgeUrl 대표 배지 이미지 URL
 */
public record RepresentativeBadgeInfo(
        Long badgeId,
        String badgeName,
        String badgeUrl
) {

    /**
     * 사용자 배지 엔티티를 대표 배지 조회 결과로 변환합니다.
     *
     * @param usersBadge 대표 사용자 배지
     * @return 대표 배지 조회 결과
     */
    public static RepresentativeBadgeInfo from(UsersBadge usersBadge) {
        return new RepresentativeBadgeInfo(
                usersBadge.getBadge().getBadgeId(),
                usersBadge.getBadge().getBadgeName(),
                usersBadge.getBadge().getBadgeUrl()
        );
    }
}
