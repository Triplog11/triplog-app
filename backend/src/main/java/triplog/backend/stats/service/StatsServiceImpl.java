package triplog.backend.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.stats.entity.Stats;
import triplog.backend.stats.dto.response.StatsResponse.MyRankingResponse;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;
import triplog.backend.stats.dto.response.StatsResponse.RankingListResponse;
import triplog.backend.stats.dto.response.StatsResponse.RankingEntry;
import triplog.backend.stats.exception.StatsException;
import triplog.backend.stats.repository.StatsRepository;
import triplog.backend.levelpolicy.service.LevelPolicyService;
import triplog.backend.levelpolicy.service.LevelPolicyInfo;
import triplog.backend.rankpolicy.service.RankPolicyService;
import triplog.backend.rankpolicy.service.RankPolicyInfo;
import triplog.backend.users.entity.Users;
import triplog.backend.users.service.UsersRankingInfo;
import triplog.backend.users.service.UsersRankingService;
import triplog.backend.activitypolicy.entity.ActivityPolicy;
import triplog.backend.activitypolicy.service.ActivityPolicyService;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static triplog.backend.stats.exception.StatsErrorCode.PROFILE_UPDATE_TARGET_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.ACTIVITY_POLICY_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.MY_RANKING_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.MY_STATS_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.RANKING_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.STATS_NOT_FOUND;

