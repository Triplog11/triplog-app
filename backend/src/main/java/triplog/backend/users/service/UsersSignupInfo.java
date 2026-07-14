package triplog.backend.users.service;

/**
 * 신규 회원 생성 후 인증 흐름에 전달할 사용자 요약 정보입니다.
 * <p>
 * Auth 도메인이 Users 엔티티에 직접 의존하지 않도록 UsersService의 반환 계약으로 사용합니다.
 *
 * @param usersId 사용자 식별자
 * @param nickname 닉네임
 */
public record UsersSignupInfo(
        String usersId,
        String nickname
) {
}