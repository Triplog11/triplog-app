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
import triplog.backend.stats.entity.UsersRewardLog;
import triplog.backend.stats.exception.StatsException;
import triplog.backend.stats.repository.StatsRepository;
import triplog.backend.stats.repository.UsersRewardLogRepository;
import triplog.backend.activitypolicy.service.ActivityPolicyService;
import triplog.backend.activitypolicy.entity.ActivityPolicy;
import triplog.backend.users.entity.Users;
import triplog.backend.users.exception.UsersException;
import triplog.backend.users.service.UsersRankingInfo;
import triplog.backend.users.service.UsersRankingService;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static triplog.backend.stats.exception.StatsErrorCode.MY_RANKING_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.MY_STATS_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.RANKING_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.STATS_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.ACTIVITY_POLICY_NOT_FOUND;
import static triplog.backend.users.entity.LoginType.GOOGLE;
import static triplog.backend.users.exception.UsersErrorCode.USER_NOT_FOUND;

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
    private UsersRewardLogRepository usersRewardLogRepository;

    @Mock
    private RankPolicyService rankPolicyService;

    @Mock
    private UsersRankingService usersRankingService;

    @Mock
    private LevelPolicyService levelPolicyService;

    @Mock
    private ActivityPolicyService activityPolicyService;

    private StatsServiceImpl statsService;

    @BeforeEach
    void setUp() {
        statsService = new StatsServiceImpl(
                statsRepository,
                usersRewardLogRepository,
                usersRankingService,
                rankPolicyService,
                levelPolicyService,
                activityPolicyService
        );
    }

    @Test
    @DisplayName("월간 Score만 초기화하고 변경된 사용자 수를 반환한다")
    void resetMonthlyScores() {
        // Given
        given(statsRepository.resetMonthlyScores()).willReturn(12);

        // When
        int resetCount = statsService.resetMonthlyScores();

        // Then
        assertThat(resetCount).isEqualTo(12);
        verify(statsRepository).resetMonthlyScores();
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
        RankPolicyInfo nextRankPolicy = new RankPolicyInfo("GOLD", 1500);
        when(stats.getOverallScore()).thenReturn(1250);
        when(stats.getMonthScore()).thenReturn(220);
        when(stats.getStatsLevel()).thenReturn(3);
        when(stats.getCurrentTier()).thenReturn("SILVER");
        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(stats));
        given(statsRepository.findRankingPosition(eq(USERS_ID), eq("TOTAL"), isNull()))
                .willReturn(Optional.of(120L));
        given(statsRepository.findRankingPosition(eq(USERS_ID), eq("MONTHLY"), any(LocalDateTime.class)))
                .willReturn(Optional.of(34L));
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
        assertThat(response.getOverallScore()).isEqualTo(1250);
        assertThat(response.getMonthScore()).isEqualTo(220);
        assertThat(response.getLevel()).isEqualTo(3);
        assertThat(response.getTier()).isEqualTo("SILVER");
        assertThat(response.getNextTier()).isEqualTo("GOLD");
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
        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(stats));
        given(statsRepository.findRankingPosition(eq(USERS_ID), eq("TOTAL"), isNull()))
                .willReturn(Optional.of(1L));
        given(statsRepository.findRankingPosition(eq(USERS_ID), eq("MONTHLY"), any(LocalDateTime.class)))
                .willReturn(Optional.of(1L));
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
        verify(statsRepository, never()).findRankingPosition(
                any(String.class), any(String.class), any(LocalDateTime.class)
        );
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
    @DisplayName("신규 사용자 초기 통계를 생성하고 회원가입 30 XP를 지급한다")
    void createInitialStats() {
        // given
        Users users = mock(Users.class);
        given(users.getUsersId()).willReturn(USERS_ID);
        Stats initialStats = new Stats(users, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU);
        Stats rewardedStats = mock(Stats.class);
        ActivityPolicy signupPolicy = mock(ActivityPolicy.class);
        given(signupPolicy.getActivityPolicyId()).willReturn("SIGNUP_COMPLETE");
        given(signupPolicy.getPolicyXp()).willReturn(30);
        given(signupPolicy.getPolicyScore()).willReturn(0);
        given(statsRepository.save(any(Stats.class))).willReturn(initialStats);
        given(activityPolicyService.findById("SIGNUP_COMPLETE"))
                .willReturn(Optional.of(signupPolicy));
        given(usersRewardLogRepository.insertIfAbsent(
                eq(USERS_ID),
                eq("SIGNUP_COMPLETE"),
                eq("SIGNUP_COMPLETE:USER:" + USERS_ID),
                org.mockito.ArgumentMatchers.isNull(),
                eq("USER"),
                eq(USERS_ID),
                eq(30),
                eq(0),
                any(LocalDateTime.class)
        )).willReturn(1);
        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(rewardedStats));
        given(rewardedStats.getStatsXp()).willReturn(30);
        given(rewardedStats.getOverallScore()).willReturn(0);
        given(levelPolicyService.calculateLevel(30)).willReturn(1);
        given(rankPolicyService.findCurrentRankPolicy(0)).willReturn(new RankPolicyInfo("BRONZE", 0));

        // when
        StatsLoginInfo result = statsService.createInitialStats(
                users, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU
        );

        // then
        assertThat(result.level()).isEqualTo(1);
        assertThat(result.xp()).isEqualTo(30);
        assertThat(result.tier()).isEqualTo("BRONZE");
        verify(statsRepository).addXpAndScore(
                org.mockito.ArgumentMatchers.eq(USERS_ID),
                org.mockito.ArgumentMatchers.eq(30),
                org.mockito.ArgumentMatchers.eq(0),
                any(java.time.LocalDateTime.class)
        );

        ArgumentCaptor<Stats> statsCaptor = ArgumentCaptor.forClass(Stats.class);
        verify(statsRepository).save(statsCaptor.capture());
        Stats savedStats = statsCaptor.getValue();
        assertThat(savedStats.getUsers()).isEqualTo(users);
        assertThat(savedStats.getAddressSi()).isEqualTo(ADDRESS_SI);
        assertThat(savedStats.getAddressDoGun()).isEqualTo(ADDRESS_DO_GUN);
        assertThat(savedStats.getAddressGu()).isEqualTo(ADDRESS_GU);
    }

    /**
     * 필수 회원가입 보상 정책이 없으면 가입 통계 생성을 실패시키는지 검증합니다.
     */
    @Test
    @DisplayName("회원가입 보상 정책이 없으면 초기 통계 생성을 실패한다")
    void createInitialStats_ActivityPolicyNotFound() {
        // given
        Users users = mock(Users.class);
        given(users.getUsersId()).willReturn(USERS_ID);
        given(activityPolicyService.findById("SIGNUP_COMPLETE")).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> statsService.createInitialStats(
                users, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU
        ))
                .isInstanceOf(StatsException.class)
                .extracting("errorCode")
                .isEqualTo(ACTIVITY_POLICY_NOT_FOUND);
        verify(statsRepository, never()).addXpAndScore(
                any(String.class), anyInt(), anyInt(), any(java.time.LocalDateTime.class)
        );
    }

    /**
     * 초기 통계를 생성할 사용자를 찾을 수 없으면 통계를 저장하지 않고 {@link StatsException}이 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("초기 통계 생성 시 사용자가 없으면 예외가 발생한다")
    void createInitialStats_UsersNotFound() {
        // given
        Users users = createUsers();
        ActivityPolicy signupPolicy = mock(ActivityPolicy.class);
        given(signupPolicy.getPolicyXp()).willReturn(30);
        given(signupPolicy.getPolicyScore()).willReturn(0);
        given(activityPolicyService.findById("SIGNUP_COMPLETE"))
                .willReturn(Optional.of(signupPolicy));

        // when
        // then
        assertThatThrownBy(() -> statsService.createInitialStats(
                users, ADDRESS_SI, ADDRESS_DO_GUN, ADDRESS_GU
        ))
                .isInstanceOf(StatsException.class)
                .extracting("errorCode")
                .isEqualTo(STATS_NOT_FOUND);
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
        given(statsRepository.findRankings(
                org.mockito.ArgumentMatchers.eq("TOTAL"), isNull(), any(Pageable.class)
        )).willReturn(statsPage);
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
        given(statsRepository.findRankings(
                org.mockito.ArgumentMatchers.eq("MONTHLY"), any(LocalDateTime.class), any(Pageable.class)
        )).willReturn(statsPage);
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
        given(statsRepository.findRankings(
                org.mockito.ArgumentMatchers.eq("TOTAL"), isNull(), any(Pageable.class)
        )).willReturn(statsPage);
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
        given(statsRepository.findRankings(
                org.mockito.ArgumentMatchers.eq("TOTAL"), isNull(), any(Pageable.class)
        )).willReturn(emptyPage);

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
        when(stats.getStatsXp()).thenReturn(240);
        when(stats.getCurrentTier()).thenReturn("SILVER");
        when(stats.getOverallScore()).thenReturn(1250);
        when(stats.getMonthScore()).thenReturn(220);

        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(stats));
        given(levelPolicyService.findNextLevelPolicy(3))
                .willReturn(Optional.of(new LevelPolicyInfo(4, 300)));
        given(rankPolicyService.findNextRankPolicy(1250))
                .willReturn(Optional.of(new RankPolicyInfo("GOLD", 1500)));

        // when
        MyStatsResponse response = statsService.getMyStats(USERS_ID);

        // then
        assertThat(response.getLevel()).isEqualTo(3);
        assertThat(response.getXp()).isEqualTo(240);
        assertThat(response.getCurrentTier()).isEqualTo("SILVER");
        assertThat(response.getOverallScore()).isEqualTo(1250);
        assertThat(response.getMonthScore()).isEqualTo(220);
        assertThat(response.getNextLevel()).isEqualTo(4);
        assertThat(response.getRequiredXp()).isEqualTo(300);
        assertThat(response.getRemainingXp()).isEqualTo(60);
        assertThat(response.getNextTier()).isEqualTo("GOLD");
        assertThat(response.getRequiredScore()).isEqualTo(1500);
    }

    /**
     * 최고 레벨이면 다음 레벨 관련 필드가 null로 반환되는지 검증합니다.
     */
    @Test
    @DisplayName("101레벨 이후에도 확장 레벨 정책을 반환한다")
    void getMyStats_ExtendedLevel() {
        // given
        Stats stats = mock(Stats.class);
        when(stats.getStatsLevel()).thenReturn(101);
        when(stats.getStatsXp()).thenReturn(35500);
        when(stats.getCurrentTier()).thenReturn("DIAMOND");
        when(stats.getOverallScore()).thenReturn(10000);
        when(stats.getMonthScore()).thenReturn(2000);

        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(stats));
        given(levelPolicyService.findNextLevelPolicy(101))
                .willReturn(Optional.of(new LevelPolicyInfo(102, 36200)));
        given(rankPolicyService.findNextRankPolicy(10000)).willReturn(Optional.empty());

        // when
        MyStatsResponse response = statsService.getMyStats(USERS_ID);

        // then
        assertThat(response.getNextLevel()).isEqualTo(102);
        assertThat(response.getRequiredXp()).isEqualTo(36200);
        assertThat(response.getRemainingXp()).isEqualTo(700);
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

    @Test
    @DisplayName("처음 등록된 이벤트 보상만 지급 원장과 사용자 통계에 반영한다")
    void applyActivityPolicies_GrantsOnlyFirstEvent() {
        // Given
        Stats beforeStats = mock(Stats.class);
        Stats afterStats = mock(Stats.class);
        ActivityPolicy policy = mock(ActivityPolicy.class);
        ActivityRewardGrant grant = new ActivityRewardGrant(
                "LANDMARK_FIRST_VISIT:LANDMARK:301",
                "review-request-1",
                "REVIEW",
                "10",
                "LANDMARK_FIRST_VISIT"
        );
        given(beforeStats.getStatsLevel()).willReturn(1);
        given(beforeStats.getCurrentTier()).willReturn("BRONZE");
        given(afterStats.getStatsXp()).willReturn(50);
        given(afterStats.getOverallScore()).willReturn(30);
        given(afterStats.getStatsLevel()).willReturn(1);
        given(afterStats.getCurrentTier()).willReturn("BRONZE");
        given(statsRepository.findByUsersUsersIdForUpdate(USERS_ID))
                .willReturn(Optional.of(beforeStats));
        given(statsRepository.findByUsersUsersId(USERS_ID))
                .willReturn(Optional.of(afterStats));
        given(activityPolicyService.findAllByIds(List.of("LANDMARK_FIRST_VISIT")))
                .willReturn(List.of(policy));
        given(policy.getActivityPolicyId()).willReturn("LANDMARK_FIRST_VISIT");
        given(policy.getPolicyDescription()).willReturn("랜드마크 최초 방문");
        given(policy.getPolicyXp()).willReturn(50);
        given(policy.getPolicyScore()).willReturn(30);
        given(usersRewardLogRepository.insertIfAbsent(
                eq(USERS_ID),
                eq("LANDMARK_FIRST_VISIT"),
                eq("LANDMARK_FIRST_VISIT:LANDMARK:301"),
                eq("review-request-1"),
                eq("REVIEW"),
                eq("10"),
                eq(50),
                eq(30),
                any(LocalDateTime.class)
        )).willReturn(1);
        given(levelPolicyService.calculateLevel(50)).willReturn(1);
        given(rankPolicyService.findCurrentRankPolicy(30))
                .willReturn(new RankPolicyInfo("BRONZE", 0));

        // When
        ActivityRewardResult result = statsService.applyActivityPolicies(
                USERS_ID, List.of(grant)
        );

        // Then
        assertThat(result.totalXp()).isEqualTo(50);
        assertThat(result.totalScore()).isEqualTo(30);
        assertThat(result.rewards()).singleElement()
                .extracting(ActivityRewardInfo::eventKey)
                .isEqualTo("LANDMARK_FIRST_VISIT:LANDMARK:301");
        verify(statsRepository).addXpAndScore(
                eq(USERS_ID), eq(50), eq(30), any(LocalDateTime.class)
        );
    }

    @Test
    @DisplayName("이미 등록된 이벤트는 XP와 Score를 다시 지급하지 않는다")
    void applyActivityPolicies_DuplicateEventDoesNotGrantReward() {
        // Given
        Stats stats = mock(Stats.class);
        ActivityPolicy policy = mock(ActivityPolicy.class);
        ActivityRewardGrant grant = new ActivityRewardGrant(
                "REGION_CONQUEST:REGION:201",
                "retry-request",
                "REVIEW",
                "11",
                "REGION_CONQUEST"
        );
        given(stats.getStatsLevel()).willReturn(2);
        given(stats.getStatsXp()).willReturn(180);
        given(stats.getOverallScore()).willReturn(200);
        given(stats.getCurrentTier()).willReturn("BRONZE");
        given(statsRepository.findByUsersUsersIdForUpdate(USERS_ID))
                .willReturn(Optional.of(stats));
        given(statsRepository.findByUsersUsersId(USERS_ID)).willReturn(Optional.of(stats));
        given(activityPolicyService.findAllByIds(List.of("REGION_CONQUEST")))
                .willReturn(List.of(policy));
        given(policy.getActivityPolicyId()).willReturn("REGION_CONQUEST");
        given(policy.getPolicyXp()).willReturn(100);
        given(policy.getPolicyScore()).willReturn(100);
        given(usersRewardLogRepository.insertIfAbsent(
                eq(USERS_ID),
                eq("REGION_CONQUEST"),
                eq("REGION_CONQUEST:REGION:201"),
                eq("retry-request"),
                eq("REVIEW"),
                eq("11"),
                eq(100),
                eq(100),
                any(LocalDateTime.class)
        )).willReturn(0);
        given(levelPolicyService.calculateLevel(180)).willReturn(2);
        given(rankPolicyService.findCurrentRankPolicy(200))
                .willReturn(new RankPolicyInfo("BRONZE", 0));

        // When
        ActivityRewardResult result = statsService.applyActivityPolicies(
                USERS_ID, List.of(grant)
        );

        // Then
        assertThat(result.rewards()).isEmpty();
        assertThat(result.totalXp()).isZero();
        assertThat(result.totalScore()).isZero();
        verify(statsRepository, never()).addXpAndScore(
                any(String.class), anyInt(), anyInt(), any(LocalDateTime.class)
        );
    }

    @Test
    @DisplayName("무효 처리된 리뷰의 지급 보상을 한 번만 회수한다")
    void revokeRewards_RevokesGrantedRewards() {
        // Given
        UsersRewardLog firstReward = mock(UsersRewardLog.class);
        UsersRewardLog secondReward = mock(UsersRewardLog.class);
        Stats updatedStats = mock(Stats.class);
        LocalDateTime awardedAt = LocalDateTime.now();
        given(firstReward.getRewardXp()).willReturn(50);
        given(firstReward.getRewardScore()).willReturn(30);
        given(firstReward.getAwardedAt()).willReturn(awardedAt);
        given(secondReward.getRewardXp()).willReturn(15);
        given(secondReward.getRewardScore()).willReturn(0);
        given(secondReward.getAwardedAt()).willReturn(awardedAt);
        given(usersRewardLogRepository.findGrantedBySourceForUpdate(
                USERS_ID, "REVIEW", "10"
        )).willReturn(List.of(firstReward, secondReward));
        given(statsRepository.findByUsersUsersIdForUpdate(USERS_ID))
                .willReturn(Optional.of(updatedStats));
        given(statsRepository.adjustRewards(
                eq(USERS_ID),
                eq(-65),
                eq(-30),
                eq(-30),
                any(LocalDateTime.class)
        )).willReturn(1);
        given(statsRepository.findByUsersUsersId(USERS_ID))
                .willReturn(Optional.of(updatedStats));
        given(updatedStats.getStatsXp()).willReturn(100);
        given(updatedStats.getOverallScore()).willReturn(120);
        given(levelPolicyService.calculateLevel(100)).willReturn(2);
        given(rankPolicyService.findCurrentRankPolicy(120))
                .willReturn(new RankPolicyInfo("BRONZE", 0));

        // When
        RewardRevocationResult result = statsService.revokeRewards(
                USERS_ID, "REVIEW", "10", "위치 인증 무효"
        );

        // Then
        assertThat(result.revokedCount()).isEqualTo(2);
        assertThat(result.revokedXp()).isEqualTo(65);
        assertThat(result.revokedScore()).isEqualTo(30);
        verify(firstReward).revoke(eq("위치 인증 무효"), any(LocalDateTime.class));
        verify(secondReward).revoke(eq("위치 인증 무효"), any(LocalDateTime.class));
        verify(statsRepository).updateGrowth(USERS_ID, 2, "BRONZE");
    }

    @Test
    @DisplayName("직접 XP와 Score를 지급하면 동시 요청을 잠그고 성장 결과를 반환한다")
    void addXpAndScore_ReturnsGrowthUpdate() {
        // Given
        Stats beforeStats = mock(Stats.class);
        Stats updatedStats = mock(Stats.class);
        given(beforeStats.getStatsLevel()).willReturn(1);
        given(beforeStats.getCurrentTier()).willReturn("BRONZE");
        given(updatedStats.getStatsXp()).willReturn(120);
        given(updatedStats.getOverallScore()).willReturn(550);
        given(statsRepository.findByUsersUsersIdForUpdate(USERS_ID))
                .willReturn(Optional.of(beforeStats));
        given(statsRepository.findByUsersUsersId(USERS_ID))
                .willReturn(Optional.of(updatedStats));
        given(levelPolicyService.calculateLevel(120)).willReturn(2);
        given(rankPolicyService.findCurrentRankPolicy(550))
                .willReturn(new RankPolicyInfo("SILVER", 500));

        // When
        GrowthUpdateResult result = statsService.addXpAndScore(USERS_ID, 30, 50);

        // Then
        assertThat(result.currentLevel()).isEqualTo(2);
        assertThat(result.currentTier()).isEqualTo("SILVER");
        assertThat(result.levelUp()).isTrue();
        assertThat(result.rankUp()).isTrue();
        verify(statsRepository).addXpAndScore(
                eq(USERS_ID), eq(30), eq(50), any(LocalDateTime.class)
        );
        verify(statsRepository).updateGrowth(USERS_ID, 2, "SILVER");
    }
}
