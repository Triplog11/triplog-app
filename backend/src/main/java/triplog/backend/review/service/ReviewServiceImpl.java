package triplog.backend.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.attraction.entity.Attraction;
import triplog.backend.attraction.repository.AttractionRepository;
import triplog.backend.attractionvisitlog.service.AttractionVisitLogService;
import triplog.backend.image.service.ImageService;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.mission.service.MissionAchievementService;
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
import triplog.backend.tourismcontent.repository.TourismContentRepository;

import java.util.List;
import java.util.Optional;

import static triplog.backend.review.exception.ReviewErrorCode.TOURISM_CONTENT_NOT_FOUND;
import static triplog.backend.review.exception.ReviewErrorCode.UNSUPPORTED_VISIT_CONTENT;

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
    private final TourismContentRepository tourismContentRepository;
    private final AttractionRepository attractionRepository;
    private final AttractionVisitLogService attractionVisitLogService;
    private final MissionAchievementService missionAchievementService;

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

        TourismContent tourismContent = tourismContentRepository.findById(request.getTourismContentId())
                .orElseThrow(() -> new ReviewException(TOURISM_CONTENT_NOT_FOUND));
        Optional<Landmark> landmark = landmarkService.findByTourismContentId(
                tourismContent.getTourismContentId()
        );
        Optional<Attraction> attraction = attractionRepository.findByTourismContentTourismContentId(
                tourismContent.getTourismContentId()
        );

        if (landmark.isEmpty() && attraction.isEmpty()) {
            throw new ReviewException(UNSUPPORTED_VISIT_CONTENT);
        }

        boolean firstVisit = landmark
                .map(value -> !landmarkService.hasVisited(usersId, value.getLandmarkId()))
                .orElseGet(() -> !attractionVisitLogService.hasVisited(
                        usersId, attraction.orElseThrow().getAttractionId()
                ));

        Region region = tourismContent.getRegion();
        if (!region.getLegalRegionCode().equals(request.getLegalRegionCode())
                || !region.getLegalDistrictCode().equals(request.getLegalDistrictCode())) {
            throw new ReviewException(ReviewErrorCode.REGION_CODE_MISMATCH);
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

        if (firstVisit) {
            statsService.addXpAndScore(usersId, REVIEW_XP, reviewPoint);
        }

        String logContent = tourismContent.getTitle() + " 방문 인증";
        reviewLogService.createLog(review.getReviewId(), logContent, reviewPoint);

        if (landmark.isPresent()) {
            Landmark visitedLandmark = landmark.get();
            landmarkService.saveVisitLog(usersId, visitedLandmark.getLandmarkId());
            landmarkService.acquireCard(usersId, visitedLandmark.getLandmarkId());
        } else {
            attractionVisitLogService.createLog(usersId, attraction.orElseThrow().getAttractionId());
        }

        regionService.recordRegionVisit(usersId, region.getRegionId());

        String contentType = landmark.isPresent() ? "LANDMARK" : "ATTRACTION";
        missionAchievementService.evaluateVisit(usersId, contentType, firstVisit);
        missionAchievementService.evaluateRegion(usersId);
        if (!reviewTitle.isBlank() || !reviewContent.isBlank()) {
            missionAchievementService.evaluateReview(usersId);
        }

        return CreateReviewResponse.toDto();
    }
}
