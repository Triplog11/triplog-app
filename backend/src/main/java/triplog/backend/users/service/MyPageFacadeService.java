package triplog.backend.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.badge.service.BadgeService;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.service.RegionService;
import triplog.backend.review.service.ReviewService;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;
import triplog.backend.stats.service.StatsService;
import triplog.backend.users.dto.response.UsersResponse.MyPageInfoResponse;
import triplog.backend.users.entity.Users;

/**
 * 마이페이지에 필요한 여러 도메인의 조회 흐름을 조합합니다.
 */
@Service
@RequiredArgsConstructor
public class MyPageFacadeService {

    private final UsersService usersService;
    private final StatsService statsService;
    private final ReviewService reviewService;
    private final RegionService regionService;
    private final BadgeService badgeService;
    private final LandmarkService landmarkService;

    /**
     * 로그인 사용자의 프로필과 활동 요약 정보를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 마이페이지 정보 응답
     */
    @Transactional(readOnly = true)
    public MyPageInfoResponse getMyPageInfo(String usersId) {
        Users users = usersService.findById(usersId);
        MyStatsResponse stats = statsService.getMyStats(usersId);
        int totalCertificationCount = reviewService.countCertifications(usersId);
        int visitedRegionCount = regionService.countVisitedRegions(usersId);
        int acquiredBadgeCount = badgeService.countAcquiredBadges(usersId);
        int collectedCardCount = landmarkService.countCollectedCards(usersId);

        return MyPageInfoResponse.toDto(
                users,
                stats,
                totalCertificationCount,
                visitedRegionCount,
                acquiredBadgeCount,
                collectedCardCount
        );
    }
}
