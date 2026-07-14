package triplog.backend.users.service;

/**
 * 인증 처리에 필요한 사용자 요약 정보입니다.
 * <p>
 * Auth 도메인이 Users 엔티티에 직접 의존하지 않도록 UsersService의 반환 계약으로 사용합니다.
 *
 * @param usersId 사용자 식별자
 * @param nickname 닉네임
 * @param password 암호화된 비밀번호. 소셜 로그인 사용자는 null입니다.
 */
public record UsersAuthInfo(
        String usersId,
        String nickname,
        String password
) {
}