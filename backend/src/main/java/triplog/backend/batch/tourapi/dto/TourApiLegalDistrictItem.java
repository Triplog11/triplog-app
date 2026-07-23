package triplog.backend.batch.tourapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * TourAPI 법정동 코드 조회 결과의 지역 코드와 명칭입니다.
 *
 * @param code 법정동 시도 또는 시군구 코드
 * @param name 법정동 시도 또는 시군구 명칭
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiLegalDistrictItem(String code, String name) {
}
