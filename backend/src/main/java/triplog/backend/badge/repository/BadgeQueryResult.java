package triplog.backend.badge.repository;

/**
 * 배지와 사용자 배지 테이블을 조합한 목록 조회 전용 프로젝션입니다.
 * <p>
 * 획득 여부와 대표 여부는 JDBC Boolean 변환의 DB 의존성을 피하기 위해
 * {@code 1} 또는 {@code 0}의 정수로 전달합니다.
 *
 * @param badgeId 배지 식별자
 * @param badgeName 배지 이름
 * @param badgeUrl 배지 이미지 URL
 * @param badgeType 배지 조건 유형
 * @param badgeTarget 배지 조건 대상
 * @param badgeValue 배지 조건 기준값
 * @param acquired 사용자 획득 여부(1 또는 0)
 * @param representative 대표 배지 여부(1 또는 0)
 */
public record BadgeQueryResult(
        Long badgeId,
        String badgeName,
        String badgeUrl,
        String badgeType,
        String badgeTarget,
        Integer badgeValue,
        Integer acquired,
        Integer representative
) {
}
