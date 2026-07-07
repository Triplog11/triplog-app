package triplog.backend.users.entity;

/**
 * 사용자의 로그인 방식을 정의하는 Enum 클래스입니다.
 * <p>
 * 사용자의 인증 방식에 따라 네이버, 구글 또는 자체(Local) 로그인 여부를
 * 구분하기 위해 사용됩니다.
 */
public enum LoginType {
    NAVER, GOOGLE, LOCAL
}
