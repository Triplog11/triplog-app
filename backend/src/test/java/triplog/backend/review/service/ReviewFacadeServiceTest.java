package triplog.backend.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.attraction.service.AttractionService;
import triplog.backend.attractionvisitlog.service.AttractionVisitLogService;
import triplog.backend.image.service.ImageService;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.mission.service.MissionAchievementService;
import triplog.backend.region.entity.Region;
import triplog.backend.region.service.RegionService;
import triplog.backend.review.dto.request.ReviewRequest.CreateRequest;
import triplog.backend.review.entity.Review;
import triplog.backend.reviewlog.service.ReviewLogService;
import triplog.backend.stats.service.ActivityRewardInfo;
import triplog.backend.stats.service.ActivityRewardResult;
import triplog.backend.stats.service.StatsService;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.service.TourismContentService;
import triplog.backend.users.service.ActivityHistoryRecord;
import triplog.backend.users.service.ActivityHistoryService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * {@link ReviewFacadeService}의 방문 인증 및 활동 히스토리 조합 흐름을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class ReviewFacadeServiceTest {

    private static final String USERS_ID = "013d613e-de7d-4a31-8661-ba8e65ff14c6";

    @Mock private ReviewService reviewService;
    @Mock private LandmarkService landmarkService;
    @Mock private ImageService imageService;
    @Mock private StatsService statsService;
    @Mock private ReviewLogService reviewLogService;
    @Mock private RegionService regionService;
    @Mock private TourismContentService tourismContentService;
    @Mock private AttractionService attractionService;
    @Mock private AttractionVisitLogService attractionVisitLogService;
    @Mock private MissionAchievementService missionAchievementService;
    @Mock private ActivityHistoryService activityHistoryService;

    private ReviewFacadeService reviewFacadeService;

    @BeforeEach
    void setUp() {
        reviewFacadeService = new ReviewFacadeService(
                reviewService,
                landmarkService,
                imageService,
                statsService,
                reviewLogService,
                regionService,
                tourismContentService,
                attractionService,
                attractionVisitLogService,
                missionAchievementService,
                activityHistoryService
        );
    }

    @Test
    @DisplayName("최초 랜드마크와 지역 방문 및 레벨 상승을 통합 활동 로그로 기록한다")
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
                                "LANDMARK_FIRST_VISIT", "랜드마크 최초 방문", 50, 30
                        ),
                        new ActivityRewardInfo(
                                "REGION_FIRST_VISIT", "지역 최초 방문", 100, 100
                        ),
                        new ActivityRewardInfo(
                                "REVIEW_CREATE", "여행 기록 작성", 10, 0
                        )
                ),
                160,
                130,
                2,
                "BRONZE",
                true,
                false
        );
        given(tourismContentService.findOptionalById(1L)).willReturn(Optional.of(content));
        given(landmarkService.findByTourismContentId(101L)).willReturn(Optional.of(landmark));
        given(attractionService.findByTourismContentId(101L)).willReturn(Optional.empty());
        given(reviewService.createReview(USERS_ID, request, content)).willReturn(review);
        given(landmarkService.acquireCard(USERS_ID, 301L)).willReturn(true);
        given(statsService.applyActivityPolicies(
                USERS_ID, "LANDMARK_FIRST_VISIT", "REGION_FIRST_VISIT", "REVIEW_CREATE"
        )).willReturn(reward);

        // When
        var response = reviewFacadeService.createReview(USERS_ID, request, null);

        // Then
        assertThat(response.getTotalXp()).isEqualTo(160);
        assertThat(response.getTotalScore()).isEqualTo(130);
        ArgumentCaptor<ActivityHistoryRecord> captor =
                ArgumentCaptor.forClass(ActivityHistoryRecord.class);
        verify(activityHistoryService, times(3)).record(captor.capture());
        List<ActivityHistoryRecord> records = captor.getAllValues();
        assertThat(records).extracting(ActivityHistoryRecord::activityType)
                .containsExactly("LANDMARK", "REGION", "LEVEL");
        assertThat(records).extracting(ActivityHistoryRecord::displayOrder)
                .containsExactly(10, 20, 30);
        assertThat(records.get(0).xp()).isEqualTo(50);
        assertThat(records.get(0).score()).isEqualTo(30);
        assertThat(records.get(1).xp()).isEqualTo(100);
        assertThat(records.get(1).score()).isEqualTo(100);
        assertThat(records.get(2).xp()).isZero();
        assertThat(records.get(2).score()).isZero();
        assertThat(records).extracting(ActivityHistoryRecord::createdAt)
                .containsOnly(records.getFirst().createdAt());
        verify(reviewService).updateRewardScore(10L, 130);
        verify(missionAchievementService).evaluateVisit(USERS_ID, "LANDMARK", true);
    }

    private Region region() {
        Region region = org.mockito.Mockito.mock(Region.class);
        given(region.getRegionId()).willReturn(201L);
        given(region.getRegionName()).willReturn("수원시");
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
        given(landmark.getLandmarkName()).willReturn("수원화성");
        return landmark;
    }

    private Review review() {
        Review review = org.mockito.Mockito.mock(Review.class);
        given(review.getReviewId()).willReturn(10L);
        return review;
    }
}
