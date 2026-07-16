package triplog.backend.stats.service;

/**
 * 사용자 프로필 수정 응답에 필요한 통계 도메인 주소 요약 정보입니다.
 * <p>
 * 프로필 조합 서비스가 Stats 엔티티나 Repository에 직접 의존하지 않도록
 * StatsService의 반환 계약으로 사용합니다.
 *
 * @param addressSi 시
 * @param addressDoGun 도/군
 * @param addressGu 구
 */
public record StatsProfileInfo(
        String addressSi,
        String addressDoGun,
        String addressGu
) {
}
