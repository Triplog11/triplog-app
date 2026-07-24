package triplog.backend.rankpolicy.service;

/**
 * 다음 티어 안내에 필요한 랭크 정책 요약 정보입니다.
 * <p>
 * Stats 도메인이 RankPolicy 엔티티에 직접 의존하지 않도록 서비스 반환 계약으로 사용합니다.
 *
 * @param tier 다음 티어
 * @param requiredScore 다음 티어 달성 조건 점수
 */
public record RankPolicyInfo(
        String tier,
        Integer requiredScore
) {
}