/**
 * {@link StatsService}의 구현 클래스입니다.
 * <p>
 * 사용자 통계(Stats)와 관련된 비즈니스 로직을 처리하며,
 * Repository를 통해 사용자 통계 데이터를 조회하고 관리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final UsersRankingService usersRankingService;

    private final RankPolicyService rankPolicyService;

    private final LevelPolicyService levelPolicyService;

    private final ActivityPolicyService activityPolicyService;

    /**
     * 로그인 사용자의 통계와 점수 순위 및 다음 티어 정보를 조회합니다.
     * <p>
     * 자신보다 점수가 높은 사용자 수에 1을 더하여 동점자에게 같은 순위를 부여합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 내 랭킹 정보
     * @throws StatsException 사용자 통계 정보를 찾을 수 없는 경우
     */
    @Override
    public MyRankingResponse getMyRanking(String usersId) {
        Stats stats = statsRepository.findByUsersUsersId(usersId)
                .orElseThrow(() -> new StatsException(MY_RANKING_NOT_FOUND));

        int totalRank = Math.toIntExact(
                statsRepository.countByOverallScoreGreaterThan(stats.getOverallScore()) + 1
        );
        int monthlyRank = Math.toIntExact(
                statsRepository.countByMonthScoreGreaterThan(stats.getMonthScore()) + 1
        );
        int quarterRank = Math.toIntExact(
                statsRepository.countByQuarterScoreGreaterThan(stats.getQuarterScore()) + 1
        );
        UsersRankingInfo usersInfo = usersRankingService.getRankingInfo(usersId);
        Optional<RankPolicyInfo> nextRankPolicy =
                rankPolicyService.findNextRankPolicy(stats.getOverallScore());

        return new MyRankingResponse(
                usersInfo.nickname(),
                usersInfo.profileUrl(),
                totalRank,
                monthlyRank,
                quarterRank,
                stats.getOverallScore(),
                stats.getMonthScore(),
                stats.getQuarterScore(),
                stats.getStatsLevel(),
                stats.getCurrentTier(),
                nextRankPolicy.map(RankPolicyInfo::tier).orElse(null),
                nextRankPolicy.map(RankPolicyInfo::requiredScore).orElse(null)
        );
    }

    /**
     * 사용자 ID로 로그인 응답에 필요한 통계 정보를 조회합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 사용자의 레벨, 경험치, 티어 정보
     */
    @Override
    public StatsLoginInfo getLoginStats(String usersId) {
        log.info("로그인 응답용 사용자 통계 조회 시작: usersId={}", usersId);
        Stats stats = statsRepository.findByUsersUsersId(usersId)
                .orElseThrow(() -> new StatsException(STATS_NOT_FOUND));

        return new StatsLoginInfo(
                stats.getStatsLevel(),
                stats.getStatsXp(),
                stats.getCurrentTier()
        );
    }

    /**
     * 신규 사용자의 초기 통계 정보를 생성합니다.
     *
     * @param usersId 통계를 생성할 사용자 ID
     * @param addressSi 시
     * @param addressDoGun 도/군
     * @param addressGu 구
     * @return 생성된 사용자의 초기 레벨, 경험치, 티어 정보
    */
    /**
     * 사용자 엔티티를 기반으로 초기 통계와 회원가입 보상을 생성합니다.
     *
     * @param users 생성 대상 사용자
     * @param addressSi 시 주소
     * @param addressDoGun 도·군 주소
     * @param addressGu 구 주소
     * @return 생성된 초기 통계 정보
     */
    @Override
    @Transactional
    public StatsLoginInfo createInitialStats(Users users, String addressSi, String addressDoGun, String addressGu) {
        String usersId = users.getUsersId();
        log.info("신규 사용자 초기 통계 생성 시작: usersId={}", usersId);
        statsRepository.save(new Stats(users, addressSi, addressDoGun, addressGu));
        ActivityPolicy signupPolicy = activityPolicyService.findById("SIGNUP_COMPLETE")
                .orElseThrow(() -> new StatsException(ACTIVITY_POLICY_NOT_FOUND));
        statsRepository.addXpAndScore(
                usersId,
                signupPolicy.getPolicyXp(),
                signupPolicy.getPolicyScore()
        );
        Stats stats = statsRepository.findByUsersUsersId(usersId)
                .orElseThrow(() -> new StatsException(STATS_NOT_FOUND));
        int level = levelPolicyService.calculateLevel(stats.getStatsXp());
        String tier = rankPolicyService.findCurrentRankPolicy(stats.getOverallScore()).tier();
        statsRepository.updateGrowth(usersId, level, tier);

        return new StatsLoginInfo(
                level,
                stats.getStatsXp(),
                tier
        );
    }
    /**
     * 사용자 주소 프로필 정보를 수정하고 수정 후 주소 요약 정보를 조회합니다.
     * <p>
     * 전달되지 않은 주소 필드는 Repository 수정 쿼리에서 기존 값을 유지합니다.
     *
     * @param usersId 수정할 사용자 ID
     * @param addressSi 변경할 시
     * @param addressDoGun 변경할 도/군
     * @param addressGu 변경할 구
     * @return 수정 후 주소 프로필 요약 정보
     */
    @Override
    @Transactional
    public StatsProfileInfo updateProfileAddress(String usersId, String addressSi, String addressDoGun, String addressGu) {
        int updatedCount = statsRepository.updateProfileAddress(usersId, addressSi, addressDoGun, addressGu);
        if (updatedCount == 0) {
            throw new StatsException(PROFILE_UPDATE_TARGET_NOT_FOUND);
        }

        Stats stats = statsRepository.findByUsersUsersId(usersId)
                .orElseThrow(() -> new StatsException(PROFILE_UPDATE_TARGET_NOT_FOUND));

        return new StatsProfileInfo(
                stats.getAddressSi(),
                stats.getAddressDoGun(),
                stats.getAddressGu()
        );
    }

    /**
     * 전체 랭킹을 페이지 단위로 조회합니다.
     * <p>
     * 랭킹 타입에 따라 누적/월간/분기 점수 기준으로 내림차순 정렬하여 조회합니다.
     * 각 사용자 정보는 UsersRankingService를 통해 가져옵니다.
     *
     * @param rankingType 랭킹 타입 (TOTAL, MONTHLY, QUARTER)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 랭킹 목록 응답
     * @throws StatsException 유효하지 않은 랭킹 타입인 경우
     */
    @Override
    public RankingListResponse getRankings(String rankingType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Stats> statsPage = switch (rankingType) {
            case "TOTAL" -> statsRepository.findAllByOrderByOverallScoreDesc(pageable);
            case "MONTHLY" -> statsRepository.findAllByOrderByMonthScoreDesc(pageable);
            case "QUARTER" -> statsRepository.findAllByOrderByQuarterScoreDesc(pageable);
            default -> throw new StatsException(RANKING_NOT_FOUND);
        };

        List<RankingEntry> rankings = new ArrayList<>();
        List<Stats> statsList = statsPage.getContent();

        for (int i = 0; i < statsList.size(); i++) {
            Stats stat = statsList.get(i);
            String usersId = stat.getUsers().getUsersId();
            UsersRankingInfo usersInfo = usersRankingService.getRankingInfo(usersId);

            int rank = (page * size) + i + 1;
            int score = switch (rankingType) {
                case "TOTAL" -> stat.getOverallScore();
                case "MONTHLY" -> stat.getMonthScore();
                case "QUARTER" -> stat.getQuarterScore();
                default -> 0;
            };

            rankings.add(new RankingEntry(
                    rank,
                    usersId,
                    usersInfo.nickname(),
                    usersInfo.profileUrl(),
                    score,
                    stat.getStatsLevel(),
                    stat.getCurrentTier()
            ));
        }

        return new RankingListResponse(
                rankingType,
                page,
                size,
                statsPage.getTotalElements(),
                statsPage.getTotalPages(),
                rankings
        );
    }

    /**
     * 로그인 사용자의 스탯 정보를 조회합니다.
     * <p>
     * 현재 레벨과 경험치를 기반으로 다음 레벨 정보와 남은 경험치를 계산하고,
     * 다음 티어 정보도 함께 반환합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 내 스탯 정보
     * @throws StatsException 사용자 통계 정보를 찾을 수 없는 경우
     */
    @Override
    public MyStatsResponse getMyStats(String usersId) {
        Stats stats = statsRepository.findByUsersUsersId(usersId)
                .orElseThrow(() -> new StatsException(MY_STATS_NOT_FOUND));

        Optional<LevelPolicyInfo> nextLevelPolicy =
                levelPolicyService.findNextLevelPolicy(stats.getStatsLevel());

        Optional<RankPolicyInfo> nextRankPolicy =
                rankPolicyService.findNextRankPolicy(stats.getOverallScore());

        Integer remainingXp = nextLevelPolicy
                .map(info -> info.requiredXp() - stats.getStatsXp())
                .orElse(null);

        return new MyStatsResponse(
                stats.getStatsLevel(),
                stats.getStatsXp(),
                stats.getCurrentTier(),
                stats.getOverallScore(),
                stats.getMonthScore(),
                nextLevelPolicy.map(LevelPolicyInfo::nextLevel).orElse(null),
                nextLevelPolicy.map(LevelPolicyInfo::requiredXp).orElse(null),
                remainingXp,
                nextRankPolicy.map(RankPolicyInfo::tier).orElse(null),
                nextRankPolicy.map(RankPolicyInfo::requiredScore).orElse(null)
        );
    }

    /**
     * 사용자에게 XP와 Score를 지급합니다.
     *
     * @param usersId 사용자 ID
     * @param xp      추가할 경험치
     * @param score   추가할 점수
     */
    @Override
    @Transactional
    public void addXpAndScore(String usersId, int xp, int score) {
        statsRepository.addXpAndScore(usersId, xp, score);
        Stats stats = statsRepository.findByUsersUsersId(usersId)
                .orElseThrow(() -> new StatsException(STATS_NOT_FOUND));
        int level = levelPolicyService.calculateLevel(stats.getStatsXp());
        String tier = rankPolicyService.findCurrentRankPolicy(stats.getOverallScore()).tier();
        statsRepository.updateGrowth(usersId, level, tier);
    }

    /**
     * 활동 정책을 조회하여 보상을 합산하고 사용자의 성장 정보를 갱신합니다.
     *
     * @param usersId  사용자 식별자
     * @param policyIds 적용할 활동 정책 식별자 목록
     * @return 정책별 보상과 지급 후 성장 정보
     */
    @Override
    @Transactional
    public ActivityRewardResult applyActivityPolicies(String usersId, String... policyIds) {
        Stats stats = statsRepository.findByUsersUsersId(usersId)
                .orElseThrow(() -> new StatsException(STATS_NOT_FOUND));

        Map<String, ActivityPolicy> policiesById = new LinkedHashMap<>();
        activityPolicyService.findAllByIds(List.of(policyIds))
                .forEach(policy -> policiesById.put(policy.getActivityPolicyId(), policy));

        List<ActivityRewardInfo> rewards = new ArrayList<>();
        int totalXp = 0;
        int totalScore = 0;
        for (String policyId : policyIds) {
            ActivityPolicy policy = Optional.ofNullable(policiesById.get(policyId))
                    .orElseThrow(() -> new IllegalStateException("Activity policy not found: " + policyId));
            rewards.add(new ActivityRewardInfo(
                    policy.getActivityPolicyId(),
                    policy.getPolicyDescription(),
                    policy.getPolicyXp(),
                    policy.getPolicyScore()
            ));
            totalXp += policy.getPolicyXp();
            totalScore += policy.getPolicyScore();
        }

        int previousLevel = stats.getStatsLevel();
        String previousTier = stats.getCurrentTier();
        statsRepository.addXpAndScore(usersId, totalXp, totalScore);
        Stats updatedStats = statsRepository.findByUsersUsersId(usersId)
                .orElseThrow(() -> new StatsException(STATS_NOT_FOUND));
        int currentLevel = levelPolicyService.calculateLevel(updatedStats.getStatsXp());
        String currentTier = rankPolicyService.findCurrentRankPolicy(updatedStats.getOverallScore()).tier();
        statsRepository.updateGrowth(usersId, currentLevel, currentTier);

        return new ActivityRewardResult(
                List.copyOf(rewards),
                totalXp,
                totalScore,
                currentLevel,
                currentTier,
                currentLevel > previousLevel,
                !currentTier.equalsIgnoreCase(previousTier)
        );
    }

}
