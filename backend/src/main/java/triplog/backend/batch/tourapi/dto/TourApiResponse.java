package triplog.backend.batch.tourapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.JsonNode;

/**
 * TourAPI의 공통 JSON 응답 구조입니다.
 *
 * @param response 응답 본문
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiResponse(Response response) {

    /**
     * TourAPI 응답의 header와 body입니다.
     *
     * @param header 처리 결과
     * @param body 조회 결과
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(Header header, Body body) {
    }

    /**
     * TourAPI 처리 결과 코드입니다.
     *
     * @param resultCode 결과 코드
     * @param resultMsg 결과 메시지
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Header(String resultCode, String resultMsg) {
    }

    /**
     * TourAPI 조회 결과와 페이지 정보입니다.
     *
     * @param items 조회 항목 컨테이너
     * @param numOfRows 페이지 크기
     * @param pageNo 페이지 번호
     * @param totalCount 전체 건수
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(JsonNode items, Integer numOfRows, Integer pageNo, Integer totalCount) {
    }
}
