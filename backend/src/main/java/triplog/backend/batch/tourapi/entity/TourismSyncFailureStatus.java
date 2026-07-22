package triplog.backend.batch.tourapi.entity;

/**
 * TourAPI 동기화 실패 이력의 재처리 상태입니다.
 */
public enum TourismSyncFailureStatus {
    PENDING,
    RETRYING,
    RESOLVED
}
