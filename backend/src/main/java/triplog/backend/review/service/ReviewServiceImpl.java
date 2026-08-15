package triplog.backend.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.attraction.entity.Attraction;
import triplog.backend.attraction.service.AttractionService;
import triplog.backend.attractionvisitlog.service.AttractionVisitLogService;
import triplog.backend.image.service.ImageService;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.mission.service.MissionAchievementService;
import triplog.backend.region.entity.Region;
import triplog.backend.region.service.RegionService;
import triplog.backend.review.dto.request.ReviewRequest.CreateRequest;
import triplog.backend.review.dto.response.ReviewResponse.CreateReviewResponse;
import triplog.backend.review.entity.Review;
import triplog.backend.review.exception.ReviewErrorCode;
import triplog.backend.review.exception.ReviewException;
import triplog.backend.review.repository.ReviewRepository;
import triplog.backend.reviewlog.service.ReviewLogService;
import triplog.backend.stats.service.ActivityRewardResult;
import triplog.backend.stats.service.StatsService;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.service.TourismContentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static triplog.backend.review.exception.ReviewErrorCode.TOURISM_CONTENT_NOT_FOUND;
import static triplog.backend.review.exception.ReviewErrorCode.UNSUPPORTED_VISIT_CONTENT;

/**
 * 방문 인증과 여행 기록 등록 및 정책 보상 처리를 담당하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final LandmarkService landmarkService;
    private final ImageService imageService;
    private final StatsService statsService;
    private final ReviewLogService reviewLogService;
    private final RegionService regionService;
    private final TourismContentService tourismContentService;
    private final AttractionService attractionService;
    private final AttractionVisitLogService attractionVisitLogService;
    private final MissionAchievementService missionAchievementService;

    /**
     * 관광 콘텐츠와 지역 코드를 검증하고 서버가 판정한 활동 정책 보상을 지급합니다.
     *
     * @param usersId 사용자 식별자
     * @param request 방문 인증 및 여행 기록 요청
     * @param files   첨부 이미지 목록
     * @return 인증 결과와 정책별 보상 내역
     */
    @Override
    @Transactional
    public CreateReviewResponse createReview(
            String usersId, CreateRequest request, List<MultipartFile> files
    ) {
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
        if (!region.getLegalRegionCode().equals(request.getLegalRegionCode())
                || !region.getLegalDistrictCode().equals(request.getLegalDistrictCode())) {
            throw new ReviewException(ReviewErrorCode.REGION_CODE_MISMATCH);
        }

        boolean firstVisit = landmark
                .map(value -> !landmarkService.hasVisited(usersId, value.getLandmarkId()))
                .orElseGet(() -> !attractionVisitLogService.hasVisited(
                        usersId, attraction.orElseThrow().getAttractionId()
                ));
        boolean firstRegionVisit = !regionService.hasVisited(usersId, region.getRegionId());
        Review review = reviewRepository.save(request.toEntity(usersId, content));
        String title = request.getReviewTitle() == null ? "" : request.getReviewTitle();
        String body = request.getReviewContent() == null ? "" : request.getReviewContent();
        boolean hasImages = files != null && !files.isEmpty();
        if (hasImages) {
            imageService.uploadAndSave(review.getReviewId(), files);
        }

        if (landmark.isPresent()) {
            landmarkService.saveVisitLog(usersId, landmark.get().getLandmarkId());
            landmarkService.acquireCard(usersId, landmark.get().getLandmarkId());
        } else {
            attractionVisitLogService.createLog(usersId, attraction.orElseThrow().getAttractionId());
        }
        regionService.recordRegionVisit(usersId, region.getRegionId());

        List<String> policyIds = new ArrayList<>();
        if (firstVisit) {
            policyIds.add(landmark.isPresent() ? "LANDMARK_FIRST_VISIT" : "ATTRACTION_FIRST_VISIT");
        }
        if (firstRegionVisit) {
            policyIds.add("REGION_FIRST_VISIT");
        }
        boolean travelRecord = !title.isBlank() || !body.isBlank();
        if (travelRecord && reviewLogService.countRewardedTravelRecordsToday(usersId) < 3) {
            policyIds.add("REVIEW_CREATE");
            if (hasImages) {
                policyIds.add("REVIEW_IMAGE_BONUS");
            }
        }

        ActivityRewardResult reward = statsService.applyActivityPolicies(
                usersId, policyIds.toArray(String[]::new)
        );
        reviewRepository.updateRewardScore(review.getReviewId(), reward.totalScore());
        int reviewXp = reward.rewards().stream()
                .filter(item -> item.policyId().startsWith("REVIEW_"))
                .mapToInt(item -> item.xp())
                .sum();
        reviewLogService.createLog(review.getReviewId(), content.getTitle() + " 방문 인증", reviewXp);

        String contentType = landmark.isPresent() ? "LANDMARK" : "ATTRACTION";
        missionAchievementService.evaluateVisit(usersId, contentType, firstVisit);
        missionAchievementService.evaluateRegion(usersId);
        if (travelRecord) {
            missionAchievementService.evaluateReview(usersId);
        }

        return CreateReviewResponse.toDto(
                reward.rewards(), reward.totalXp(), reward.totalScore()
        );
    }
}
