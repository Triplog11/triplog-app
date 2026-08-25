package triplog.backend.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.attraction.service.AttractionService;
import triplog.backend.attraction.entity.Attraction;
import triplog.backend.attractionvisitlog.service.AttractionVisitLogService;
import triplog.backend.badge.service.BadgeService;
import triplog.backend.badge.service.AcquiredBadgeInfo;
import triplog.backend.achievement.service.AchievementContext;
import triplog.backend.appellation.service.AcquiredAppellationInfo;
import triplog.backend.appellation.service.AppellationService;
import triplog.backend.image.service.ImageService;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.landmark.service.UsersCardLandmarkService;
import triplog.backend.mission.service.MissionAchievementService;
import triplog.backend.mission.service.MissionCompletionInfo;
import triplog.backend.notification.service.NotificationService;
import triplog.backend.notification.service.NotificationEvent;
import triplog.backend.region.entity.Region;
import triplog.backend.region.service.RegionService;
import triplog.backend.review.dto.request.ReviewRequest.CreateRequest;
import triplog.backend.review.entity.Review;
import triplog.backend.reviewlog.service.ReviewLogService;
import triplog.backend.stats.service.ActivityRewardInfo;
import triplog.backend.stats.service.ActivityRewardGrant;
import triplog.backend.stats.service.ActivityRewardResult;
import triplog.backend.stats.service.StatsService;
import triplog.backend.stats.service.GrowthUpdateResult;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.service.TourismContentService;
import triplog.backend.users.service.ActivityHistoryRecord;
import triplog.backend.users.service.ActivityHistoryService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

