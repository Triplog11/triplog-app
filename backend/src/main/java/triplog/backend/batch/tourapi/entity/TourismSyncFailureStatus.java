package triplog.backend.batch.tourapi.entity;

/**
 * TourAPI 동기화 실패 이력의 재처리 상태입니다.
 */
public enum TourismSyncFailureStatus {

    /** 재시도 대상으로 등록되어 처리를 기다리는 상태입니다. */
    PENDING,

    /** 실패 이력을 현재 재처리하고 있는 상태입니다. */
    RETRYING,

    /** 재처리에 성공해 실패 원인이 해소된 상태입니다. */
    RESOLVED
}
