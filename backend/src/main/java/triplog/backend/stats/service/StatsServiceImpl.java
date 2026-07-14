package triplog.backend.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import triplog.backend.stats.entity.Stats;
import triplog.backend.stats.exception.StatsException;
import triplog.backend.stats.repository.StatsRepository;
import triplog.backend.users.entity.Users;
import triplog.backend.users.repository.UsersRepository;

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
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final UsersRepository usersRepository;

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
}
