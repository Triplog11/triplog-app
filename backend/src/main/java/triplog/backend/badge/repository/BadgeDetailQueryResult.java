package triplog.backend.badge.repository;

/**
 * 배지와 사용자 배지 테이블을 조합한 상세 조회 전용 프로젝션입니다.
 * <p>
 * MySQL Boolean CASE 결과의 타입 변환 문제를 방지하기 위해 획득 여부와 대표 여부를
 * {@code 1} 또는 {@code 0}의 정수로 전달합니다.
 */
public record BadgeDetailQueryResult(
        Long badgeId,
        String badgeName,
        String badgeUrl,
        Integer badgeGroup,
        String badgeType,
        String badgeTarget,
        String badgeOperator,
        Integer badgeValue,
        Integer acquired,
        Integer representative
) {
}
