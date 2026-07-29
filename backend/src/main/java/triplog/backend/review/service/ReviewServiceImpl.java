package triplog.backend.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.image.service.ImageService;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.entity.Region;
import triplog.backend.region.service.RegionService;
import triplog.backend.review.dto.request.ReviewRequest.CreateReviewRequest;
import triplog.backend.review.dto.response.ReviewResponse.CreateReviewResponse;
import triplog.backend.review.entity.Review;
import triplog.backend.review.exception.ReviewErrorCode;
import triplog.backend.review.exception.ReviewException;
import triplog.backend.review.repository.ReviewRepository;
import triplog.backend.reviewlog.service.ReviewLogService;
import triplog.backend.stats.service.StatsService;
import triplog.backend.tourismcontent.entity.TourismContent;

import java.util.List;

/**
 * {@link ReviewService}의 기본 구현체입니다.
 * 방문 인증 리뷰를 등록합니다.
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final int REVIEW_XP = 50;

    private final ReviewRepository reviewRepository;
    private final LandmarkService landmarkService;
    private final ImageService imageService;
    private final StatsService statsService;
    private final ReviewLogService reviewLogService;
    private final RegionService regionService;

    /**
     * 방문 인증 리뷰를 등록합니다.
     *
     * @param usersId 사용자 식별자
     * @param request 방문 인증 등록 요청
     * @param files   인증 이미지 파일 목록
     * @return 방문 인증 등록 응답
     */
    @Override
    @Transactional
    public CreateReviewResponse createReview(String usersId, CreateReviewRequest request, List<MultipartFile> files) {

        Landmark landmark = landmarkService.findByIdWithContent(request.getLandmarkId());
        TourismContent tourismContent = landmark.getTourismContent();

        Region region = tourismContent.getRegion();
        if (!region.getLegalRegionCode().equals(request.getLegalRegionCode())
                || !region.getLegalDistrictCode().equals(request.getLegalDistrictCode())) {
            throw new ReviewException(ReviewErrorCode.REGION_CODE_MISMATCH);
        }

        if (reviewRepository.existsByUsersIdAndTourismContentTourismContentId(
                usersId, tourismContent.getTourismContentId())) {
            throw new ReviewException(ReviewErrorCode.ALREADY_VERIFIED_LANDMARK);
        }

        String reviewTitle = request.getReviewTitle() != null ? request.getReviewTitle() : "";
        String reviewContent = request.getReviewContent() != null ? request.getReviewContent() : "";
        float reviewScore = request.getReviewScore() != null ? request.getReviewScore() : 5;
        int reviewPoint = request.getReviewPoint();

        Review review = new Review(
                usersId,
                tourismContent,
                reviewTitle,
                reviewContent,
                reviewScore,
                reviewPoint
        );
        reviewRepository.save(review);

        if (files != null && !files.isEmpty()) {
            imageService.uploadAndSave(review.getReviewId(), files);
        }

        statsService.addXpAndScore(usersId, REVIEW_XP, reviewPoint);

        String logContent = landmark.getLandmarkName() + " 방문 인증";
        reviewLogService.createLog(review.getReviewId(), logContent, reviewPoint);

        landmarkService.saveVisitLog(usersId, landmark.getLandmarkId());

        landmarkService.acquireCard(usersId, landmark.getLandmarkId());

        regionService.recordRegionVisit(usersId, region.getRegionId());

        return CreateReviewResponse.toDto();
    }
}
