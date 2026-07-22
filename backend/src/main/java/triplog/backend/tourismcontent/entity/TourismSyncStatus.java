package triplog.backend.tourismcontent.entity;

/**
 * 관광 콘텐츠의 동기화 처리 상태입니다.
 */
public enum TourismSyncStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    INACTIVE_CANDIDATE
}
