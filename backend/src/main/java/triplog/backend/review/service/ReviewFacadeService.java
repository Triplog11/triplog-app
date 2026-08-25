package triplog.backend.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.attraction.entity.Attraction;
import triplog.backend.attraction.service.AttractionService;
import triplog.backend.attractionvisitlog.service.AttractionVisitLogService;
import triplog.backend.badge.service.AcquiredBadgeInfo;
import triplog.backend.badge.service.BadgeService;
import triplog.backend.achievement.service.AchievementContext;
import triplog.backend.appellation.service.AcquiredAppellationInfo;
import triplog.backend.appellation.service.AppellationService;
import triplog.backend.image.service.ImageService;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.landmark.service.UsersCardLandmarkService;
import triplog.backend.mission.service.MissionAchievementService;
import triplog.backend.mission.service.MissionCompletionInfo;
import triplog.backend.notification.service.NotificationEvent;
import triplog.backend.notification.service.NotificationService;
import triplog.backend.region.entity.Region;
import triplog.backend.region.service.RegionService;
import triplog.backend.review.dto.request.ReviewRequest.CreateRequest;
import triplog.backend.review.dto.response.ReviewResponse.CreateReviewResponse;
import triplog.backend.review.entity.Review;
import triplog.backend.review.exception.ReviewErrorCode;
import triplog.backend.review.exception.ReviewException;
import triplog.backend.reviewlog.service.ReviewLogService;
import triplog.backend.stats.service.ActivityRewardInfo;
import triplog.backend.stats.service.ActivityRewardGrant;
import triplog.backend.stats.service.ActivityRewardResult;
import triplog.backend.stats.service.StatsService;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.service.TourismContentService;
import triplog.backend.users.service.ActivityHistoryRecord;
import triplog.backend.users.service.ActivityHistoryService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.Map;

import static triplog.backend.review.exception.ReviewErrorCode.TOURISM_CONTENT_NOT_FOUND;
import static triplog.backend.review.exception.ReviewErrorCode.UNSUPPORTED_VISIT_CONTENT;

/**
 * 방문 인증 생성과 방문·보상·미션·활동 히스토리 흐름을 조합합니다.
 */
@Service
@RequiredArgsConstructor
public class ReviewFacadeService {

    private static final String REVIEW_SOURCE_TYPE = "REVIEW";
    private static final int LANDMARK_DISPLAY_ORDER = 10;
    private static final int REGION_DISPLAY_ORDER = 20;
    private static final int REGION_CONQUEST_DISPLAY_ORDER = 25;
    private static final int CARD_DISPLAY_ORDER = 30;
    private static final int BADGE_DISPLAY_ORDER = 40;
    private static final int APPELLATION_DISPLAY_ORDER = 45;
    private static final int MISSION_DISPLAY_ORDER = 50;
    private static final int LEVEL_DISPLAY_ORDER = 90;
    private static final int RANK_DISPLAY_ORDER = 95;

    private final ReviewService reviewService;
    private final LandmarkService landmarkService;
    private final UsersCardLandmarkService usersCardLandmarkService;
    private final ImageService imageService;
    private final StatsService statsService;
    private final ReviewLogService reviewLogService;
    private final RegionService regionService;
    private final TourismContentService tourismContentService;
    private final AttractionService attractionService;
    private final AttractionVisitLogService attractionVisitLogService;
    private final MissionAchievementService missionAchievementService;
    private final ActivityHistoryService activityHistoryService;
    private final BadgeService badgeService;
    private final AppellationService appellationService;
    private final NotificationService notificationService;

