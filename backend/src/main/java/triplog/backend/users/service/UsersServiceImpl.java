package triplog.backend.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.common.auth.entity.RefreshToken;
import triplog.backend.common.auth.repository.RefreshTokenRepository;
import triplog.backend.stats.service.StatsProfileInfo;
import triplog.backend.stats.service.StatsService;
import triplog.backend.users.dto.request.UsersRequest.ProfileUpdateRequest;
import triplog.backend.users.dto.request.UsersRequest.WithdrawalRequest;
import triplog.backend.users.dto.response.UsersResponse.EmailCheckResponse;
import triplog.backend.users.dto.response.UsersResponse.NicknameCheckResponse;
import triplog.backend.users.dto.response.UsersResponse.ProfileUpdateResponse;
import triplog.backend.users.dto.response.UsersResponse.WithdrawalResponse;
import triplog.backend.users.entity.LoginType;
import triplog.backend.users.entity.Users;
import triplog.backend.users.exception.UsersException;
import triplog.backend.users.repository.UsersRepository;
import java.util.Optional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static triplog.backend.users.exception.UsersErrorCode.NICKNAME_DUPLICATED;
import static triplog.backend.users.exception.UsersErrorCode.EMAIL_DUPLICATED;
import static triplog.backend.users.exception.UsersErrorCode.USER_NOT_FOUND;
import static triplog.backend.users.exception.UsersErrorCode.INVALID_WITHDRAWAL_REQUEST;
import static triplog.backend.users.exception.UsersErrorCode.WITHDRAWAL_USER_NOT_FOUND;

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

    private static final DateTimeFormatter RESPONSE_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String DEFAULT_PROFILE_URL =
            "https://res.cloudinary.com/pvswis5a/image/upload/v1784864852/basic-profile_dcuxor.png";

    private final UsersRepository usersRepository;
    private final StatsService statsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

    /**
     * 사용자 ID로 사용자를 조회합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 조회된 사용자
     * @throws UsersException 사용자가 존재하지 않는 경우
     */
    @Override
    public Users findById(String usersId) {
        return usersRepository.findById(usersId)
                .orElseThrow(() -> new UsersException(USER_NOT_FOUND));
    }

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
     * 로컬 회원가입 요청 정보를 기반으로 신규 사용자를 생성합니다.
     *
     * @param email 사용자 이메일
     * @param nickname 닉네임
     * @param profileUrl 프로필 이미지 URL
     * @param encodedPassword 암호화된 비밀번호
     * @return 저장된 사용자 요약 정보
     */
    @Override
    @Transactional
    public UsersSignupInfo createLocalUser(String email, String nickname, String profileUrl, String encodedPassword) {
        if (usersRepository.existsByEmail(email)) {
            throw new UsersException(EMAIL_DUPLICATED);
        }

        if (usersRepository.existsByNickname(nickname)) {
            throw new UsersException(NICKNAME_DUPLICATED);
        }

        String resolvedProfileUrl = profileUrl == null || profileUrl.isBlank() ? DEFAULT_PROFILE_URL : profileUrl;
        Users users = usersRepository.save(new Users(LoginType.LOCAL, nickname, resolvedProfileUrl, email, encodedPassword));

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
     * 이메일 사용 가능 여부를 확인합니다.
     *
     * @param email 확인할 이메일
     * @return 이메일 사용 가능 여부와 결과 메시지
     */
    @Override
    @Transactional(readOnly = true)
    public EmailCheckResponse checkEmail(String email) {
        boolean available = !usersRepository.existsByEmail(email);
        return EmailCheckResponse.toDto(available);
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

    /**
     * 요청 정보가 인증된 사용자와 일치하는지 확인한 뒤 계정과 관련 데이터를 삭제합니다.
     *
     * @param usersId 액세스 토큰으로 인증된 사용자 ID
     * @param request 회원 탈퇴 요청
     * @return 회원 탈퇴 처리 결과
     * @throws UsersException 사용자가 없거나 이메일 또는 리프레시 토큰이 일치하지 않는 경우
     */
    @Override
    @Transactional
    public WithdrawalResponse withdraw(String usersId, WithdrawalRequest request) {
        Users users = usersRepository.findById(usersId)
                .orElseThrow(() -> new UsersException(WITHDRAWAL_USER_NOT_FOUND));

        if (!users.getEmail().equals(request.getEmail())) {
            throw new UsersException(INVALID_WITHDRAWAL_REQUEST);
        }

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
                .filter(token -> usersId.equals(token.getUsersId()))
                .orElseThrow(() -> new UsersException(INVALID_WITHDRAWAL_REQUEST));

        deleteNonCascadingRelations(usersId);
        usersRepository.delete(users);
        usersRepository.flush();
        refreshTokenRepository.deleteById(refreshToken.getUsersId());

        String deletedAt = LocalDateTime.now(clock).format(RESPONSE_DATE_TIME_FORMAT);
        return new WithdrawalResponse(true, users.getEmail(), deletedAt);
    }

    /**
     * 외래 키에 삭제 전파가 설정되지 않은 사용자 종속 데이터를 먼저 삭제합니다.
     *
     * @param usersId 탈퇴할 사용자 ID
     */
    private void deleteNonCascadingRelations(String usersId) {
        usersRepository.deleteStatsByUsersId(usersId);
        usersRepository.deleteBadgesByUsersId(usersId);
        usersRepository.deleteRegionVisitLogsByUsersId(usersId);
        usersRepository.deleteLandmarkVisitLogsByUsersId(usersId);
        usersRepository.deleteAttractionVisitLogsByUsersId(usersId);
    }
}
