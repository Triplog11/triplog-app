package triplog.backend.reviewlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.reviewlog.entity.ReviewLog;
import triplog.backend.reviewlog.repository.ReviewLogRepository;

/**
 * {@link ReviewLogService}의 기본 구현체입니다.
 */
@Service
@RequiredArgsConstructor
public class ReviewLogServiceImpl implements ReviewLogService {

    private final ReviewLogRepository reviewLogRepository;

    /**
     * 리뷰 로그를 저장합니다.
     *
     * @param reviewId   리뷰 식별자
     * @param logContent 로그 내용
     * @param gainXp     받은 경험치
     */
    @Override
    @Transactional
    public void createLog(Long reviewId, String logContent, int gainXp) {
        ReviewLog reviewLog = new ReviewLog(reviewId, logContent, gainXp);
        reviewLogRepository.save(reviewLog);
    }
}
