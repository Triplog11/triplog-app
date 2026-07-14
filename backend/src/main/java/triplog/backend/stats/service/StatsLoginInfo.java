package triplog.backend.stats.service;

/**
 * 로그인 성공 응답에 포함할 사용자의 통계 요약 정보입니다.
 * <p>
 * Auth 도메인이 Stats 엔티티나 Repository 조회 결과에 직접 의존하지 않도록
 * StatsService의 반환 계약으로 사용합니다.
 *
 * @param level 사용자의 현재 레벨
 * @param xp 사용자의 현재 경험치
 * @param tier 사용자의 현재 티어
 */
public record StatsLoginInfo(
        Integer level,
        Integer xp,
        String tier
) {
}