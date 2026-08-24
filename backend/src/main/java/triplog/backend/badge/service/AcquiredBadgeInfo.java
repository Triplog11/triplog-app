package triplog.backend.badge.service;

/**
 * 이번 이벤트에서 사용자가 최초 획득한 뱃지 정보입니다.
 *
 * @param badgeId 뱃지 식별자
 * @param badgeName 뱃지명
 */
public record AcquiredBadgeInfo(Long badgeId, String badgeName) {
}
