package triplog.backend.image.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 이미지 관련 응답 DTO를 그룹화하는 클래스입니다.
 */
@Schema(description = "이미지 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageResponse {

    /**
     * Cloudinary 이미지 업로드 결과를 반환하는 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "이미지 업로드 응답")
    public static class ImageUploadResponse {

        /**
         * Cloudinary에 업로드된 이미지의 HTTPS URL 목록입니다.
         */
        @Schema(
                description = "업로드된 이미지 URL 목록",
                example = "[\"https://res.cloudinary.com/demo/image/upload/sample1.png\","
                        + "\"https://res.cloudinary.com/demo/image/upload/sample2.png\"]"
        )
        private List<String> imageUrls;

        /**
         * 업로드된 이미지 URL 목록을 기반으로 응답 DTO를 생성합니다.
         *
         * @param imageUrls 업로드된 이미지의 HTTPS URL 목록
         * @return 이미지 업로드 응답 DTO
         */
        public static ImageUploadResponse toDto(List<String> imageUrls) {
            return new ImageUploadResponse(imageUrls);
        }
    }
}
