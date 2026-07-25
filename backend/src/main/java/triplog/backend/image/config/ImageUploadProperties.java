package triplog.backend.image.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * 이미지 업로드 요청 검증에 사용하는 설정입니다.
 *
 * @param allowedContentTypes 업로드를 허용할 이미지 MIME 타입 목록
 * @param jpegConversionContentTypes JPEG로 변환하여 업로드할 이미지 MIME 타입 목록
 * @param maxFileCount 한 요청에서 업로드할 수 있는 최대 이미지 개수
 */
@ConfigurationProperties(prefix = "cloudinary.upload")
public record ImageUploadProperties(
        Set<String> allowedContentTypes,
        Set<String> jpegConversionContentTypes,
        int maxFileCount
) {
}
