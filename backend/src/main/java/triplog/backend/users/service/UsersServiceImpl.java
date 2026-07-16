package triplog.backend.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.stats.service.StatsProfileInfo;
import triplog.backend.stats.service.StatsService;
import triplog.backend.users.dto.request.UsersRequest.ProfileUpdateRequest;
import triplog.backend.users.dto.response.UsersResponse.NicknameCheckResponse;
import triplog.backend.users.dto.response.UsersResponse.ProfileUpdateResponse;
import triplog.backend.users.entity.LoginType;
import triplog.backend.users.entity.Users;
import triplog.backend.users.exception.UsersException;
import triplog.backend.users.repository.UsersRepository;

import java.util.Optional;

import static triplog.backend.users.exception.UsersErrorCode.NICKNAME_DUPLICATED;
import static triplog.backend.users.exception.UsersErrorCode.USER_NOT_FOUND;

/**
 * {@link UsersService}의 구현 클래스입니다.
 * <p>
 * 사용자(Users)와 관련된 비즈니스 로직을 처리하며,
 * Repository를 통해 사용자 데이터를 조회하고 관리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UsersServiceImpl implements UsersService {

    private static final String DEFAULT_PROFILE_URL = "profile-default.png";

    private final UsersRepository usersRepository;
    private final StatsService statsService;

    /**
     * 이메일과 로그인 타입으로 인증 처리에 필요한 사용자 정보를 조회합니다.
     *
     * @param email 사용자 이메일
     * @param loginType 로그인 타입
     * @return 인증 처리에 필요한 사용자 정보
     */
    @Override
    public Optional<UsersAuthInfo> findAuthInfoByEmailAndLoginType(String email, LoginType loginType) {
        return usersRepository.findByEmailAndLoginType(email, loginType)
                .map(users -> new UsersAuthInfo(
                        users.getUsersId(),
                        users.getNickname(),
                        users.getPassword()
                ));
    }

    /**
     * 소셜 로그인 추가정보를 기반으로 신규 사용자를 생성합니다.
     *
     * @param email 사용자 이메일
     * @param loginType 소셜 로그인 타입
     * @param nickname 닉네임
     * @param profileUrl 프로필 이미지 URL
     * @return 저장된 사용자 요약 정보
     */
    @Override
    @Transactional
    public UsersSignupInfo createSocialUser(String email, LoginType loginType, String nickname, String profileUrl) {
        String resolvedProfileUrl = profileUrl == null || profileUrl.isBlank() ? DEFAULT_PROFILE_URL : profileUrl;
        Users users = usersRepository.save(new Users(loginType, nickname, resolvedProfileUrl, email, null));

        return new UsersSignupInfo(users.getUsersId(), users.getNickname());
    }

    /**
     * 닉네임 사용 가능 여부를 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 닉네임 사용 가능 여부와 결과 메시지
     */
    @Override
    public NicknameCheckResponse checkNickname(String nickname) {
        boolean available = !usersRepository.existsByNickname(nickname);
        return NicknameCheckResponse.toDto(available);
    }

    /**
     * 사용자 프로필 정보를 수정하고 수정 후 사용자 요약 정보를 조회합니다.
     * <p>
     * 닉네임이 전달된 경우 기존 닉네임 중복 확인 로직을 사용해 사용 가능 여부를 먼저 검증합니다.
     *
     * @param usersId 수정할 사용자 ID
     * @param request 프로필 수정 요청 DTO
     * @return 수정 후 사용자 프로필 응답 정보
     */
    @Override
    @Transactional
    public ProfileUpdateResponse updateProfile(String usersId, ProfileUpdateRequest request) {
        String nickname = request.getNickname();
        String profileUrl = request.getProfileUrl();

        if (nickname != null && usersRepository.existsByNickname(nickname)) {
            throw new UsersException(NICKNAME_DUPLICATED);
        }

        int updatedCount = usersRepository.updateProfile(usersId, nickname, profileUrl);
        if (updatedCount == 0) {
            throw new UsersException(USER_NOT_FOUND);
        }

        Users users = usersRepository.findById(usersId)
                .orElseThrow(() -> new UsersException(USER_NOT_FOUND));
        StatsProfileInfo stats = statsService.updateProfileAddress(
                usersId,
                request.getAddressSi(),
                request.getAddressDoGun(),
                request.getAddressGu()
        );

        return new ProfileUpdateResponse(
                users.getUsersId(),
                users.getNickname(),
                stats.addressSi(),
                stats.addressDoGun(),
                stats.addressGu(),
                users.getProfileUrl()
        );
    }
}
