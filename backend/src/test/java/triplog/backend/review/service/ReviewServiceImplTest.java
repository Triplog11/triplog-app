package triplog.backend.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
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
import triplog.backend.review.exception.ReviewException;
import triplog.backend.review.repository.ReviewRepository;
import triplog.backend.reviewlog.service.ReviewLogService;
import triplog.backend.stats.service.ActivityRewardResult;
import triplog.backend.stats.service.StatsService;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.service.TourismContentService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static triplog.backend.review.exception.ReviewErrorCode.REGION_CODE_MISMATCH;

/**
 * {@link ReviewServiceImpl}의 방문 인증 및 정책 보상 처리를 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    private static final String USERS_ID = "013d613e-de7d-4a31-8661-ba8e65ff14c6";
    private static final ActivityRewardResult REWARD_RESULT =
            new ActivityRewardResult(List.of(), 95, 50, 1, "BRONZE", false, false);

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private LandmarkService landmarkService;

    @Mock
    private ImageService imageService;

    @Mock
    private StatsService statsService;

    @Mock
    private ReviewLogService reviewLogService;

    @Mock
    private RegionService regionService;

    @Mock
    private TourismContentService tourismContentService;

    @Mock
    private AttractionService attractionService;

    @Mock
    private AttractionVisitLogService attractionVisitLogService;

    @Mock
    private MissionAchievementService missionAchievementService;

    private ReviewServiceImpl reviewService;

    /**
     * 테스트 대상과 Mock 의존성을 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(
                reviewRepository, landmarkService, imageService, statsService,
                reviewLogService, regionService, tourismContentService,
                attractionService, attractionVisitLogService, missionAchievementService
        );
    }

    /**
     * 최초 랜드마크와 신규 지역 및 여행 기록 정책을 함께 적용하는지 검증합니다.
     */
    @Test
    @DisplayName("방문 인증 등록 시 서버가 적용할 보상 정책을 결정한다")
    void createReview_AppliesServerPolicies() {
        // given
        CreateRequest request = new CreateRequest(1L, "41", "110", "수원화성 방문", "방문 완료", 5.0F);
        TourismContent content = givenLandmarkContent(request, false);
        Review review = mock(Review.class);
        given(review.getReviewId()).willReturn(10L);
        given(reviewRepository.save(any(Review.class))).willReturn(review);
        given(statsService.applyActivityPolicies(
                USERS_ID, "LANDMARK_FIRST_VISIT", "REGION_FIRST_VISIT", "REVIEW_CREATE"
        )).willReturn(REWARD_RESULT);

        // when
        var response = reviewService.createReview(USERS_ID, request, null);

        // then
        assertThat(response.getIsVerified()).isTrue();
        assertThat(response.getTotalScore()).isEqualTo(50);
        verify(reviewRepository).updateRewardScore(10L, 50);
        verify(regionService).recordRegionVisit(USERS_ID, content.getRegion().getRegionId());
    }

    /**
     * 여행 기록에 이미지가 있으면 이미지 보너스 정책을 포함하는지 검증합니다.
     */
    @Test
    @DisplayName("사진이 포함된 여행 기록은 이미지 보너스 정책을 적용한다")
    void createReview_AppliesImageBonus() {
        // given
        CreateRequest request = new CreateRequest(1L, "41", "110", "기록", "내용", 5.0F);
        givenLandmarkContent(request, false);
        Review review = mock(Review.class);
        given(review.getReviewId()).willReturn(10L);
        given(reviewRepository.save(any(Review.class))).willReturn(review);
        MultipartFile file = mock(MultipartFile.class);
        given(statsService.applyActivityPolicies(
                USERS_ID, "LANDMARK_FIRST_VISIT", "REGION_FIRST_VISIT",
                "REVIEW_CREATE", "REVIEW_IMAGE_BONUS"
        )).willReturn(REWARD_RESULT);

        // when
        reviewService.createReview(USERS_ID, request, List.of(file));

        // then
        verify(imageService).uploadAndSave(10L, List.of(file));
    }

    /**
     * 재방문 시 최초 방문 정책을 제외하는지 검증합니다.
     */
    @Test
    @DisplayName("재방문은 최초 방문 보상을 지급하지 않는다")
    void createReview_ExcludesFirstVisitPolicyOnRevisit() {
        // given
        CreateRequest request = new CreateRequest(1L, "41", "110", "재방문", "기록", 5.0F);
        givenLandmarkContent(request, true);
        given(regionService.hasVisited(USERS_ID, 1L)).willReturn(true);
        Review review = mock(Review.class);
        given(review.getReviewId()).willReturn(10L);
        given(reviewRepository.save(any(Review.class))).willReturn(review);
        given(statsService.applyActivityPolicies(USERS_ID, "REVIEW_CREATE")).willReturn(REWARD_RESULT);

        // when
        reviewService.createReview(USERS_ID, request, null);

        // then
        verify(statsService).applyActivityPolicies(USERS_ID, "REVIEW_CREATE");
        verify(missionAchievementService).evaluateVisit(USERS_ID, "LANDMARK", false);
    }

    /**
     * 여행 기록의 일일 보상 한도를 초과하면 기록 정책을 제외하는지 검증합니다.
     */
    @Test
    @DisplayName("하루 세 건을 초과한 여행 기록에는 기록 XP를 지급하지 않는다")
    void createReview_ExcludesTravelRecordPolicyAfterDailyLimit() {
        // given
        CreateRequest request = new CreateRequest(1L, "41", "110", "네 번째 기록", "내용", 5.0F);
        givenLandmarkContent(request, true);
        given(regionService.hasVisited(USERS_ID, 1L)).willReturn(true);
        given(reviewLogService.countRewardedTravelRecordsToday(USERS_ID)).willReturn(3L);
        Review review = mock(Review.class);
        given(review.getReviewId()).willReturn(10L);
        given(reviewRepository.save(any(Review.class))).willReturn(review);
        given(statsService.applyActivityPolicies(USERS_ID)).willReturn(REWARD_RESULT);

        // when
        reviewService.createReview(USERS_ID, request, null);

        // then
        verify(statsService).applyActivityPolicies(USERS_ID);
    }

    /**
     * 요청 지역 코드가 관광 콘텐츠의 지역 코드와 다르면 등록을 거부하는지 검증합니다.
     */
    @Test
    @DisplayName("지역 코드가 일치하지 않으면 예외가 발생한다")
    void createReview_RejectsRegionCodeMismatch() {
        // given
        CreateRequest request = new CreateRequest(1L, "42", "150", "", "", 5.0F);
        Region region = mock(Region.class);
        given(region.getLegalRegionCode()).willReturn("41");
        TourismContent content = mock(TourismContent.class);
        given(content.getTourismContentId()).willReturn(101L);
        given(content.getRegion()).willReturn(region);
        given(tourismContentService.findOptionalById(1L)).willReturn(Optional.of(content));
        given(landmarkService.findByTourismContentId(101L)).willReturn(Optional.of(mock(Landmark.class)));

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(USERS_ID, request, null))
                .isInstanceOf(ReviewException.class)
                .extracting("errorCode")
                .isEqualTo(REGION_CODE_MISMATCH);
        verify(reviewRepository, never()).save(any());
        verify(statsService, never()).applyActivityPolicies(anyString(), any(String[].class));
    }

    /**
     * 랜드마크 방문 테스트에 필요한 콘텐츠와 지역 정보를 구성합니다.
     *
     * @param request   방문 인증 요청
     * @param revisited 기존 방문 여부
     * @return 구성된 관광 콘텐츠 Mock
     */
    private TourismContent givenLandmarkContent(CreateRequest request, boolean revisited) {
        Region region = mock(Region.class);
        given(region.getRegionId()).willReturn(1L);
        given(region.getLegalRegionCode()).willReturn(request.getLegalRegionCode());
        given(region.getLegalDistrictCode()).willReturn(request.getLegalDistrictCode());
        TourismContent content = mock(TourismContent.class);
        given(content.getTourismContentId()).willReturn(101L);
        given(content.getRegion()).willReturn(region);
        given(content.getTitle()).willReturn("수원화성");
        Landmark landmark = mock(Landmark.class);
        given(landmark.getLandmarkId()).willReturn(1L);
        given(tourismContentService.findOptionalById(request.getTourismContentId())).willReturn(Optional.of(content));
        given(landmarkService.findByTourismContentId(101L)).willReturn(Optional.of(landmark));
        given(landmarkService.hasVisited(USERS_ID, 1L)).willReturn(revisited);
        return content;
    }
}
