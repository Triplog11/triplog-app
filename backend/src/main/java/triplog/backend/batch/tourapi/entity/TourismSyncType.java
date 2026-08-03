package triplog.backend.batch.tourapi.entity;

/**
 * TourAPI 동기화 실패가 발생한 작업 유형입니다.
 */
public enum TourismSyncType {

    /** 법정동 기반 지역 정보 동기화입니다. */
    REGION,

    /** CSV 관리 대상을 기준으로 한 랜드마크 동기화입니다. */
    LANDMARK,

    /** CSV 관리 대상을 기준으로 한 일반 관광지 동기화입니다. */
    ATTRACTION,

    /** 지정된 조회 기간에 포함되는 축제 동기화입니다. */
    FESTIVAL,

    /** 관광 콘텐츠에 연결되는 이미지 동기화입니다. */
    IMAGE
}
