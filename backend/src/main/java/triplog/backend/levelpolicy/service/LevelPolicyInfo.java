package triplog.backend.levelpolicy.service;

/**
 * 다음 레벨 안내에 필요한 레벨 정책 요약 정보입니다.
 * <p>
 * Stats 도메인이 LevelPolicy 엔티티에 직접 의존하지 않도록 서비스 반환 계약으로 사용합니다.
 *
 * @param nextLevel 다음 레벨
 * @param requiredXp 다음 레벨 달성 조건 경험치
 */
public record LevelPolicyInfo(
        Integer nextLevel,
        Integer requiredXp
) {
}
