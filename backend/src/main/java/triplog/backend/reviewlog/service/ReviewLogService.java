package triplog.backend.reviewlog.service;

import java.time.LocalDateTime;

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

    /**
     * 사용자의 유효한 전체 여행 기록 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 유효한 여행 기록 수
     */
    long countTravelRecords(String usersId);

    /** 사용자의 사진 포함 유효 여행 기록 수를 조회합니다. */
    long countPhotoTravelRecords(String usersId);

    /**
     * 지정 기간에 작성된 여행 기록 수를 이미지 조건에 따라 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param start 집계 시작 시각
     * @param end 집계 종료 시각
     * @param imageRequired 이미지 필수 여부
     * @return 조건에 맞는 여행 기록 수
     */
    long countTravelRecords(
            String usersId, LocalDateTime start, LocalDateTime end, boolean imageRequired
    );
}
