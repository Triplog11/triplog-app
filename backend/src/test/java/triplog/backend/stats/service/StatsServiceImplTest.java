package triplog.backend.stats.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import triplog.backend.levelpolicy.service.LevelPolicyInfo;
import triplog.backend.levelpolicy.service.LevelPolicyService;
import triplog.backend.rankpolicy.service.RankPolicyInfo;
import triplog.backend.rankpolicy.service.RankPolicyService;
import triplog.backend.stats.dto.response.StatsResponse.MyRankingResponse;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;
import triplog.backend.stats.dto.response.StatsResponse.RankingListResponse;
import triplog.backend.stats.entity.Stats;
import triplog.backend.stats.exception.StatsException;
import triplog.backend.stats.repository.StatsRepository;
import triplog.backend.users.entity.Users;
import triplog.backend.users.repository.UsersRepository;
import triplog.backend.users.service.UsersRankingInfo;
import triplog.backend.users.service.UsersRankingService;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static triplog.backend.stats.exception.StatsErrorCode.MY_RANKING_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.MY_STATS_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.RANKING_NOT_FOUND;
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

    @Mock
    private RankPolicyService rankPolicyService;

    @Mock
    private UsersRankingService usersRankingService;

    @Mock
    private LevelPolicyService levelPolicyService;

    private StatsServiceImpl statsService;

    @BeforeEach
    void setUp() {
        statsService = new StatsServiceImpl(
                statsRepository,
                usersRepository,
                usersRankingService,
                rankPolicyService,
                levelPolicyService
        );
    }

    /**
     * 사용자 점수보다 높은 사용자 수를 기준으로 전체 및 월간 순위를 계산하는지 검증합니다.
     */
    @Test
    @DisplayName("내 랭킹 정보와 다음 티어 정책을 조회한다")
    void getMyRanking() {
        // given
        Stats stats = mock(Stats.class);
        UsersRankingInfo usersInfo = new UsersRankingInfo(NICKNAME, PROFILE_URL);
        RankPolicyInfo nextRankPolicy = new RankPolicyInfo("SILVER", 1500);
        when(stats.getOverallScore()).thenReturn(1250);
        when(stats.getMonthScore()).thenReturn(220);
        when(stats.getQuarterScore()).thenReturn(680);
        when(stats.getStatsLevel()).thenReturn(3);
        when(stats.getCurrentTier()).thenReturn("BRONZE");
        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(stats));
        given(statsRepository.countByOverallScoreGreaterThan(1250)).willReturn(119L);
        given(statsRepository.countByMonthScoreGreaterThan(220)).willReturn(33L);
        given(statsRepository.countByQuarterScoreGreaterThan(680)).willReturn(55L);
        given(usersRankingService.getRankingInfo(USERS_ID)).willReturn(usersInfo);
        given(rankPolicyService.findNextRankPolicy(1250))
                .willReturn(Optional.of(nextRankPolicy));

        // when
        MyRankingResponse response = statsService.getMyRanking(USERS_ID);

        // then
        assertThat(response.getNickname()).isEqualTo(NICKNAME);
        assertThat(response.getProfileUrl()).isEqualTo(PROFILE_URL);
        assertThat(response.getTotalRank()).isEqualTo(120);
        assertThat(response.getMonthlyRank()).isEqualTo(34);
        assertThat(response.getQuarterRank()).isEqualTo(56);
        assertThat(response.getOverallScore()).isEqualTo(1250);
        assertThat(response.getMonthScore()).isEqualTo(220);
        assertThat(response.getQuarterScore()).isEqualTo(680);
        assertThat(response.getLevel()).isEqualTo(3);
        assertThat(response.getTier()).isEqualTo("BRONZE");
        assertThat(response.getNextTier()).isEqualTo("SILVER");
        assertThat(response.getRequiredScore()).isEqualTo(1500);
    }

    /**
     * 다음 랭크 정책이 없는 최고 티어 사용자는 다음 티어 정보를 빈 값으로 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("최고 티어이면 다음 티어 정보가 null로 반환된다")
    void getMyRankingAtHighestTier() {
        // given
        Stats stats = mock(Stats.class);
        when(stats.getOverallScore()).thenReturn(5000);
        when(stats.getMonthScore()).thenReturn(1000);
        when(stats.getQuarterScore()).thenReturn(2000);
        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(stats));
        given(usersRankingService.getRankingInfo(USERS_ID))
                .willReturn(new UsersRankingInfo(NICKNAME, PROFILE_URL));
        given(rankPolicyService.findNextRankPolicy(5000))
                .willReturn(Optional.empty());

        // when
        MyRankingResponse response = statsService.getMyRanking(USERS_ID);

        // then
        assertThat(response.getNextTier()).isNull();
        assertThat(response.getRequiredScore()).isNull();
    }

    /**
     * 로그인 사용자의 통계가 없으면 내 랭킹 정보 없음 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("내 랭킹 정보를 찾을 수 없으면 예외가 발생한다")
    void getMyRankingNotFound() {
        // given
        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> statsService.getMyRanking(USERS_ID))
                .isInstanceOf(StatsException.class)
                .extracting("errorCode")
                .isEqualTo(MY_RANKING_NOT_FOUND);
        verify(statsRepository, never()).countByOverallScoreGreaterThan(any(Integer.class));
        verify(usersRankingService, never()).getRankingInfo(USERS_ID);
        verify(rankPolicyService, never()).findNextRankPolicy(any(Integer.class));
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

    /**
     * TOTAL 기준 전체 랭킹을 페이지 단위로 조회하는지 검증합니다.
     */
    @Test
    @DisplayName("TOTAL 기준 전체 랭킹을 조회한다")
    void getRankings_Total() {
        // given
        Stats stats1 = mock(Stats.class);
        Stats stats2 = mock(Stats.class);
        Users users1 = mock(Users.class);
        Users users2 = mock(Users.class);

        when(stats1.getUsers()).thenReturn(users1);
        when(stats2.getUsers()).thenReturn(users2);
        when(users1.getUsersId()).thenReturn("user-001");
        when(users2.getUsersId()).thenReturn("user-002");
        when(stats1.getOverallScore()).thenReturn(3200);
        when(stats2.getOverallScore()).thenReturn(2800);
        when(stats1.getStatsLevel()).thenReturn(7);
        when(stats2.getStatsLevel()).thenReturn(6);
        when(stats1.getCurrentTier()).thenReturn("GOLD");
        when(stats2.getCurrentTier()).thenReturn("GOLD");

        Page<Stats> statsPage = new PageImpl<>(List.of(stats1, stats2), PageRequest.of(0, 10), 2);
        given(statsRepository.findAllByOrderByOverallScoreDesc(any(Pageable.class))).willReturn(statsPage);
        given(usersRankingService.getRankingInfo("user-001"))
                .willReturn(new UsersRankingInfo("여행자", "profile1.png"));
        given(usersRankingService.getRankingInfo("user-002"))
                .willReturn(new UsersRankingInfo("모험가", "profile2.png"));

        // when
        RankingListResponse response = statsService.getRankings("TOTAL", 0, 10);

        // then
        assertThat(response.getRankingType()).isEqualTo("TOTAL");
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getRankings()).hasSize(2);
        assertThat(response.getRankings().get(0).getRank()).isEqualTo(1);
        assertThat(response.getRankings().get(0).getNickname()).isEqualTo("여행자");
        assertThat(response.getRankings().get(0).getScore()).isEqualTo(3200);
        assertThat(response.getRankings().get(1).getRank()).isEqualTo(2);
        assertThat(response.getRankings().get(1).getScore()).isEqualTo(2800);
    }

    /**
     * MONTHLY 기준 전체 랭킹을 조회하는지 검증합니다.
     */
    @Test
    @DisplayName("MONTHLY 기준 전체 랭킹을 조회한다")
    void getRankings_Monthly() {
        // given
        Stats stats1 = mock(Stats.class);
        Users users1 = mock(Users.class);

        when(stats1.getUsers()).thenReturn(users1);
        when(users1.getUsersId()).thenReturn("user-001");
        when(stats1.getMonthScore()).thenReturn(520);
        when(stats1.getStatsLevel()).thenReturn(6);
        when(stats1.getCurrentTier()).thenReturn("GOLD");

        Page<Stats> statsPage = new PageImpl<>(List.of(stats1), PageRequest.of(0, 10), 1);
        given(statsRepository.findAllByOrderByMonthScoreDesc(any(Pageable.class))).willReturn(statsPage);
        given(usersRankingService.getRankingInfo("user-001"))
                .willReturn(new UsersRankingInfo("모험가", "profile2.png"));

        // when
        RankingListResponse response = statsService.getRankings("MONTHLY", 0, 10);

        // then
        assertThat(response.getRankingType()).isEqualTo("MONTHLY");
        assertThat(response.getRankings().get(0).getScore()).isEqualTo(520);
    }

    /**
     * 두 번째 페이지 조회 시 순위가 페이지 오프셋을 반영하는지 검증합니다.
     */
    @Test
    @DisplayName("두 번째 페이지 조회 시 순위가 오프셋을 반영한다")
    void getRankings_SecondPage() {
        // given
        Stats stats1 = mock(Stats.class);
        Users users1 = mock(Users.class);

        when(stats1.getUsers()).thenReturn(users1);
        when(users1.getUsersId()).thenReturn("user-011");
        when(stats1.getOverallScore()).thenReturn(500);
        when(stats1.getStatsLevel()).thenReturn(2);
        when(stats1.getCurrentTier()).thenReturn("BRONZE");

        Page<Stats> statsPage = new PageImpl<>(List.of(stats1), PageRequest.of(1, 10), 11);
        given(statsRepository.findAllByOrderByOverallScoreDesc(any(Pageable.class))).willReturn(statsPage);
        given(usersRankingService.getRankingInfo("user-011"))
                .willReturn(new UsersRankingInfo("세계정복", "profile11.png"));

        // when
        RankingListResponse response = statsService.getRankings("TOTAL", 1, 10);

        // then
        assertThat(response.getRankings().get(0).getRank()).isEqualTo(11);
    }

    /**
     * 랭킹 조회 결과가 비어있으면 빈 목록과 페이지 정보를 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("랭킹 조회 결과가 비어있으면 빈 목록을 반환한다")
    void getRankings_Empty() {
        // given
        Page<Stats> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        given(statsRepository.findAllByOrderByOverallScoreDesc(any(Pageable.class))).willReturn(emptyPage);

        // when
        RankingListResponse response = statsService.getRankings("TOTAL", 0, 10);

        // then
        assertThat(response.getRankingType()).isEqualTo("TOTAL");
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getTotalPages()).isZero();
        assertThat(response.getRankings()).isEmpty();
        verify(usersRankingService, never()).getRankingInfo(any(String.class));
    }

    /**
     * 유효하지 않은 rankingType이면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("유효하지 않은 rankingType이면 예외가 발생한다")
    void getRankings_InvalidType() {
        // when
        // then
        assertThatThrownBy(() -> statsService.getRankings("INVALID", 0, 10))
                .isInstanceOf(StatsException.class)
                .extracting("errorCode")
                .isEqualTo(RANKING_NOT_FOUND);
    }

    /**
     * 로그인 사용자의 스탯 정보와 다음 레벨/티어 정보를 조회하는지 검증합니다.
     */
    @Test
    @DisplayName("내 스탯 정보와 다음 레벨/티어 정보를 조회한다")
    void getMyStats() {
        // given
        Stats stats = mock(Stats.class);
        when(stats.getStatsLevel()).thenReturn(3);
        when(stats.getStatsXp()).thenReturn(340);
        when(stats.getCurrentTier()).thenReturn("BRONZE");
        when(stats.getOverallScore()).thenReturn(1250);
        when(stats.getMonthScore()).thenReturn(220);

        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(stats));
        given(levelPolicyService.findNextLevelPolicy(3))
                .willReturn(Optional.of(new LevelPolicyInfo(4, 500)));
        given(rankPolicyService.findNextRankPolicy(1250))
                .willReturn(Optional.of(new RankPolicyInfo("SILVER", 1500)));

        // when
        MyStatsResponse response = statsService.getMyStats(USERS_ID);

        // then
        assertThat(response.getLevel()).isEqualTo(3);
        assertThat(response.getXp()).isEqualTo(340);
        assertThat(response.getCurrentTier()).isEqualTo("BRONZE");
        assertThat(response.getOverallScore()).isEqualTo(1250);
        assertThat(response.getMonthScore()).isEqualTo(220);
        assertThat(response.getNextLevel()).isEqualTo(4);
        assertThat(response.getRequiredXp()).isEqualTo(500);
        assertThat(response.getRemainingXp()).isEqualTo(160);
        assertThat(response.getNextTier()).isEqualTo("SILVER");
        assertThat(response.getRequiredScore()).isEqualTo(1500);
    }

    /**
     * 최고 레벨이면 다음 레벨 관련 필드가 null로 반환되는지 검증합니다.
     */
    @Test
    @DisplayName("최고 레벨이면 다음 레벨 정보가 null로 반환된다")
    void getMyStats_MaxLevel() {
        // given
        Stats stats = mock(Stats.class);
        when(stats.getStatsLevel()).thenReturn(10);
        when(stats.getStatsXp()).thenReturn(4000);
        when(stats.getCurrentTier()).thenReturn("DIAMOND");
        when(stats.getOverallScore()).thenReturn(10000);
        when(stats.getMonthScore()).thenReturn(2000);

        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(stats));
        given(levelPolicyService.findNextLevelPolicy(10)).willReturn(Optional.empty());
        given(rankPolicyService.findNextRankPolicy(10000)).willReturn(Optional.empty());

        // when
        MyStatsResponse response = statsService.getMyStats(USERS_ID);

        // then
        assertThat(response.getNextLevel()).isNull();
        assertThat(response.getRequiredXp()).isNull();
        assertThat(response.getRemainingXp()).isNull();
        assertThat(response.getNextTier()).isNull();
        assertThat(response.getRequiredScore()).isNull();
    }

    /**
     * 스탯 정보가 없으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("내 스탯 정보를 찾을 수 없으면 예외가 발생한다")
    void getMyStats_NotFound() {
        // given
        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> statsService.getMyStats(USERS_ID))
                .isInstanceOf(StatsException.class)
                .extracting("errorCode")
                .isEqualTo(MY_STATS_NOT_FOUND);
    }
}
