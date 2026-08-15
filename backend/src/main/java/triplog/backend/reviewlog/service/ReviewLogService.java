package triplog.backend.reviewlog.service;

/**
 * 리뷰 로그 생성 기능을 정의하는 도메인 서비스입니다.
 */
public interface ReviewLogService {

    /**
     * 리뷰 로그를 저장합니다.
     *
     * @param reviewId   리뷰 식별자
     * @param logContent 로그 내용
     * @param gainXp     받은 경험치
     */
    void createLog(Long reviewId, String logContent, int gainXp);

    /**
     * 오늘 XP 보상 대상이 된 여행 기록 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 오늘 작성한 보상 대상 여행 기록 수
     */
    long countRewardedTravelRecordsToday(String usersId);
}
