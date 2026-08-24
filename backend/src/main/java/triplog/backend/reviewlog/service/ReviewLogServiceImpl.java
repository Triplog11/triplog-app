package triplog.backend.reviewlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.reviewlog.entity.ReviewLog;
import triplog.backend.reviewlog.repository.ReviewLogRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    /**
     * 오늘 생성된 보상 대상 여행 기록의 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 오늘의 보상 대상 여행 기록 수
     */
    @Override
    @Transactional(readOnly = true)
    public long countRewardedTravelRecordsToday(String usersId) {
        LocalDate today = LocalDate.now();
        return reviewLogRepository.countTravelRecords(
                usersId,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay().minusNanos(1),
                false
        );
    }

    /**
     * 사용자의 유효한 전체 여행 기록 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 유효한 여행 기록 수
     */
    @Override
    @Transactional(readOnly = true)
    public long countTravelRecords(String usersId) {
        return reviewLogRepository.countTravelRecords(usersId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPhotoTravelRecords(String usersId) {
        return reviewLogRepository.countPhotoTravelRecords(usersId);
    }

    /**
     * 지정 기간에 작성된 여행 기록 수를 이미지 조건에 따라 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param start 집계 시작 시각
     * @param end 집계 종료 시각
     * @param imageRequired 이미지 필수 여부
     * @return 조건에 맞는 여행 기록 수
     */
    @Override
    @Transactional(readOnly = true)
    public long countTravelRecords(
            String usersId, LocalDateTime start, LocalDateTime end, boolean imageRequired
    ) {
        return reviewLogRepository.countTravelRecords(usersId, start, end, imageRequired);
    }
}
