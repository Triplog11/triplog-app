package triplog.backend.bookmark.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 북마크 관련 요청 데이터를 전달하기 위한 DTO입니다.
 */
@Schema(description = "북마크 관련 요청 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookmarkRequest {

    /**
     * 북마크 등록 요청 DTO입니다.
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "북마크 등록 요청")
    public static class CreateRequest {

        @Schema(description = "저장 대상 타입", example = "REGION")
        @NotNull(message = "북마크 타입은 필수입니다.")
        private String bookmarkType;

        @Schema(description = "저장 대상 식별자", example = "101")
        @NotNull(message = "북마크 대상 식별자는 필수입니다.")
        private Long bookmarkIdentifier;
    }
}
