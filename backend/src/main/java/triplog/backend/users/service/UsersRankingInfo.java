package triplog.backend.users.service;

/**
 * 랭킹 응답 구성에 필요한 사용자 요약 정보입니다.
 * <p>
 * Stats 도메인이 Users 엔티티에 직접 의존하지 않도록 사용자 도메인의 반환 계약으로 사용합니다.
 *
 * @param nickname 사용자 닉네임
 * @param profileUrl 사용자 프로필 이미지 URL
 */
public record UsersRankingInfo(
        String nickname,
        String profileUrl
) {
}
