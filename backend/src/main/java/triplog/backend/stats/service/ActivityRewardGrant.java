package triplog.backend.stats.service;

/**
 * 활동 정책 보상을 중복 없이 지급하기 위한 개별 지급 요청입니다.
 *
 * @param eventKey 사용자별 보상 중복 방지 키
 * @param requestKey 클라이언트 요청 멱등성 키
 * @param sourceType 보상 발생 원본 유형
 * @param sourceId 보상 발생 원본 식별자
 * @param policyId 적용할 활동 정책 식별자
 */
public record ActivityRewardGrant(
        String eventKey,
        String requestKey,
        String sourceType,
        String sourceId,
        String policyId
) {
}
