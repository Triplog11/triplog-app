package triplog.backend.batch.tourapi.dto;

import java.util.List;

/**
 * TourAPI 목록 응답의 항목과 페이지 정보를 함께 전달합니다.
 *
 * @param items 현재 페이지 항목
 * @param pageNumber 현재 페이지 번호
 * @param pageSize 페이지 크기
 * @param totalCount 전체 항목 수
 * @param <T> 목록 항목 타입
 */
public record TourApiPage<T>(
        List<T> items,
        int pageNumber,
        int pageSize,
        int totalCount
) {

    /**
     * 현재 응답이 전체 목록의 마지막 페이지인지 확인합니다.
     *
     * @return 마지막 페이지이면 true
     */
    public boolean isLastPage() {
        return (long) pageNumber * pageSize >= totalCount;
    }
}
