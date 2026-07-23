package triplog.backend.tourismcontent.entity;

/**
 * 관광 콘텐츠의 동기화 처리 상태입니다.
 */
public enum TourismSyncStatus {

    /** 아직 동기화를 시작하지 않은 대기 상태입니다. */
    PENDING,

    /** 외부 데이터를 조회하고 반영하는 중인 상태입니다. */
    PROCESSING,

    /** 외부 데이터 동기화를 정상적으로 완료한 상태입니다. */
    COMPLETED,

    /** 외부 데이터 동기화에 실패한 상태입니다. */
    FAILED,

    /** 외부 목록에서 반복해서 누락되어 비활성화 여부를 확인하는 상태입니다. */
    INACTIVE_CANDIDATE
}
