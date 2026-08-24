package triplog.backend.home.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.home.dto.response.HomeResponse.HomeInfoResponse;
import triplog.backend.landmark.service.LandmarkHomeCardInfo;
import triplog.backend.landmark.service.UsersCardLandmarkService;
import triplog.backend.mission.service.MissionHomeInfo;
import triplog.backend.mission.service.MissionService;
import triplog.backend.region.service.RegionHomeInfo;
import triplog.backend.region.service.RegionService;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;
import triplog.backend.stats.service.StatsService;
import triplog.backend.users.entity.Users;
import triplog.backend.users.service.UsersService;

import java.util.List;

/**
 * 홈 화면에 필요한 여러 도메인의 조회 흐름을 조합합니다.
 */
@Service
@RequiredArgsConstructor
public class HomeFacadeService {

    private static final int HOME_CARD_LIMIT = 3;
    private static final int HOME_REGION_LIMIT = 3;

    private final UsersService usersService;
    private final StatsService statsService;
    private final MissionService missionService;
    private final UsersCardLandmarkService usersCardLandmarkService;
    private final RegionService regionService;

    /**
     * 로그인 사용자의 홈 화면 정보를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 홈 정보 응답
     */
    @Transactional(readOnly = true)
    public HomeInfoResponse getHomeInfo(String usersId) {
        Users users = usersService.findById(usersId);
        MyStatsResponse stats = statsService.getMyStats(usersId);
        List<MissionHomeInfo> missions = missionService.getHomeMissions();
        List<LandmarkHomeCardInfo> cards =
                usersCardLandmarkService.getRecentObtainedCardInfo(usersId, HOME_CARD_LIMIT);
        List<RegionHomeInfo> regions =
                regionService.getRecentVisitedRegionInfo(usersId, HOME_REGION_LIMIT);

        return HomeInfoResponse.toDto(
                users.getNickname(),
                stats,
                missions,
                cards,
                regions
        );
    }
}
