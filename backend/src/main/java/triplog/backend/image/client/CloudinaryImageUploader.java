package triplog.backend.image.client;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.image.config.ImageUploadProperties;
import triplog.backend.image.exception.ImageErrorCode;
import triplog.backend.image.exception.ImageException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static triplog.backend.image.exception.ImageErrorCode.IMAGE_UPLOAD_FAILED;

/**
 * Cloudinary Upload API를 호출하고 SDK 예외를 이미지 도메인 예외로 변환하는 컴포넌트입니다.
 */
@Component
@RequiredArgsConstructor
public class CloudinaryImageUploader {

    private static final String SECURE_URL = "secure_url";
    private static final String IMAGE_FOLDER = "triplog/images";
    private final Cloudinary cloudinary;
    private final ImageUploadProperties imageUploadProperties;

    /**
     * 이미지 파일을 Cloudinary에 업로드하고 HTTPS 이미지 URL을 반환합니다.
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드된 이미지의 HTTPS URL
     * @throws ImageException Cloudinary 호출에 실패하거나 응답에 이미지 URL이 없는 경우
     */
    public String upload(MultipartFile file) {
        Map<String, Object> uploadOptions = new HashMap<>();
        uploadOptions.put("resource_type", "image");
        uploadOptions.put("folder", IMAGE_FOLDER);
        if (imageUploadProperties.jpegConversionContentTypes().contains(file.getContentType())) {
            uploadOptions.put("format", "jpg");
        }

        Map<?, ?> uploadResult;
        try {
            uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    uploadOptions
            );
        } catch (IOException e) {
            throw new ImageException(IMAGE_UPLOAD_FAILED, e);
        }

        Object secureUrl = uploadResult.get(SECURE_URL);
        if (secureUrl == null || secureUrl.toString().isBlank()) {
            throw new ImageException(IMAGE_UPLOAD_FAILED);
        }

        return secureUrl.toString();
    }
}
