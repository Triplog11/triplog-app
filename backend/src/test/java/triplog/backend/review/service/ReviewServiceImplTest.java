package triplog.backend.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.image.service.ImageService;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.entity.Region;
import triplog.backend.region.service.RegionService;
import triplog.backend.review.dto.request.ReviewRequest.CreateReviewRequest;
import triplog.backend.review.dto.response.ReviewResponse.CreateReviewResponse;
import triplog.backend.review.entity.Review;
import triplog.backend.review.exception.ReviewException;
import triplog.backend.review.repository.ReviewRepository;
import triplog.backend.reviewlog.service.ReviewLogService;
import triplog.backend.stats.service.StatsService;
import triplog.backend.tourismcontent.entity.TourismContent;
import java.util.List;
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
import static org.mockito.Mockito.when;
import static triplog.backend.review.exception.ReviewErrorCode.ALREADY_VERIFIED_LANDMARK;
import static triplog.backend.review.exception.ReviewErrorCode.REGION_CODE_MISMATCH;

/**
 * {@link ReviewServiceImpl}의 방문 인증 등록 흐름을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    private static final String USERS_ID = "013d613e-de7d-4a31-8661-ba8e65ff14c6";

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

    private ReviewServiceImpl reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(
                reviewRepository, landmarkService, imageService,
                statsService, reviewLogService, regionService
        );
    }

    /**
     * 방문 인증 등록이 정상 처리되는지 검증합니다.
     */
    @Test
    @DisplayName("방문 인증 등록에 성공한다")
    void createReview_Success() {
        // given
        CreateReviewRequest request = new CreateReviewRequest(
                1L, "41", "110", "수원화성 방문", "방문 인증 완료", 5, 150
        );

        Region region = mock(Region.class);
        when(region.getRegionId()).thenReturn(1L);
        when(region.getLegalRegionCode()).thenReturn("41");
        when(region.getLegalDistrictCode()).thenReturn("110");

        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getTourismContentId()).thenReturn(101L);
        when(tourismContent.getRegion()).thenReturn(region);

        Landmark landmark = mock(Landmark.class);
        when(landmark.getLandmarkId()).thenReturn(1L);
        when(landmark.getLandmarkName()).thenReturn("수원 화성");
        when(landmark.getTourismContent()).thenReturn(tourismContent);

        given(landmarkService.findByIdWithContent(1L)).willReturn(landmark);
        given(reviewRepository.existsByUsersIdAndTourismContentTourismContentId(USERS_ID, 101L))
                .willReturn(false);
        given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        CreateReviewResponse response = reviewService.createReview(USERS_ID, request, null);

        // then
        assertThat(response.getIsVerified()).isTrue();

        verify(statsService).addXpAndScore(USERS_ID, 50, 150);
        verify(reviewLogService).createLog(any(), anyString(), anyInt());
        verify(landmarkService).saveVisitLog(USERS_ID, 1L);
        verify(landmarkService).acquireCard(USERS_ID, 1L);
        verify(regionService).recordRegionVisit(USERS_ID, 1L);
    }

    /**
     * 이미지 파일이 있을 때 업로드가 호출되는지 검증합니다.
     */
    @Test
    @DisplayName("이미지 파일이 있으면 업로드를 수행한다")
    void createReview_WithImages() {
        // given
        CreateReviewRequest request = new CreateReviewRequest(
                1L, "41", "110", "", "", 5, 50
        );

        Region region = mock(Region.class);
        when(region.getRegionId()).thenReturn(1L);
        when(region.getLegalRegionCode()).thenReturn("41");
        when(region.getLegalDistrictCode()).thenReturn("110");

        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getTourismContentId()).thenReturn(101L);
        when(tourismContent.getRegion()).thenReturn(region);

        Landmark landmark = mock(Landmark.class);
        when(landmark.getLandmarkId()).thenReturn(1L);
        when(landmark.getLandmarkName()).thenReturn("수원 화성");
        when(landmark.getTourismContent()).thenReturn(tourismContent);

        MultipartFile file = mock(MultipartFile.class);

        given(landmarkService.findByIdWithContent(1L)).willReturn(landmark);
        given(reviewRepository.existsByUsersIdAndTourismContentTourismContentId(USERS_ID, 101L))
                .willReturn(false);
        given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        reviewService.createReview(USERS_ID, request, List.of(file));

        // then
        verify(imageService).uploadAndSave(any(), any(List.class));
    }

    /**
     * 이미지 파일이 없을 때 업로드가 호출되지 않는지 검증합니다.
     */
    @Test
    @DisplayName("이미지 파일이 없으면 업로드를 수행하지 않는다")
    void createReview_WithoutImages() {
        // given
        CreateReviewRequest request = new CreateReviewRequest(
                1L, "41", "110", "", "", 5, 50
        );

        Region region = mock(Region.class);
        when(region.getRegionId()).thenReturn(1L);
        when(region.getLegalRegionCode()).thenReturn("41");
        when(region.getLegalDistrictCode()).thenReturn("110");

        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getTourismContentId()).thenReturn(101L);
        when(tourismContent.getRegion()).thenReturn(region);

        Landmark landmark = mock(Landmark.class);
        when(landmark.getLandmarkId()).thenReturn(1L);
        when(landmark.getLandmarkName()).thenReturn("수원 화성");
        when(landmark.getTourismContent()).thenReturn(tourismContent);

        given(landmarkService.findByIdWithContent(1L)).willReturn(landmark);
        given(reviewRepository.existsByUsersIdAndTourismContentTourismContentId(USERS_ID, 101L))
                .willReturn(false);
        given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        reviewService.createReview(USERS_ID, request, null);

        // then
        verify(imageService, never()).uploadAndSave(anyLong(), any(List.class));
    }

    /**
     * 지역 코드가 일치하지 않으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("지역 코드가 일치하지 않으면 예외가 발생한다")
    void createReview_RegionCodeMismatch() {
        // given
        CreateReviewRequest request = new CreateReviewRequest(
                1L, "42", "150", "", "", 5, 50
        );

        Region region = mock(Region.class);
        when(region.getLegalRegionCode()).thenReturn("41");

        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getRegion()).thenReturn(region);

        Landmark landmark = mock(Landmark.class);
        when(landmark.getTourismContent()).thenReturn(tourismContent);

        given(landmarkService.findByIdWithContent(1L)).willReturn(landmark);

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(USERS_ID, request, null))
                .isInstanceOf(ReviewException.class)
                .extracting("errorCode")
                .isEqualTo(REGION_CODE_MISMATCH);

        verify(reviewRepository, never()).save(any());
        verify(statsService, never()).addXpAndScore(anyString(), anyInt(), anyInt());
    }

    /**
     * 이미 인증된 랜드마크이면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("이미 인증된 랜드마크이면 예외가 발생한다")
    void createReview_AlreadyVerified() {
        // given
        CreateReviewRequest request = new CreateReviewRequest(
                1L, "41", "110", "", "", 5, 50
        );

        Region region = mock(Region.class);
        when(region.getLegalRegionCode()).thenReturn("41");
        when(region.getLegalDistrictCode()).thenReturn("110");

        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getTourismContentId()).thenReturn(101L);
        when(tourismContent.getRegion()).thenReturn(region);

        Landmark landmark = mock(Landmark.class);
        when(landmark.getTourismContent()).thenReturn(tourismContent);

        given(landmarkService.findByIdWithContent(1L)).willReturn(landmark);
        given(reviewRepository.existsByUsersIdAndTourismContentTourismContentId(USERS_ID, 101L))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(USERS_ID, request, null))
                .isInstanceOf(ReviewException.class)
                .extracting("errorCode")
                .isEqualTo(ALREADY_VERIFIED_LANDMARK);

        verify(reviewRepository, never()).save(any());
        verify(statsService, never()).addXpAndScore(anyString(), anyInt(), anyInt());
    }

}