    /**
     * 방문 인증을 생성하고 관련 방문·보상·미션·활동 히스토리를 함께 처리합니다.
     *
     * @param usersId 사용자 식별자
     * @param request 방문 인증 요청
     * @param files 첨부 이미지 목록
     * @param idempotencyKey 네트워크 재시도 시 동일하게 전달할 요청 키
     * @return 방문 인증 결과
     */
    @Transactional
    public CreateReviewResponse createReview(
            String usersId,
            CreateRequest request,
            List<MultipartFile> files,
            String idempotencyKey
    ) {
        String requestKey = validateIdempotencyKey(idempotencyKey);
        TourismContent content = tourismContentService.findOptionalById(request.getTourismContentId())
                .orElseThrow(() -> new ReviewException(TOURISM_CONTENT_NOT_FOUND));
        Optional<Landmark> landmark = landmarkService.findByTourismContentId(content.getTourismContentId());
        Optional<Attraction> attraction = attractionService.findByTourismContentId(
                content.getTourismContentId()
        );
        if (landmark.isEmpty() && attraction.isEmpty()) {
            throw new ReviewException(UNSUPPORTED_VISIT_CONTENT);
        }

        Region region = content.getRegion();
        validateRegionCodes(request, region);

        boolean firstVisit = landmark
                .map(value -> !landmarkService.hasVisited(usersId, value.getLandmarkId()))
                .orElseGet(() -> !attractionVisitLogService.hasVisited(
                        usersId, attraction.orElseThrow().getAttractionId()
                ));
        boolean firstRegionVisit = !regionService.hasVisited(usersId, region.getRegionId());
        Review review = reviewService.createReview(usersId, request, content);

        boolean hasImages = files != null && !files.isEmpty();
        if (hasImages) {
            imageService.uploadAndSave(review.getReviewId(), files);
        }

        VisitResult visitResult = recordVisits(
                usersId, landmark, attraction, region.getRegionId()
        );
        AchievementContext achievementContext = createAchievementContext(
                usersId,
                landmark,
                attraction,
                true,
                hasImages
        );
        List<AcquiredBadgeInfo> acquiredBadges = badgeService.acquireEligibleBadges(
                usersId, achievementContext
        );
        List<AcquiredAppellationInfo> acquiredAppellations =
                appellationService.acquireEligibleAppellations(
                        usersId, achievementContext
        );
        List<ActivityRewardGrant> rewardGrants = determineRewardGrants(
                usersId,
                review.getReviewId(),
                requestKey,
                landmark,
                attraction,
                region,
                firstVisit,
                firstRegionVisit,
                visitResult.regionConquered(),
                acquiredBadges,
                acquiredAppellations,
                hasImages
        );

        ActivityRewardResult reward = statsService.applyActivityPolicies(
                usersId, rewardGrants
        );
        reviewService.updateRewardScore(review.getReviewId(), reward.totalScore());
        int reviewXp = reward.rewards().stream()
                .filter(item -> item.policyId().startsWith("REVIEW_"))
                .mapToInt(ActivityRewardInfo::xp)
                .sum();
        reviewLogService.createLog(
                review.getReviewId(), content.getTitle() + " 방문 인증", reviewXp
        );

        List<MissionCompletionInfo> missionCompletions = evaluateMissions(
                usersId, landmark.isPresent()
        );
        recordActivityHistory(
                usersId,
                review.getReviewId(),
                content,
                landmark,
                attraction,
                region,
                firstVisit,
                firstRegionVisit,
                visitResult,
                acquiredBadges,
                acquiredAppellations,
                reward,
                missionCompletions
        );
        createNotifications(
                usersId,
                review.getReviewId(),
                content,
                landmark,
                region,
                firstVisit,
                firstRegionVisit,
                visitResult,
                acquiredBadges,
                acquiredAppellations,
                reward,
                missionCompletions
        );

        return CreateReviewResponse.toDto(
                reward.rewards(), reward.totalXp(), reward.totalScore()
        );
    }

