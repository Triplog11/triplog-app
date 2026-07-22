package triplog.backend.users.service;

import triplog.backend.users.entity.LoginType;
import triplog.backend.users.dto.request.UsersRequest.ProfileUpdateRequest;
import triplog.backend.users.dto.response.UsersResponse.EmailCheckResponse;
import triplog.backend.users.dto.response.UsersResponse.NicknameCheckResponse;
import triplog.backend.users.dto.response.UsersResponse.ProfileUpdateResponse;
import java.util.Optional;

/**
 * 사용자(Users)와 관련된 비즈니스 로직을 정의하는 Service 인터페이스입니다.
 * <p>
 * 회원 가입, 로그인, 사용자 정보 조회 및 수정 등 사용자 도메인의 비즈니스 기능을 선언합니다.
 */
public interface UsersService {

    /**
     * 이메일과 로그인 타입으로 인증 처리에 필요한 사용자 정보를 조회합니다.
     *
     * @param email 사용자 이메일
     * @param loginType 로그인 타입
     * @return 인증 처리에 필요한 사용자 정보
     */
    Optional<UsersAuthInfo> findAuthInfoByEmailAndLoginType(String email, LoginType loginType);

    /**
     * 소셜 로그인 추가정보를 기반으로 신규 사용자를 생성합니다.
     *
     * @param email 사용자 이메일
     * @param loginType 소셜 로그인 타입
     * @param nickname 닉네임
     * @param profileUrl 프로필 이미지 URL
     * @return 저장된 사용자 요약 정보
     */
    UsersSignupInfo createSocialUser(String email, LoginType loginType, String nickname, String profileUrl);

    /**
     * 로컬 회원가입 요청 정보를 기반으로 신규 사용자를 생성합니다.
     *
     * @param email 사용자 이메일
     * @param nickname 닉네임
     * @param profileUrl 프로필 이미지 URL
     * @param encodedPassword 암호화된 비밀번호
     * @return 저장된 사용자 요약 정보
     */
    UsersSignupInfo createLocalUser(String email, String nickname, String profileUrl, String encodedPassword);

    /**
     * 닉네임 사용 가능 여부를 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 닉네임 사용 가능 여부와 결과 메시지
     */
    NicknameCheckResponse checkNickname(String nickname);
    
    /**
     * 이메일 사용 가능 여부를 확인합니다.
     *
     * @param email 확인할 이메일
     * @return 이메일 사용 가능 여부와 결과 메시지
     */
    EmailCheckResponse checkEmail(String email);
  
    /**
     * 사용자 프로필 정보를 수정하고 수정 후 사용자 요약 정보를 조회합니다.
     *
     * @param usersId 수정할 사용자 ID
     * @param request 프로필 수정 요청 DTO
     * @return 수정 후 사용자 프로필 응답 정보
     */
    ProfileUpdateResponse updateProfile(String usersId, ProfileUpdateRequest request);
}
