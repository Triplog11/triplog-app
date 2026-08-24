package triplog.backend.appellation.service;

/**
 * 이번 이벤트에서 사용자가 최초 획득한 칭호 정보입니다.
 *
 * @param appellationId 칭호 식별자
 * @param appellationName 칭호명
 */
public record AcquiredAppellationInfo(Long appellationId, String appellationName) {
}