    /**
     * 방문 인증 처리에서 실제로 발생한 결과를 알림 이벤트로 변환하여 저장합니다.
     *
     * @param usersId 알림을 받을 사용자 식별자
     * @param reviewId 방문 인증 식별자
     * @param content 방문한 관광 콘텐츠
     * @param landmark 방문한 랜드마크
     * @param region 방문 지역
     * @param firstVisit 관광 콘텐츠 최초 방문 여부
     * @param firstRegionVisit 신규 지역 방문 여부
     * @param visitResult 카드 획득 및 지역 정복 결과
     * @param acquiredBadges 이번에 획득한 뱃지 목록
     * @param acquiredAppellations 이번에 획득한 칭호 목록
     * @param reward 활동 정책 적용 결과
     * @param missionCompletions 이번에 완료한 미션 목록
     */
    private void createNotifications(
            String usersId,
            Long reviewId,
            TourismContent content,
            Optional<Landmark> landmark,
            Region region,
            boolean firstVisit,
            boolean firstRegionVisit,
            VisitResult visitResult,
            List<AcquiredBadgeInfo> acquiredBadges,
            List<AcquiredAppellationInfo> acquiredAppellations,
            ActivityRewardResult reward,
            List<MissionCompletionInfo> missionCompletions
    ) {
        List<NotificationEvent> events = new ArrayList<>();
        if (firstVisit) {
            events.add(new NotificationEvent(
                    "VISIT_VERIFICATION_SUCCEEDED",
                    reviewId,
                    "REVIEW",
                    Map.of(
                            "placeName", content.getTitle(),
                            "xp", reward.totalXp(),
                            "score", reward.totalScore()
                    )
            ));
        }

        if (firstRegionVisit) {
            findReward(reward, regionVisitRewardKey(region.getRegionId()))
                    .ifPresent(regionReward -> events.add(new NotificationEvent(
                            "REGION_FIRST_VISITED",
                            region.getRegionId(),
                            "REGION",
                            Map.of(
                                    "regionName", region.getRegionName(),
                                    "xp", regionReward.xp(),
                                    "score", regionReward.score()
                            )
                    )));
        }
        if (visitResult.regionConquered()) {
            findReward(reward, regionConquestRewardKey(region.getRegionId()))
                    .ifPresent(regionReward -> events.add(new NotificationEvent(
                            "REGION_CONQUERED",
                            region.getRegionId(),
                            "REGION",
                            Map.of(
                                    "regionName", region.getRegionName(),
                                    "xp", regionReward.xp(),
                                    "score", regionReward.score()
                            )
                    )));
        }
        if (landmark.isPresent() && visitResult.cardAcquired()) {
            events.add(new NotificationEvent(
                    "LANDMARK_CARD_ACQUIRED",
                    landmark.get().getLandmarkId(),
                    "CARD",
                    Map.of("cardName", landmarkName(landmark.get(), content))
            ));
        }
        for (AcquiredBadgeInfo badge : acquiredBadges) {
            findReward(reward, badgeRewardKey(badge.badgeId()))
                    .ifPresent(badgeReward -> events.add(new NotificationEvent(
                            "BADGE_ACQUIRED",
                            badge.badgeId(),
                            "BADGE",
                            Map.of("badgeName", badge.badgeName(), "xp", badgeReward.xp())
                    )));
        }
        for (AcquiredAppellationInfo appellation : acquiredAppellations) {
            findReward(reward, appellationRewardKey(appellation.appellationId()))
                    .ifPresent(appellationReward -> events.add(new NotificationEvent(
                            "APPELLATION_ACQUIRED",
                            appellation.appellationId(),
                            "APPELLATION",
                            Map.of(
                                    "appellationName", appellation.appellationName(),
                                    "xp", appellationReward.xp()
                            )
                    )));
        }
        for (MissionCompletionInfo mission : missionCompletions) {
            events.add(new NotificationEvent(
                    "WEEKLY_MISSION_COMPLETED",
                    mission.missionId(),
                    "MISSION",
                    Map.of("missionName", mission.missionName(), "xp", mission.xp())
            ));
        }

        boolean missionLevelUp = missionCompletions.stream()
                .anyMatch(completion -> completion.growth().levelUp());
        boolean missionRankUp = missionCompletions.stream()
                .anyMatch(completion -> completion.growth().rankUp());
        int currentLevel = missionCompletions.isEmpty()
                ? reward.currentLevel()
                : missionCompletions.getLast().growth().currentLevel();
        String currentTier = missionCompletions.isEmpty()
                ? reward.currentTier()
                : missionCompletions.getLast().growth().currentTier();
        if (reward.levelUp() || missionLevelUp) {
            events.add(new NotificationEvent(
                    "USER_LEVEL_UP", reviewId, "STATS", Map.of("level", currentLevel)
            ));
        }
        if (reward.rankUp() || missionRankUp) {
            events.add(new NotificationEvent(
                    "USER_RANK_UP", reviewId, "STATS", Map.of("rank", currentTier)
            ));
        }

        notificationService.createNotifications(usersId, events);
    }

