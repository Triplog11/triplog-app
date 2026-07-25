package triplog.backend.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.stats.entity.Stats;
import triplog.backend.stats.dto.response.StatsResponse.MyRankingResponse;
import triplog.backend.stats.exception.StatsException;
import triplog.backend.stats.repository.StatsRepository;
import triplog.backend.rankpolicy.service.RankPolicyService;
import triplog.backend.rankpolicy.service.RankPolicyInfo;
import triplog.backend.users.entity.Users;
import triplog.backend.users.repository.UsersRepository;
import triplog.backend.users.service.UsersRankingInfo;
import triplog.backend.users.service.UsersRankingService;

import java.util.Optional;

import static triplog.backend.stats.exception.StatsErrorCode.PROFILE_UPDATE_TARGET_NOT_FOUND;
import static triplog.backend.stats.exception.StatsErrorCode.MY_RANKING_NOT_FOUND;
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
    private final UsersRepository usersRepository;

    private final UsersRankingService usersRankingService;

    private final RankPolicyService rankPolicyService;

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
        UsersRankingInfo usersInfo = usersRankingService.getRankingInfo(usersId);
        Optional<RankPolicyInfo> nextRankPolicy =
                rankPolicyService.findNextRankPolicy(stats.getOverallScore());

        return new MyRankingResponse(
                usersInfo.nickname(),
                usersInfo.profileUrl(),
                totalRank,
                monthlyRank,
                stats.getOverallScore(),
                stats.getMonthScore(),
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
    @Override
    @Transactional
    public StatsLoginInfo createInitialStats(String usersId, String addressSi, String addressDoGun, String addressGu) {
        log.info("신규 사용자 초기 통계 생성 시작: usersId={}", usersId);
        Users users = usersRepository.findById(usersId)
                .orElseThrow(() -> new StatsException(STATS_NOT_FOUND));
        Stats stats = statsRepository.save(new Stats(users, addressSi, addressDoGun, addressGu));

        return new StatsLoginInfo(
                stats.getStatsLevel(),
                stats.getStatsXp(),
                stats.getCurrentTier()
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
}
