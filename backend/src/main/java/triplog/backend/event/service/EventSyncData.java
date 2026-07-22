package triplog.backend.event.service;

import java.time.LocalDate;
import java.util.Map;

/**
 * TourAPI 축제 소개정보를 Event 저장 값으로 변환한 입력 데이터입니다.
 *
 * @param eventStartDate 행사 시작일
 * @param eventEndDate 행사 종료일
 * @param eventPlace 행사 장소
 * @param playTime 공연 시간
 * @param ageLimit 관람 가능 연령
 * @param usageFee 이용 요금
 * @param sponsorName 주최자명
 * @param sponsorTelephone 주최자 연락처
 * @param progressType 진행 상태
 * @param festivalType 축제 진행 형태
 * @param detailData 정형 컬럼에 포함되지 않은 상세정보
 */
public record EventSyncData(
        LocalDate eventStartDate,
        LocalDate eventEndDate,
        String eventPlace,
        String playTime,
        String ageLimit,
        String usageFee,
        String sponsorName,
        String sponsorTelephone,
        String progressType,
        String festivalType,
        Map<String, Object> detailData
) {
}
