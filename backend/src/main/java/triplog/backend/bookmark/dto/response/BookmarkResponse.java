package triplog.backend.bookmark.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.bookmark.entity.Bookmark;
import java.util.List;

/**
 * 북마크 관련 응답 데이터를 전달하기 위한 DTO입니다.
 */
@Schema(description = "북마크 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookmarkResponse {

    /**
     * 북마크 해제 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "북마크 해제 응답")
    public static class DeleteResponse {

        @Schema(description = "북마크 해제 처리 여부", example = "true")
        private Boolean isDeleted;

        /**
         * 북마크 해제 응답을 생성합니다.
         *
         * @param isDeleted 해제 처리 여부
         * @return 북마크 해제 응답 DTO
         */
        public static DeleteResponse toDto(Boolean isDeleted) {
            return new DeleteResponse(isDeleted);
        }
    }

    /**
     * 북마크 등록 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "북마크 등록 응답")
    public static class CreateResponse {

        @Schema(description = "북마크 ID", example = "9001")
        private Long bookmarkId;

        @Schema(description = "저장 대상 타입", example = "REGION")
        private String bookmarkType;

        @Schema(description = "저장 대상 식별자", example = "101")
        private Long bookmarkIdentifier;

        @Schema(description = "저장 여부", example = "true")
        private Boolean bookmarked;

        /**
         * 저장된 북마크 엔티티로부터 응답 DTO를 생성합니다.
         *
         * @param bookmark 저장된 북마크 엔티티
         * @return 북마크 등록 응답 DTO
         */
        public static CreateResponse toDto(Bookmark bookmark) {
            return new CreateResponse(
                    bookmark.getBookmarkId(),
                    bookmark.getBookmarkType().name(),
                    bookmark.getBookmarkIdentifier(),
                    true
            );
        }
    }

    /**
     * 북마크 이벤트 목록 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "북마크 이벤트 목록 응답")
    public static class EventListResponse {

        @Schema(description = "현재 페이지", example = "0")
        private Integer page;

        @Schema(description = "페이지 크기", example = "10")
        private Integer size;

        @Schema(description = "전체 저장 이벤트 수", example = "4")
        private Long totalElements;

        @Schema(description = "전체 페이지 수", example = "1")
        private Integer totalPages;

        @Schema(description = "저장 이벤트 목록")
        private List<EventBookmarkEntry> bookmarks;
    }

    /**
     * 북마크 이벤트 항목 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "북마크 이벤트 항목")
    public static class EventBookmarkEntry {

        @Schema(description = "북마크 ID", example = "9101")
        private Long bookmarkId;

        @Schema(description = "이벤트 ID", example = "201")
        private Long eventId;

        @Schema(description = "이벤트 제목", example = "수원 문화 행사")
        private String eventTitle;

        @Schema(description = "이벤트 이미지 URL", example = "event-main.png")
        private String eventImageUrl;

        @Schema(description = "시작 일시", example = "2026-07-01T10:00:00", nullable = true)
        private String eventStart;

        @Schema(description = "종료 일시", example = "2026-07-07T18:00:00", nullable = true)
        private String eventEnd;
    }

    /**
     * 북마크 랜드마크 목록 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "북마크 랜드마크 목록 응답")
    public static class LandmarkListResponse {

        @Schema(description = "현재 페이지", example = "0")
        private Integer page;

        @Schema(description = "페이지 크기", example = "10")
        private Integer size;

        @Schema(description = "전체 저장 장소 수", example = "12")
        private Long totalElements;

        @Schema(description = "전체 페이지 수", example = "2")
        private Integer totalPages;

        @Schema(description = "저장 장소 목록")
        private List<LandmarkBookmarkEntry> bookmarks;
    }

    /**
     * 북마크 랜드마크 항목 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "북마크 랜드마크 항목")
    public static class LandmarkBookmarkEntry {

        @Schema(description = "북마크 ID", example = "9011")
        private Long bookmarkId;

        @Schema(description = "랜드마크 ID", example = "301")
        private Long landmarkId;

        @Schema(description = "랜드마크명", example = "수원화성")
        private String landmarkName;

        @Schema(description = "지역 ID", example = "101")
        private Long regionId;

        @Schema(description = "지역명", example = "수원시", nullable = true)
        private String regionName;

        @Schema(description = "Tour API 식별자", example = "TOUR-10001")
        private String contentId;
    }

    /**
     * 북마크 지역 목록 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "북마크 지역 목록 응답")
    public static class RegionListResponse {

        @Schema(description = "현재 페이지", example = "0")
        private Integer page;

        @Schema(description = "페이지 크기", example = "10")
        private Integer size;

        @Schema(description = "전체 저장 지역 수", example = "3")
        private Long totalElements;

        @Schema(description = "전체 페이지 수", example = "1")
        private Integer totalPages;

        @Schema(description = "저장 지역 목록")
        private List<RegionBookmarkEntry> bookmarks;
    }

    /**
     * 북마크 지역 항목 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "북마크 지역 항목")
    public static class RegionBookmarkEntry {

        @Schema(description = "북마크 ID", example = "9201")
        private Long bookmarkId;

        @Schema(description = "지역 ID", example = "101")
        private Long regionId;

        @Schema(description = "지역명", example = "서울특별시 강남구")
        private String regionName;
    }
}
