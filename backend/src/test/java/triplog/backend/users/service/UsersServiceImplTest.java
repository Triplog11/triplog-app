package triplog.backend.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.users.dto.response.UsersResponse.EmailCheckResponse;
import triplog.backend.users.dto.response.UsersResponse.NicknameCheckResponse;
import triplog.backend.users.entity.Users;
import triplog.backend.users.repository.UsersRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static triplog.backend.users.entity.LoginType.GOOGLE;
import static triplog.backend.users.entity.LoginType.LOCAL;

/**
 * {@link UsersServiceImpl}의 사용자 인증 정보 조회 및 소셜 사용자 생성 흐름을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    private static final String EMAIL = "user@test.com";
    private static final String NICKNAME = "여행자";
    private static final String PASSWORD = "encoded-password";
    private static final String PROFILE_URL = "https://example.com/profile.png";
    private static final String DEFAULT_PROFILE_URL = "profile-default.png";

    @Mock
    private UsersRepository usersRepository;

    private UsersServiceImpl usersService;

    /**
     * 테스트 대상 서비스를 생성합니다.
     */
    @BeforeEach
    void setUp() {
        usersService = new UsersServiceImpl(usersRepository);
    }

    /**
     * 이메일과 로그인 타입으로 사용자를 찾으면 인증 정보만 담은 레코드로 변환되는지 검증합니다.
     */
    @Test
    @DisplayName("이메일과 로그인 타입으로 인증 정보를 조회한다")
    void findAuthInfoByEmailAndLoginType() {
        // given
        Users users = new Users(LOCAL, NICKNAME, PROFILE_URL, EMAIL, PASSWORD);
        given(usersRepository.findByEmailAndLoginType(EMAIL, LOCAL)).willReturn(Optional.of(users));

        // when
        Optional<UsersAuthInfo> result = usersService.findAuthInfoByEmailAndLoginType(EMAIL, LOCAL);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().usersId()).isEqualTo(users.getUsersId());
        assertThat(result.get().nickname()).isEqualTo(NICKNAME);
        assertThat(result.get().password()).isEqualTo(PASSWORD);
    }

    /**
     * 이메일과 로그인 타입에 해당하는 사용자가 없으면 빈 Optional을 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("인증 정보 조회 시 사용자가 없으면 빈 값을 반환한다")
    void findAuthInfoByEmailAndLoginType_Empty() {
        // given
        given(usersRepository.findByEmailAndLoginType(EMAIL, LOCAL)).willReturn(Optional.empty());

        // when
        Optional<UsersAuthInfo> result = usersService.findAuthInfoByEmailAndLoginType(EMAIL, LOCAL);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * 소셜 로그인 추가정보로 신규 사용자를 생성하고 저장된 사용자 요약 정보를 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("소셜 사용자 생성 시 입력한 프로필 URL을 저장한다")
    void createSocialUser() {
        // given
        given(usersRepository.save(any(Users.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        UsersSignupInfo result = usersService.createSocialUser(EMAIL, GOOGLE, NICKNAME, PROFILE_URL);

        // then
        ArgumentCaptor<Users> usersCaptor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(usersCaptor.capture());
        Users savedUsers = usersCaptor.getValue();

        assertThat(savedUsers.getEmail()).isEqualTo(EMAIL);
        assertThat(savedUsers.getLoginType()).isEqualTo(GOOGLE);
        assertThat(savedUsers.getNickname()).isEqualTo(NICKNAME);
        assertThat(savedUsers.getProfileUrl()).isEqualTo(PROFILE_URL);
        assertThat(savedUsers.getPassword()).isNull();
        assertThat(result.usersId()).isEqualTo(savedUsers.getUsersId());
        assertThat(result.nickname()).isEqualTo(NICKNAME);
    }

    /**
     * 프로필 URL이 비어 있으면 기본 프로필 URL을 저장하는지 검증합니다.
     */
    @Test
    @DisplayName("소셜 사용자 생성 시 프로필 URL이 비어 있으면 기본값을 저장한다")
    void createSocialUser_DefaultProfileUrl() {
        // given
        given(usersRepository.save(any(Users.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        UsersSignupInfo result = usersService.createSocialUser(EMAIL, GOOGLE, NICKNAME, " ");

        // then
        ArgumentCaptor<Users> usersCaptor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(usersCaptor.capture());
        Users savedUsers = usersCaptor.getValue();

        assertThat(savedUsers.getProfileUrl()).isEqualTo(DEFAULT_PROFILE_URL);
        assertThat(result.usersId()).isEqualTo(savedUsers.getUsersId());
        assertThat(result.nickname()).isEqualTo(NICKNAME);
    }

    /**
     * 닉네임을 사용하는 사용자가 없으면 사용 가능 응답을 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("닉네임 중복 확인 시 사용자가 없으면 사용 가능 응답을 반환한다")
    void checkNickname_Available() {
        // given
        given(usersRepository.existsByNickname(NICKNAME)).willReturn(false);

        // when
        NicknameCheckResponse result = usersService.checkNickname(NICKNAME);

        // then
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getMessage()).isEqualTo("사용 가능한 닉네임입니다.");
    }

    /**
     * 닉네임을 사용하는 사용자가 있으면 사용 불가 응답을 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("닉네임 중복 확인 시 사용자가 있으면 사용 불가 응답을 반환한다")
    void checkNickname_Unavailable() {
        // given
        given(usersRepository.existsByNickname(NICKNAME)).willReturn(true);

        // when
        NicknameCheckResponse result = usersService.checkNickname(NICKNAME);

        // then
        assertThat(result.getAvailable()).isFalse();
        assertThat(result.getMessage()).isEqualTo("중복된 닉네임입니다.");
    }

    /**
     * 이메일을 사용하는 사용자가 없으면 사용 가능 응답을 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("이메일 중복 확인 시 사용자가 없으면 사용 가능 응답을 반환한다")
    void checkEmail_Available() {
        // given
        given(usersRepository.existsByEmail(EMAIL)).willReturn(false);

        // when
        EmailCheckResponse result = usersService.checkEmail(EMAIL);

        // then
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getMessage()).isEqualTo("사용 가능한 이메일입니다.");
    }

    /**
     * 이메일을 사용하는 사용자가 있으면 사용 불가 응답을 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("이메일 중복 확인 시 사용자가 있으면 사용 불가 응답을 반환한다")
    void checkEmail_Unavailable() {
        // given
        given(usersRepository.existsByEmail(EMAIL)).willReturn(true);

        // when
        EmailCheckResponse result = usersService.checkEmail(EMAIL);

        // then
        assertThat(result.getAvailable()).isFalse();
        assertThat(result.getMessage()).isEqualTo("중복된 이메일입니다.");
    }
}

