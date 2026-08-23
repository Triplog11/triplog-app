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
import triplog.backend.reviewlog.service.ReviewLogService;
import triplog.backend.stats.service.ActivityRewardInfo;
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
    private static final int LEVEL_DISPLAY_ORDER = 30;

    private final ReviewService reviewService;
    private final LandmarkService landmarkService;
    private final ImageService imageService;
    private final StatsService statsService;
    private final ReviewLogService reviewLogService;
    private final RegionService regionService;
    private final TourismContentService tourismContentService;
    private final AttractionService attractionService;
    private final AttractionVisitLogService attractionVisitLogService;
    private final MissionAchievementService missionAchievementService;
    private final ActivityHistoryService activityHistoryService;

    /**
     * 방문 인증을 생성하고 관련 방문·보상·미션·활동 히스토리를 함께 처리합니다.
     *
     * @param usersId 사용자 식별자
     * @param request 방문 인증 요청
     * @param files 첨부 이미지 목록
     * @return 방문 인증 결과
     */
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

        boolean cardAcquired = recordVisits(
                usersId, landmark, attraction, region.getRegionId()
        );
        boolean travelRecord = hasTravelRecord(request);
        List<String> policyIds = determinePolicyIds(
                usersId,
                landmark.isPresent(),
                firstVisit,
                firstRegionVisit,
                travelRecord,
                hasImages
        );

        ActivityRewardResult reward = statsService.applyActivityPolicies(
                usersId, policyIds.toArray(String[]::new)
        );
        reviewService.updateRewardScore(review.getReviewId(), reward.totalScore());
        int reviewXp = reward.rewards().stream()
                .filter(item -> item.policyId().startsWith("REVIEW_"))
                .mapToInt(ActivityRewardInfo::xp)
                .sum();
        reviewLogService.createLog(
                review.getReviewId(), content.getTitle() + " 방문 인증", reviewXp
        );

        recordActivityHistory(
                usersId,
                review.getReviewId(),
                content,
                landmark,
                region,
                firstVisit,
                firstRegionVisit,
                cardAcquired,
                reward
        );
        evaluateMissions(
                usersId, landmark.isPresent(), firstVisit, travelRecord
        );

        return CreateReviewResponse.toDto(
                reward.rewards(), reward.totalXp(), reward.totalScore()
        );
    }

    private void validateRegionCodes(CreateRequest request, Region region) {
        if (!region.getLegalRegionCode().equals(request.getLegalRegionCode())
                || !region.getLegalDistrictCode().equals(request.getLegalDistrictCode())) {
            throw new ReviewException(ReviewErrorCode.REGION_CODE_MISMATCH);
        }
    }

    private boolean recordVisits(
            String usersId,
            Optional<Landmark> landmark,
            Optional<Attraction> attraction,
            Long regionId
    ) {
        boolean cardAcquired = false;
        if (landmark.isPresent()) {
            Long landmarkId = landmark.get().getLandmarkId();
            landmarkService.saveVisitLog(usersId, landmarkId);
            cardAcquired = landmarkService.acquireCard(usersId, landmarkId);
        } else {
            attractionVisitLogService.createLog(
                    usersId, attraction.orElseThrow().getAttractionId()
            );
        }
        regionService.recordRegionVisit(usersId, regionId);
        return cardAcquired;
    }

    private boolean hasTravelRecord(CreateRequest request) {
        String title = request.getReviewTitle() == null ? "" : request.getReviewTitle();
        String body = request.getReviewContent() == null ? "" : request.getReviewContent();
        return !title.isBlank() || !body.isBlank();
    }

    private List<String> determinePolicyIds(
            String usersId,
            boolean landmark,
            boolean firstVisit,
            boolean firstRegionVisit,
            boolean travelRecord,
            boolean hasImages
    ) {
        List<String> policyIds = new ArrayList<>();
        if (firstVisit) {
            policyIds.add(landmark ? "LANDMARK_FIRST_VISIT" : "ATTRACTION_FIRST_VISIT");
        }
        if (firstRegionVisit) {
            policyIds.add("REGION_FIRST_VISIT");
        }
        if (travelRecord && reviewLogService.countRewardedTravelRecordsToday(usersId) < 3) {
            policyIds.add("REVIEW_CREATE");
            if (hasImages) {
                policyIds.add("REVIEW_IMAGE_BONUS");
            }
        }
        return policyIds;
    }

    private void recordActivityHistory(
            String usersId,
            Long reviewId,
            TourismContent content,
            Optional<Landmark> landmark,
            Region region,
            boolean firstVisit,
            boolean firstRegionVisit,
            boolean cardAcquired,
            ActivityRewardResult reward
    ) {
        LocalDateTime occurredAt = LocalDateTime.now();
        String sourceId = reviewId.toString();

        if (landmark.isPresent() && firstVisit && cardAcquired) {
            ActivityRewardInfo landmarkReward = findReward(reward, "LANDMARK_FIRST_VISIT");
            activityHistoryService.record(new ActivityHistoryRecord(
                    usersId,
                    "LANDMARK",
                    REVIEW_SOURCE_TYPE,
                    sourceId,
                    eventKey(reviewId, "LANDMARK"),
                    landmarkTitle(landmark.get(), content),
                    landmarkReward.description(),
                    landmarkReward.xp(),
                    landmarkReward.score(),
                    LANDMARK_DISPLAY_ORDER,
                    occurredAt
            ));
        }

        if (firstRegionVisit) {
            ActivityRewardInfo regionReward = findReward(reward, "REGION_FIRST_VISIT");
            activityHistoryService.record(new ActivityHistoryRecord(
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
            ));
        }

        if (reward.levelUp()) {
            activityHistoryService.record(new ActivityHistoryRecord(
                    usersId,
                    "LEVEL",
                    REVIEW_SOURCE_TYPE,
                    sourceId,
                    eventKey(reviewId, "LEVEL"),
                    "레벨 " + reward.currentLevel() + " 달성",
                    "레벨이 상승했습니다.",
                    0,
                    0,
                    LEVEL_DISPLAY_ORDER,
                    occurredAt
            ));
        }
    }

    private ActivityRewardInfo findReward(ActivityRewardResult reward, String policyId) {
        return reward.rewards().stream()
                .filter(item -> item.policyId().equals(policyId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Applied reward was not returned: " + policyId
                ));
    }

    private String landmarkTitle(Landmark landmark, TourismContent content) {
        String name = landmark.getLandmarkName();
        return (name == null || name.isBlank() ? content.getTitle() : name) + " 카드 획득";
    }

    private String eventKey(Long reviewId, String activityType) {
        return REVIEW_SOURCE_TYPE + ":" + reviewId + ":" + activityType;
    }

    private void evaluateMissions(
            String usersId, boolean landmark, boolean firstVisit, boolean travelRecord
    ) {
        missionAchievementService.evaluateVisit(
                usersId, landmark ? "LANDMARK" : "ATTRACTION", firstVisit
        );
        missionAchievementService.evaluateRegion(usersId);
        if (travelRecord) {
            missionAchievementService.evaluateReview(usersId);
        }
    }
}
