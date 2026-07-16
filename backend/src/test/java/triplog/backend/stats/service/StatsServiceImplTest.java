package triplog.backend.stats.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.stats.entity.Stats;
import triplog.backend.stats.exception.StatsException;
import triplog.backend.stats.repository.StatsRepository;
import triplog.backend.users.entity.Users;
import triplog.backend.users.repository.UsersRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static triplog.backend.stats.exception.StatsErrorCode.STATS_NOT_FOUND;
import static triplog.backend.users.entity.LoginType.GOOGLE;

/**
 * {@link StatsServiceImpl}의 통계 조회 및 초기 통계 생성 흐름을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    private static final String USERS_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String EMAIL = "user@test.com";
    private static final String NICKNAME = "여행자";
    private static final String PROFILE_URL = "profile-default.png";
    private static final String ADDRESS_SI = "수원시";
    private static final String ADDRESS_DO_GUN = "경기도";
    private static final String ADDRESS_GU = "팔달구";

    @Mock
    private StatsRepository statsRepository;

    @Mock
    private UsersRepository usersRepository;

    private StatsServiceImpl statsService;

    /**
     * 테스트 대상 서비스를 생성합니다.
     */
    @BeforeEach
    void setUp() {
        statsService = new StatsServiceImpl(statsRepository, usersRepository);
    }

    /**
     * 사용자 ID로 통계를 조회하면 로그인 응답에 필요한 레벨, 경험치, 티어 정보를 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("사용자 ID로 로그인 통계 정보를 조회한다")
    void getLoginStats() {
        // given
        Users users = createUsers();
        Stats stats = new Stats(users, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU);
        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(stats));

        // when
        StatsLoginInfo result = statsService.getLoginStats(USERS_ID);

        // then
        assertThat(result.level()).isEqualTo(1);
        assertThat(result.xp()).isEqualTo(0);
        assertThat(result.tier()).isEqualTo("BRONZE");
    }

    /**
     * 사용자 ID에 해당하는 통계가 없으면 {@link StatsException}이 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("로그인 통계 정보가 없으면 예외가 발생한다")
    void getLoginStats_StatsNotFound() {
        // given
        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> statsService.getLoginStats(USERS_ID))
                .isInstanceOf(StatsException.class)
                .extracting("errorCode")
                .isEqualTo(STATS_NOT_FOUND);
    }

    /**
     * 신규 사용자의 초기 통계를 생성하고 기본 레벨, 경험치, 티어 정보를 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("신규 사용자 초기 통계 정보를 생성한다")
    void createInitialStats() {
        // given
        Users users = createUsers();
        given(usersRepository.findById(USERS_ID)).willReturn(Optional.of(users));
        given(statsRepository.save(any(Stats.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        StatsLoginInfo result = statsService.createInitialStats(USERS_ID, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU);

        // then
        assertThat(result.level()).isEqualTo(1);
        assertThat(result.xp()).isEqualTo(0);
        assertThat(result.tier()).isEqualTo("BRONZE");

        ArgumentCaptor<Stats> statsCaptor = ArgumentCaptor.forClass(Stats.class);
        verify(statsRepository).save(statsCaptor.capture());
        Stats savedStats = statsCaptor.getValue();
        assertThat(savedStats.getUsers()).isEqualTo(users);
        assertThat(savedStats.getAddressSi()).isEqualTo(ADDRESS_SI);
        assertThat(savedStats.getAddressDoGun()).isEqualTo(ADDRESS_DO_GUN);
        assertThat(savedStats.getAddressGu()).isEqualTo(ADDRESS_GU);
    }

    /**
     * 초기 통계를 생성할 사용자를 찾을 수 없으면 통계를 저장하지 않고 {@link StatsException}이 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("초기 통계 생성 시 사용자가 없으면 예외가 발생한다")
    void createInitialStats_UsersNotFound() {
        // given
        given(usersRepository.findById(USERS_ID)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> statsService.createInitialStats(USERS_ID, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU))
                .isInstanceOf(StatsException.class)
                .extracting("errorCode")
                .isEqualTo(STATS_NOT_FOUND);
        verify(statsRepository, never()).save(any(Stats.class));
    }

    /**
     * 테스트에 사용할 사용자 엔티티를 생성합니다.
     */
    private Users createUsers() {
        return new Users(GOOGLE, NICKNAME, PROFILE_URL, EMAIL, null);
    }
    /**
     * 주소 프로필 수정 요청이 정상 처리되면 수정 후 주소 정보를 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("주소 프로필 수정 요청 시 수정 후 주소 정보를 반환한다")
    void updateProfileAddress() {
        // given
        Users users = createUsers();
        Stats stats = new Stats(users, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU);
        given(statsRepository.updateProfileAddress(USERS_ID, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU)).willReturn(1);
        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(stats));

        // when
        StatsProfileInfo result = statsService.updateProfileAddress(USERS_ID, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU);

        // then
        verify(statsRepository).updateProfileAddress(USERS_ID, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU);
        assertThat(result.addressSi()).isEqualTo(ADDRESS_SI);
        assertThat(result.addressDoGun()).isEqualTo(ADDRESS_DO_GUN);
        assertThat(result.addressGu()).isEqualTo(ADDRESS_GU);
    }

    /**
     * 주소 프로필 수정 대상 통계 정보가 없으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("주소 프로필 수정 대상 통계 정보가 없으면 예외가 발생한다")
    void updateProfileAddress_ProfileUpdateTargetNotFound() {
        // given
        given(statsRepository.updateProfileAddress(USERS_ID, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU)).willReturn(0);

        // when
        // then
        assertThatThrownBy(() -> statsService.updateProfileAddress(USERS_ID, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU))
                .isInstanceOf(StatsException.class)
                .extracting("errorCode")
                .isEqualTo(triplog.backend.stats.exception.StatsErrorCode.PROFILE_UPDATE_TARGET_NOT_FOUND);
        verify(statsRepository, never()).findByUsersUsersId(USERS_ID);
    }
}