/**
 * {@link ReviewFacadeService}의 방문 인증 및 활동 히스토리 조합 흐름을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class ReviewFacadeServiceTest {

    private static final String USERS_ID = "013d613e-de7d-4a31-8661-ba8e65ff14c6";
    private static final String IDEMPOTENCY_KEY = "review-request-1";

    @Mock private ReviewService reviewService;
    @Mock private LandmarkService landmarkService;
    @Mock private UsersCardLandmarkService usersCardLandmarkService;
    @Mock private ImageService imageService;
    @Mock private StatsService statsService;
    @Mock private ReviewLogService reviewLogService;
    @Mock private RegionService regionService;
    @Mock private TourismContentService tourismContentService;
    @Mock private AttractionService attractionService;
    @Mock private AttractionVisitLogService attractionVisitLogService;
    @Mock private MissionAchievementService missionAchievementService;
    @Mock private ActivityHistoryService activityHistoryService;
    @Mock private BadgeService badgeService;
    @Mock private AppellationService appellationService;
    @Mock private NotificationService notificationService;

    private ReviewFacadeService reviewFacadeService;

    @BeforeEach
    void setUp() {
        reviewFacadeService = new ReviewFacadeService(
                reviewService,
                landmarkService,
                usersCardLandmarkService,
                imageService,
                statsService,
                reviewLogService,
                regionService,
                tourismContentService,
                attractionService,
                attractionVisitLogService,
                missionAchievementService,
                activityHistoryService,
                badgeService,
                appellationService,
                notificationService
        );
    }

    @Test
    @DisplayName("최초 방문과 사진 여행 기록 보상 및 성취 활동을 처리한다")
    void createReview_RecordsActivityHistory() {
        // Given
        CreateRequest request = new CreateRequest(
                1L, "41", "110", "수원화성 방문", "방문 완료", 5.0F
        );
        Region region = region();
        TourismContent content = content(region);
        Landmark landmark = landmark();
        Review review = review();
        ActivityRewardResult reward = new ActivityRewardResult(
                List.of(
                        new ActivityRewardInfo(
                                "LANDMARK_FIRST_VISIT:LANDMARK:301",
                                "LANDMARK_FIRST_VISIT", "랜드마크 최초 방문", 50, 30
                        ),
                        new ActivityRewardInfo(
                                "REGION_FIRST_VISIT:REGION:201",
                                "REGION_FIRST_VISIT", "지역 최초 방문", 30, 20
                        ),
                        new ActivityRewardInfo(
                                "REGION_CONQUEST:REGION:201",
                                "REGION_CONQUEST", "지역 최초 정복", 100, 100
                        ),
                        new ActivityRewardInfo(
                                "BADGE_ACQUIRED:BADGE:501",
                                "BADGE_ACQUIRED", "뱃지 최초 획득", 20, 0
                        ),
                        new ActivityRewardInfo(
                                "APPELLATION_ACQUIRED:APPELLATION:601",
                                "APPELLATION_ACQUIRED", "칭호 최초 획득", 20, 0
                        ),
                        new ActivityRewardInfo(
                                "REVIEW_CREATE:REQUEST:" + IDEMPOTENCY_KEY,
                                "REVIEW_CREATE", "여행 기록 작성 보상", 15, 0
                        ),
                        new ActivityRewardInfo(
                                "REVIEW_IMAGE_BONUS:REQUEST:" + IDEMPOTENCY_KEY,
                                "REVIEW_IMAGE_BONUS", "사진 여행 기록 추가 보상", 5, 0
                        )
                ),
                240,
                150,
                2,
                "SILVER",
                true,
                true
        );
        given(tourismContentService.findOptionalById(1L)).willReturn(Optional.of(content));
        given(landmarkService.findByTourismContentId(101L)).willReturn(Optional.of(landmark));
        given(attractionService.findByTourismContentId(101L)).willReturn(Optional.empty());
        given(reviewService.createReview(USERS_ID, request, content)).willReturn(review);
        given(usersCardLandmarkService.acquireCard(USERS_ID, 301L)).willReturn(true);
        given(landmarkService.countLandmarksByRegion(201L)).willReturn(4L);
        given(landmarkService.countVisitedLandmarksByRegionAndUser(USERS_ID, 201L))
                .willReturn(3L);
        given(regionService.conquerIfEligible(USERS_ID, 201L, 4L, 3L)).willReturn(true);
        given(landmarkService.countDistinctVisitDates(USERS_ID, 301L)).willReturn(3L);
        given(regionService.countConsecutiveNewRegionVisits(USERS_ID)).willReturn(5);
        given(landmarkService.countWeekendVisits(USERS_ID)).willReturn(7L);
        given(attractionVisitLogService.countWeekendVisits(USERS_ID)).willReturn(3L);
        given(reviewService.countVisitedProvinces(USERS_ID)).willReturn(10);
        given(regionService.countProvinces()).willReturn(17);
        given(landmarkService.countLandmarksByProvince()).willReturn(java.util.Map.of("41", 4L));
        given(landmarkService.countVisitedLandmarksByProvinceAndUser(USERS_ID))
                .willReturn(java.util.Map.of("41", 3L));
        given(badgeService.acquireEligibleBadges(
                org.mockito.ArgumentMatchers.eq(USERS_ID),
                any()
        )).willReturn(List.of(new AcquiredBadgeInfo(501L, "첫 발자국")));
        given(appellationService.acquireEligibleAppellations(
                org.mockito.ArgumentMatchers.eq(USERS_ID),
                any()
        )).willReturn(List.of(new AcquiredAppellationInfo(601L, "여행의 시작")));
        given(statsService.applyActivityPolicies(
                eq(USERS_ID), anyList()
        )).willReturn(reward);
        given(missionAchievementService.evaluateVisit(USERS_ID, "LANDMARK"))
                .willReturn(List.of());
        given(missionAchievementService.evaluateRegion(USERS_ID)).willReturn(List.of());
        given(missionAchievementService.evaluateReview(USERS_ID)).willReturn(List.of(
                new MissionCompletionInfo(
                        701L,
                        "여행 기록 남기기",
                        "WEEKLY",
                        30,
                        0,
                        new GrowthUpdateResult(2, "SILVER", false, false)
                )
        ));

        // When
        MultipartFile image = mock(MultipartFile.class);
        var response = reviewFacadeService.createReview(
                USERS_ID, request, List.of(image), IDEMPOTENCY_KEY
        );

        // Then
        assertThat(response.getTotalXp()).isEqualTo(240);
        assertThat(response.getTotalScore()).isEqualTo(150);
        ArgumentCaptor<ActivityHistoryRecord> captor =
                ArgumentCaptor.forClass(ActivityHistoryRecord.class);
        verify(activityHistoryService, times(9)).record(captor.capture());
        List<ActivityHistoryRecord> records = captor.getAllValues().stream()
                .sorted(java.util.Comparator.comparingInt(ActivityHistoryRecord::displayOrder))
                .toList();
        assertThat(records).extracting(ActivityHistoryRecord::activityType)
                .containsExactly(
                        "LANDMARK", "REGION", "REGION", "CARD", "BADGE", "TITLE",
                        "MISSION", "LEVEL", "RANK"
                );
        assertThat(records).extracting(ActivityHistoryRecord::displayOrder)
                .containsExactly(10, 20, 25, 30, 40, 45, 50, 90, 95);
        assertThat(records.get(0).xp()).isEqualTo(50);
        assertThat(records.get(0).score()).isEqualTo(30);
        assertThat(records.get(1).xp()).isEqualTo(30);
        assertThat(records.get(1).score()).isEqualTo(20);
        assertThat(records.get(2).xp()).isEqualTo(100);
        assertThat(records.get(2).score()).isEqualTo(100);
        assertThat(records.get(3).xp()).isZero();
        assertThat(records.get(3).score()).isZero();
        assertThat(records.get(4).xp()).isEqualTo(20);
        assertThat(records.get(4).score()).isZero();
        assertThat(records.get(5).xp()).isEqualTo(20);
        assertThat(records.get(5).score()).isZero();
        assertThat(records.get(6).xp()).isEqualTo(30);
        assertThat(records.get(6).score()).isZero();
        assertThat(records.get(7).xp()).isZero();
        assertThat(records.get(7).score()).isZero();
        assertThat(records.get(8).xp()).isZero();
        assertThat(records.get(8).score()).isZero();
        assertThat(records).extracting(ActivityHistoryRecord::createdAt)
                .containsOnly(records.getFirst().createdAt());
        verify(reviewService).updateRewardScore(10L, 150);
        verify(missionAchievementService).evaluateVisit(USERS_ID, "LANDMARK");
        ArgumentCaptor<AchievementContext> badgeContextCaptor =
                ArgumentCaptor.forClass(AchievementContext.class);
        verify(badgeService).acquireEligibleBadges(
                org.mockito.ArgumentMatchers.eq(USERS_ID),
                badgeContextCaptor.capture()
        );
        AchievementContext badgeContext = badgeContextCaptor.getValue();
        assertThat(badgeContext.metric("CONTENT_DISTINCT_VISIT_DATE_COUNT")).isEqualTo(3);
        assertThat(badgeContext.metric("NEW_REGION_STREAK")).isEqualTo(5);
        assertThat(badgeContext.metric("WEEKEND_VISIT_COUNT")).isEqualTo(10);
        assertThat(badgeContext.metric("PROVINCE_VISIT_COUNT")).isEqualTo(10);
        assertThat(badgeContext.metric("PROVINCE_LANDMARK_RATE_41")).isEqualTo(75);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ActivityRewardGrant>> grantCaptor =
                ArgumentCaptor.forClass(List.class);
        verify(statsService).applyActivityPolicies(eq(USERS_ID), grantCaptor.capture());
        assertThat(grantCaptor.getValue())
                .extracting(ActivityRewardGrant::eventKey)
                .containsExactly(
                        "LANDMARK_FIRST_VISIT:LANDMARK:301",
                        "REGION_FIRST_VISIT:REGION:201",
                        "REGION_CONQUEST:REGION:201",
                        "BADGE_ACQUIRED:BADGE:501",
                        "APPELLATION_ACQUIRED:APPELLATION:601",
                        "REVIEW_CREATE:REQUEST:" + IDEMPOTENCY_KEY,
                        "REVIEW_IMAGE_BONUS:REQUEST:" + IDEMPOTENCY_KEY
                );
        assertThat(grantCaptor.getValue())
                .extracting(ActivityRewardGrant::requestKey)
                .containsOnly(IDEMPOTENCY_KEY);
    }

    @Test
    @DisplayName("일반 관광지 최초 방문 보상을 활동 히스토리에 기록한다")
    void createReview_RecordsAttractionHistory() {
        // Given
        CreateRequest request = new CreateRequest(
                1L, "41", "110", null, null, 5.0F
        );
        Region region = region();
        TourismContent content = content(region);
        Attraction attraction = org.mockito.Mockito.mock(Attraction.class);
        Review review = review();
        given(attraction.getAttractionId()).willReturn(401L);
        given(tourismContentService.findOptionalById(1L)).willReturn(Optional.of(content));
        given(landmarkService.findByTourismContentId(101L)).willReturn(Optional.empty());
        given(attractionService.findByTourismContentId(101L))
                .willReturn(Optional.of(attraction));
        given(reviewService.createReview(USERS_ID, request, content)).willReturn(review);
        given(statsService.applyActivityPolicies(eq(USERS_ID), anyList()))
                .willReturn(new ActivityRewardResult(
                        List.of(new ActivityRewardInfo(
                                "ATTRACTION_FIRST_VISIT:ATTRACTION:401",
                                "ATTRACTION_FIRST_VISIT",
                                "일반 관광지 최초 방문 보상",
                                20,
                                10
                        )),
                        20,
                        10,
                        1,
                        "BRONZE",
                        false,
                        false
                ));

        // When
        reviewFacadeService.createReview(
                USERS_ID, request, null, "attraction-request-1"
        );

        // Then
        ArgumentCaptor<ActivityHistoryRecord> historyCaptor =
                ArgumentCaptor.forClass(ActivityHistoryRecord.class);
        verify(activityHistoryService).record(historyCaptor.capture());
        assertThat(historyCaptor.getValue().activityType()).isEqualTo("ATTRACTION");
        assertThat(historyCaptor.getValue().xp()).isEqualTo(20);
        assertThat(historyCaptor.getValue().score()).isEqualTo(10);
    }

    /**
     * 이미 방문한 관광 콘텐츠를 재인증하면 방문 인증 알림을 생성하지 않는지 검증합니다.
     */
    @Test
    @DisplayName("재방문 인증에는 방문 인증 알림을 생성하지 않는다")
    void createReview_DoesNotNotifyRepeatedVisit() {
        // Given
        CreateRequest request = new CreateRequest(
                1L, "41", "110", "재방문", "재방문 기록", 4.5F
        );
        Region region = region();
        TourismContent content = content(region);
        Landmark landmark = landmark();
        Review review = review();
        given(tourismContentService.findOptionalById(1L)).willReturn(Optional.of(content));
        given(landmarkService.findByTourismContentId(101L)).willReturn(Optional.of(landmark));
        given(attractionService.findByTourismContentId(101L)).willReturn(Optional.empty());
        given(landmarkService.hasVisited(USERS_ID, 301L)).willReturn(true);
        given(regionService.hasVisited(USERS_ID, 201L)).willReturn(true);
        given(reviewService.createReview(USERS_ID, request, content)).willReturn(review);
        given(statsService.applyActivityPolicies(eq(USERS_ID), anyList()))
                .willReturn(new ActivityRewardResult(
                        List.of(), 0, 0, 1, "BRONZE", false, false
                ));

        // When
        reviewFacadeService.createReview(
                USERS_ID, request, null, "repeat-visit-request-1"
        );

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<NotificationEvent>> notificationCaptor =
                ArgumentCaptor.forClass(List.class);
        verify(notificationService).createNotifications(
                eq(USERS_ID), notificationCaptor.capture()
        );
        assertThat(notificationCaptor.getValue())
                .extracting(NotificationEvent::triggerEvent)
                .doesNotContain("VISIT_VERIFICATION_SUCCEEDED");
    }

    private Region region() {
        Region region = org.mockito.Mockito.mock(Region.class);
        given(region.getRegionId()).willReturn(201L);
        org.mockito.Mockito.lenient().when(region.getRegionName()).thenReturn("수원시");
        given(region.getLegalRegionCode()).willReturn("41");
        given(region.getLegalDistrictCode()).willReturn("110");
        return region;
    }

    private TourismContent content(Region region) {
        TourismContent content = org.mockito.Mockito.mock(TourismContent.class);
        given(content.getTourismContentId()).willReturn(101L);
        given(content.getRegion()).willReturn(region);
        given(content.getTitle()).willReturn("수원화성");
        return content;
    }

    private Landmark landmark() {
        Landmark landmark = org.mockito.Mockito.mock(Landmark.class);
        given(landmark.getLandmarkId()).willReturn(301L);
        org.mockito.Mockito.lenient().when(landmark.getLandmarkName()).thenReturn("수원화성");
        return landmark;
    }

    private Review review() {
        Review review = org.mockito.Mockito.mock(Review.class);
        given(review.getReviewId()).willReturn(10L);
        return review;
    }
}