    /**
     * 네트워크 재시도 중복 보상 방지에 사용할 요청 키를 정규화합니다.
     *
     * @param idempotencyKey 클라이언트가 전달한 멱등성 키
     * @return 앞뒤 공백을 제거한 요청 키
     * @throws ReviewException 키가 비어 있거나 최대 길이를 초과한 경우
     */
    private String validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new ReviewException(ReviewErrorCode.INVALID_IDEMPOTENCY_KEY);
        }
        String normalized = idempotencyKey.trim();
        if (normalized.length() > 100) {
            throw new ReviewException(ReviewErrorCode.INVALID_IDEMPOTENCY_KEY);
        }
        return normalized;
    }

    /**
     * 방문 인증 요청의 법정동 코드가 관광 콘텐츠에 연결된 지역 코드와 일치하는지 검증합니다.
     *
     * @param request 법정동 코드를 포함한 방문 인증 요청
     * @param region 관광 콘텐츠에 연결된 지역
     * @throws ReviewException 시도 코드 또는 시군구 코드가 일치하지 않는 경우
     */
    private void validateRegionCodes(CreateRequest request, Region region) {
        if (!region.getLegalRegionCode().equals(request.getLegalRegionCode())
                || !region.getLegalDistrictCode().equals(request.getLegalDistrictCode())) {
            throw new ReviewException(ReviewErrorCode.REGION_CODE_MISMATCH);
        }
    }

    /**
     * 콘텐츠와 지역 방문을 기록하고 카드 획득·지역 정복 여부를 반환합니다.
     *
     * @param usersId 사용자 식별자
     * @param landmark 방문한 랜드마크
     * @param attraction 방문한 일반 관광지
     * @param regionId 방문한 지역 식별자
     * @return 카드 획득과 지역 정복 처리 결과
     */
    private VisitResult recordVisits(
            String usersId,
            Optional<Landmark> landmark,
            Optional<Attraction> attraction,
            Long regionId
    ) {
        boolean cardAcquired = false;
        boolean regionConquered = false;
        if (landmark.isPresent()) {
            Long landmarkId = landmark.get().getLandmarkId();
            landmarkService.saveVisitLog(usersId, landmarkId);
            cardAcquired = usersCardLandmarkService.acquireCard(usersId, landmarkId);
        } else {
            attractionVisitLogService.createLog(
                    usersId, attraction.orElseThrow().getAttractionId()
            );
        }
        regionService.recordRegionVisit(usersId, regionId);
        if (landmark.isPresent()) {
            long totalLandmarkCount = landmarkService.countLandmarksByRegion(regionId);
            long visitedLandmarkCount = landmarkService
                    .countVisitedLandmarksByRegionAndUser(usersId, regionId);
            regionConquered = regionService.conquerIfEligible(
                    usersId,
                    regionId,
                    totalLandmarkCount,
                    visitedLandmarkCount
            );
        }
        return new VisitResult(cardAcquired, regionConquered);
    }

    /**
     * 뱃지와 칭호 판정에 사용할 최신 사용자 성취 지표를 구성합니다.
     *
     * @param usersId 사용자 식별자
     * @param landmark 현재 방문 랜드마크
     * @param attraction 현재 방문 일반 관광지
     * @param currentReviewIsTravelRecord 현재 인증의 여행 기록 여부
     * @param currentReviewHasImages 현재 인증의 이미지 포함 여부
     * @return 조건 코드별 지표를 담은 성취 컨텍스트
     */
    private AchievementContext createAchievementContext(
            String usersId,
            Optional<Landmark> landmark,
            Optional<Attraction> attraction,
            boolean currentReviewIsTravelRecord,
            boolean currentReviewHasImages
    ) {
        long collectedCardCount = usersCardLandmarkService.countCollectedCards(usersId);
        long totalLandmarkCount = landmarkService.countLandmarks();
        long completionRate = totalLandmarkCount == 0
                ? 0
                : collectedCardCount * 100 / totalLandmarkCount;

        Map<String, Long> metrics = new LinkedHashMap<>();
        metrics.put("VISIT_COUNT", (long) reviewService.countUniqueVisitedContents(usersId));
        metrics.put("LANDMARK_COUNT", collectedCardCount);
        metrics.put("CARD_COUNT", collectedCardCount);
        metrics.put("REGION_VISIT_COUNT", (long) regionService.countVisitedRegions(usersId));
        metrics.put("REGION_CONQUEST_COUNT", (long) regionService.countConqueredRegions(usersId));
        metrics.put(
                "REVIEW_COUNT",
                reviewLogService.countTravelRecords(usersId)
                        + (currentReviewIsTravelRecord ? 1 : 0)
        );
        metrics.put("LANDMARK_COMPLETION_RATE", completionRate);
        metrics.put("CARD_COMPLETION_RATE", completionRate);
        long currentContentVisitDateCount = landmark
                .map(value -> landmarkService.countDistinctVisitDates(
                        usersId, value.getLandmarkId()
                ))
                .orElseGet(() -> attractionVisitLogService.countDistinctVisitDates(
                        usersId, attraction.orElseThrow().getAttractionId()
                ));
        metrics.put("CONTENT_DISTINCT_VISIT_DATE_COUNT", currentContentVisitDateCount);
        metrics.put(
                "NEW_REGION_STREAK",
                (long) regionService.countConsecutiveNewRegionVisits(usersId)
        );
        metrics.put(
                "WEEKEND_VISIT_COUNT",
                landmarkService.countWeekendVisits(usersId)
                        + attractionVisitLogService.countWeekendVisits(usersId)
        );
        metrics.put(
                "PHOTO_REVIEW_COUNT",
                reviewLogService.countPhotoTravelRecords(usersId)
                        + (currentReviewIsTravelRecord && currentReviewHasImages ? 1 : 0)
        );

        long visitedProvinceCount = reviewService.countVisitedProvinces(usersId);
        long totalProvinceCount = regionService.countProvinces();
        metrics.put("PROVINCE_VISIT_COUNT", visitedProvinceCount);
        metrics.put(
                "PROVINCE_COMPLETION_RATE",
                totalProvinceCount == 0 ? 0 : visitedProvinceCount * 100 / totalProvinceCount
        );

        Map<String, Long> totalLandmarksByProvince = landmarkService.countLandmarksByProvince();
        Map<String, Long> visitedLandmarksByProvince = landmarkService
                .countVisitedLandmarksByProvinceAndUser(usersId);
        totalLandmarksByProvince.forEach((provinceCode, totalCount) -> {
            long visitedCount = visitedLandmarksByProvince.getOrDefault(provinceCode, 0L);
            long provinceCompletionRate = totalCount == 0
                    ? 0
                    : visitedCount * 100 / totalCount;
            metrics.put(
                    "PROVINCE_LANDMARK_RATE_" + provinceCode,
                    provinceCompletionRate
            );
        });
        return new AchievementContext(metrics);
    }

    /**
     * 이번 방문 인증에서 최초로 지급할 수 있는 보상 요청 목록을 구성합니다.
     *
     * @param usersId 사용자 식별자
     * @param reviewId 방문 인증 식별자
     * @param landmark 현재 방문 랜드마크
     * @param attraction 현재 방문 일반 관광지
     * @param region 현재 방문 지역
     * @param firstVisit 콘텐츠 최초 방문 여부
     * @param firstRegionVisit 지역 최초 방문 여부
     * @param regionConquered 지역 최초 정복 여부
     * @param acquiredBadges 이번에 획득한 뱃지 목록
     * @param acquiredAppellations 이번에 획득한 칭호 목록
     * @param hasImages 이미지 포함 여부
     * @return 정책 적용을 요청할 보상 목록
     */
    private List<ActivityRewardGrant> determineRewardGrants(
            String usersId,
            Long reviewId,
            String requestKey,
            Optional<Landmark> landmark,
            Optional<Attraction> attraction,
            Region region,
            boolean firstVisit,
            boolean firstRegionVisit,
            boolean regionConquered,
            List<AcquiredBadgeInfo> acquiredBadges,
            List<AcquiredAppellationInfo> acquiredAppellations,
            boolean hasImages
    ) {
        List<ActivityRewardGrant> grants = new ArrayList<>();
        String sourceId = reviewId.toString();
        if (firstVisit) {
            if (landmark.isPresent()) {
                grants.add(rewardGrant(
                        landmarkRewardKey(landmark.get().getLandmarkId()),
                        requestKey,
                        sourceId,
                        "LANDMARK_FIRST_VISIT"
                ));
            } else {
                grants.add(rewardGrant(
                        attractionRewardKey(attraction.orElseThrow().getAttractionId()),
                        requestKey,
                        sourceId,
                        "ATTRACTION_FIRST_VISIT"
                ));
            }
        }
        if (firstRegionVisit) {
            grants.add(rewardGrant(
                    regionVisitRewardKey(region.getRegionId()),
                    requestKey,
                    sourceId,
                    "REGION_FIRST_VISIT"
            ));
        }
        if (regionConquered) {
            grants.add(rewardGrant(
                    regionConquestRewardKey(region.getRegionId()),
                    requestKey,
                    sourceId,
                    "REGION_CONQUEST"
            ));
        }
        for (AcquiredBadgeInfo acquiredBadge : acquiredBadges) {
            grants.add(rewardGrant(
                    badgeRewardKey(acquiredBadge.badgeId()),
                    requestKey,
                    sourceId,
                    "BADGE_ACQUIRED"
            ));
        }
        for (AcquiredAppellationInfo acquiredAppellation : acquiredAppellations) {
            grants.add(rewardGrant(
                    appellationRewardKey(acquiredAppellation.appellationId()),
                    requestKey,
                    sourceId,
                    "APPELLATION_ACQUIRED"
            ));
        }
        // 여행 기록과 사진 보너스는 하루 세 번째 보상 대상 기록까지만 함께 지급합니다.
        if (reviewLogService.countRewardedTravelRecordsToday(usersId) < 3) {
            grants.add(rewardGrant(
                    reviewRewardKey("REVIEW_CREATE", requestKey),
                    requestKey,
                    sourceId,
                    "REVIEW_CREATE"
            ));
            if (hasImages) {
                grants.add(rewardGrant(
                        reviewRewardKey("REVIEW_IMAGE_BONUS", requestKey),
                        requestKey,
                        sourceId,
                        "REVIEW_IMAGE_BONUS"
                ));
            }
        }
        return grants;
    }

    /**
     * 리뷰 흐름에서 사용할 보상 요청을 생성합니다.
     *
     * @param eventKey 보상 중복 방지 이벤트 키
     * @param requestKey 네트워크 재시도 방지 키
     * @param sourceId 원본 리뷰 식별자
     * @param policyId 적용할 보상 정책 식별자
     * @return 보상 요청
     */
    private ActivityRewardGrant rewardGrant(
            String eventKey, String requestKey, String sourceId, String policyId
    ) {
        return new ActivityRewardGrant(
                eventKey, requestKey, REVIEW_SOURCE_TYPE, sourceId, policyId
        );
    }

    /**
     * 한 방문 인증에서 발생한 활동을 동일 시각과 고정된 표시 순서로 기록합니다.
     *
     * @param usersId 사용자 식별자
     * @param reviewId 방문 인증 식별자
     * @param content 방문 관광 콘텐츠
     * @param landmark 방문 랜드마크
     * @param attraction 방문 일반 관광지
     * @param region 방문 지역
     * @param firstVisit 콘텐츠 최초 방문 여부
     * @param firstRegionVisit 지역 최초 방문 여부
     * @param visitResult 카드 획득과 지역 정복 결과
     * @param acquiredBadges 이번에 획득한 뱃지 목록
     * @param acquiredAppellations 이번에 획득한 칭호 목록
     * @param reward 지급된 활동 보상 결과
     * @param missionCompletions 이번에 완료한 미션 목록
     */
    private void recordActivityHistory(
            String usersId,
            Long reviewId,
            TourismContent content,
            Optional<Landmark> landmark,
            Optional<Attraction> attraction,
            Region region,
            boolean firstVisit,
            boolean firstRegionVisit,
            VisitResult visitResult,
            List<AcquiredBadgeInfo> acquiredBadges,
            List<AcquiredAppellationInfo> acquiredAppellations,
            ActivityRewardResult reward,
            List<MissionCompletionInfo> missionCompletions
    ) {
        // 한 인증에서 발생한 활동은 같은 시각을 사용하고 displayOrder로 노출 순서를 고정합니다.
        LocalDateTime occurredAt = LocalDateTime.now();
        String sourceId = reviewId.toString();

        if (landmark.isPresent() && firstVisit) {
            findReward(reward, landmarkRewardKey(landmark.get().getLandmarkId()))
                    .ifPresent(landmarkReward -> activityHistoryService.record(
                            new ActivityHistoryRecord(
                                    usersId,
                                    "LANDMARK",
                                    REVIEW_SOURCE_TYPE,
                                    sourceId,
                                    eventKey(reviewId, "LANDMARK"),
                                    landmarkName(landmark.get(), content) + " 최초 방문",
                                    landmarkReward.description(),
                                    landmarkReward.xp(),
                                    landmarkReward.score(),
                                    LANDMARK_DISPLAY_ORDER,
                                    occurredAt
                            )
                    ));
        }

        if (attraction.isPresent() && firstVisit) {
            findReward(reward, attractionRewardKey(attraction.get().getAttractionId()))
                    .ifPresent(attractionReward -> activityHistoryService.record(
                            new ActivityHistoryRecord(
                                    usersId,
                                    "ATTRACTION",
                                    REVIEW_SOURCE_TYPE,
                                    sourceId,
                                    eventKey(reviewId, "ATTRACTION"),
                                    content.getTitle() + " 최초 방문",
                                    attractionReward.description(),
                                    attractionReward.xp(),
                                    attractionReward.score(),
                                    LANDMARK_DISPLAY_ORDER,
                                    occurredAt
                            )
                    ));
        }

        if (firstRegionVisit) {
            findReward(reward, regionVisitRewardKey(region.getRegionId()))
                    .ifPresent(regionReward -> activityHistoryService.record(
                            new ActivityHistoryRecord(
                                    usersId,
                                    "REGION",
                                    REVIEW_SOURCE_TYPE,
                                    sourceId,
                                    eventKey(reviewId, "REGION"),
                                    region.getRegionName() + " 지역 방문",
                                    regionReward.description(),
                                    regionReward.xp(),
                                    regionReward.score(),
                                    REGION_DISPLAY_ORDER,
                                    occurredAt
                            )
                    ));
        }

        if (visitResult.regionConquered()) {
            findReward(reward, regionConquestRewardKey(region.getRegionId()))
                    .ifPresent(conquestReward -> activityHistoryService.record(
                            new ActivityHistoryRecord(
                                    usersId,
                                    "REGION",
                                    REVIEW_SOURCE_TYPE,
                                    sourceId,
                                    eventKey(reviewId, "REGION_CONQUEST"),
                                    region.getRegionName() + " 지역 정복",
                                    conquestReward.description(),
                                    conquestReward.xp(),
                                    conquestReward.score(),
                                    REGION_CONQUEST_DISPLAY_ORDER,
                                    occurredAt
                            )
                    ));
        }

        if (landmark.isPresent() && visitResult.cardAcquired()) {
            activityHistoryService.record(new ActivityHistoryRecord(
                    usersId,
                    "CARD",
                    REVIEW_SOURCE_TYPE,
                    sourceId,
                    eventKey(reviewId, "CARD"),
                    landmarkName(landmark.get(), content) + " 카드 획득",
                    "랜드마크 최초 방문 카드가 추가되었습니다.",
                    0,
                    0,
                    CARD_DISPLAY_ORDER,
                    occurredAt
            ));
        }

        for (AcquiredBadgeInfo acquiredBadge : acquiredBadges) {
            findReward(reward, badgeRewardKey(acquiredBadge.badgeId()))
                    .ifPresent(badgeReward -> activityHistoryService.record(
                            new ActivityHistoryRecord(
                                    usersId,
                                    "BADGE",
                                    REVIEW_SOURCE_TYPE,
                                    sourceId,
                                    eventKey(reviewId, "BADGE:" + acquiredBadge.badgeId()),
                                    acquiredBadge.badgeName() + " 뱃지 획득",
                                    badgeReward.description(),
                                    badgeReward.xp(),
                                    badgeReward.score(),
                                    BADGE_DISPLAY_ORDER,
                                    occurredAt
                            )
                    ));
        }

        for (AcquiredAppellationInfo acquiredAppellation : acquiredAppellations) {
            findReward(reward, appellationRewardKey(acquiredAppellation.appellationId()))
                    .ifPresent(appellationReward -> activityHistoryService.record(
                            new ActivityHistoryRecord(
                                    usersId,
                                    "TITLE",
                                    REVIEW_SOURCE_TYPE,
                                    sourceId,
                                    eventKey(
                                            reviewId,
                                            "TITLE:" + acquiredAppellation.appellationId()
                                    ),
                                    acquiredAppellation.appellationName() + " 칭호 획득",
                                    appellationReward.description(),
                                    appellationReward.xp(),
                                    appellationReward.score(),
                                    APPELLATION_DISPLAY_ORDER,
                                    occurredAt
                            )
                    ));
        }

        for (int index = 0; index < missionCompletions.size(); index++) {
            MissionCompletionInfo completion = missionCompletions.get(index);
            activityHistoryService.record(new ActivityHistoryRecord(
                    usersId,
                    "MISSION",
                    "MISSION",
                    completion.missionId().toString(),
                    missionEventKey(completion.missionId()),
                    completion.missionName() + " 미션 완료",
                    completion.missionType() + " 미션을 완료했습니다.",
                    completion.xp(),
                    completion.score(),
                    MISSION_DISPLAY_ORDER + index,
                    occurredAt
            ));
        }

        boolean missionLevelUp = missionCompletions.stream()
                .anyMatch(completion -> completion.growth().levelUp());
        boolean missionRankUp = missionCompletions.stream()
                .anyMatch(completion -> completion.growth().rankUp());
        int currentLevel = missionCompletions.isEmpty()
                ? reward.currentLevel()
                : missionCompletions.getLast().growth().currentLevel();
        String currentTier = missionCompletions.isEmpty()
                ? reward.currentTier()
                : missionCompletions.getLast().growth().currentTier();

        if (reward.levelUp() || missionLevelUp) {
            activityHistoryService.record(new ActivityHistoryRecord(
                    usersId,
                    "LEVEL",
                    REVIEW_SOURCE_TYPE,
                    sourceId,
                    eventKey(reviewId, "LEVEL"),
                    "레벨 " + currentLevel + " 달성",
                    "레벨이 상승했습니다.",
                    0,
                    0,
                    LEVEL_DISPLAY_ORDER,
                    occurredAt
            ));
        }


        if (reward.rankUp() || missionRankUp) {
            activityHistoryService.record(new ActivityHistoryRecord(
                    usersId,
                    "RANK",
                    REVIEW_SOURCE_TYPE,
                    sourceId,
                    eventKey(reviewId, "RANK"),
                    currentTier + " 랭크 달성",
                    "랭크가 상승했습니다.",
                    0,
                    0,
                    RANK_DISPLAY_ORDER,
                    occurredAt
            ));
        }

    }

    /**
     * 지급 결과에서 이벤트 키가 일치하는 개별 보상을 찾습니다.
     *
     * @param reward 전체 보상 결과
     * @param rewardEventKey 찾을 보상 이벤트 키
     * @return 일치하는 보상, 없으면 빈 값
     */
    private Optional<ActivityRewardInfo> findReward(
            ActivityRewardResult reward, String rewardEventKey
    ) {
        return reward.rewards().stream()
                .filter(item -> item.eventKey().equals(rewardEventKey))
                .findFirst();
    }

    /**
     * 랜드마크 표시명이 없으면 관광 콘텐츠 공식명을 사용합니다.
     *
     * @param landmark 랜드마크
     * @param content 관광 콘텐츠
     * @return 활동 히스토리에 표시할 이름
     */
    private String landmarkName(Landmark landmark, TourismContent content) {
        String name = landmark.getLandmarkName();
        return name == null || name.isBlank() ? content.getTitle() : name;
    }

    /** 리뷰 활동의 중복 방지 이벤트 키를 생성합니다. */
    private String eventKey(Long reviewId, String activityType) {
        return REVIEW_SOURCE_TYPE + ":" + reviewId + ":" + activityType;
    }

    /** 랜드마크 최초 방문 보상 키를 생성합니다. */
    private String landmarkRewardKey(Long landmarkId) {
        return "LANDMARK_FIRST_VISIT:LANDMARK:" + landmarkId;
    }

    /** 일반 관광지 최초 방문 보상 키를 생성합니다. */
    private String attractionRewardKey(Long attractionId) {
        return "ATTRACTION_FIRST_VISIT:ATTRACTION:" + attractionId;
    }

    /** 지역 최초 방문 보상 키를 생성합니다. */
    private String regionVisitRewardKey(Long regionId) {
        return "REGION_FIRST_VISIT:REGION:" + regionId;
    }

    /** 지역 최초 정복 보상 키를 생성합니다. */
    private String regionConquestRewardKey(Long regionId) {
        return "REGION_CONQUEST:REGION:" + regionId;
    }

    /** 뱃지 최초 획득 보상 키를 생성합니다. */
    private String badgeRewardKey(Long badgeId) {
        return "BADGE_ACQUIRED:BADGE:" + badgeId;
    }

    /** 칭호 최초 획득 보상 키를 생성합니다. */
    private String appellationRewardKey(Long appellationId) {
        return "APPELLATION_ACQUIRED:APPELLATION:" + appellationId;
    }

    /** 리뷰 보상 정책과 요청 키를 결합한 보상 키를 생성합니다. */
    private String reviewRewardKey(String policyId, String requestKey) {
        return policyId + ":REQUEST:" + requestKey;
    }

    /** 미션 완료 활동의 중복 방지 이벤트 키를 생성합니다. */
    private String missionEventKey(Long missionId) {
        return "MISSION:" + missionId + ":COMPLETE";
    }

    /**
     * 방문·지역·여행 기록에 영향을 받는 주간 미션을 차례로 판정합니다.
     *
     * @param usersId 사용자 식별자
     * @param landmark 랜드마크 방문 여부
     * @return 이번 요청에서 새로 완료한 미션 목록
     */
    private List<MissionCompletionInfo> evaluateMissions(
            String usersId, boolean landmark
    ) {
        List<MissionCompletionInfo> completions = new ArrayList<>(
                missionAchievementService.evaluateVisit(
                        usersId, landmark ? "LANDMARK" : "ATTRACTION"
                )
        );
        completions.addAll(missionAchievementService.evaluateRegion(usersId));
        completions.addAll(missionAchievementService.evaluateReview(usersId));
        return List.copyOf(completions);
    }

    /**
     * 방문 기록 처리 중 발생한 카드 획득과 지역 최초 정복 결과입니다.
     */
    private record VisitResult(boolean cardAcquired, boolean regionConquered) {
    }
}
