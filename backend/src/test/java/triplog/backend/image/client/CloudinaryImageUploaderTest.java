package triplog.backend.image.client;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import triplog.backend.image.config.ImageUploadProperties;
import triplog.backend.image.exception.ImageErrorCode;
import triplog.backend.image.exception.ImageException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link CloudinaryImageUploader}의 SDK 호출 및 예외 변환을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class CloudinaryImageUploaderTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    private CloudinaryImageUploader cloudinaryImageUploader;

    @BeforeEach
    void setUp() {
        ImageUploadProperties imageUploadProperties = new ImageUploadProperties(
                Set.of("image/jpeg", "image/png", "image/heic", "image/heif"),
                Set.of("image/heic", "image/heif"),
                10
        );
        cloudinaryImageUploader = new CloudinaryImageUploader(cloudinary, imageUploadProperties);
    }

    /**
     * Cloudinary 업로드 성공 응답의 secure_url을 반환하는지 검증합니다.
     *
     * @throws IOException mock SDK 업로드 메서드가 선언하는 checked exception
     */
    @Test
    @DisplayName("Cloudinary 업로드 성공 시 secure URL을 반환한다")
    void uploadsImageAndReturnsSecureUrl() throws IOException {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", "image".getBytes());
        String secureUrl = "https://res.cloudinary.com/demo/image/upload/image.png";

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("secure_url", secureUrl));

        // when
        String result = cloudinaryImageUploader.upload(file);

        // then
        assertThat(result).isEqualTo(secureUrl);
    }

    /**
     * HEIC 파일 업로드 시 Cloudinary에 JPEG 변환 옵션을 전달하고 JPEG URL을 반환하는지 검증합니다.
     *
     * @throws IOException mock SDK 업로드 메서드가 선언하는 checked exception
     */
    @Test
    @DisplayName("HEIC 업로드 시 JPEG 형식으로 변환한다")
    void convertsHeicImageToJpeg() throws IOException {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.heic", "image/heic", "image".getBytes());
        String secureUrl = "https://res.cloudinary.com/demo/image/upload/image.jpg";

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("secure_url", secureUrl));

        // when
        String result = cloudinaryImageUploader.upload(file);

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> optionsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(uploader).upload(any(byte[].class), optionsCaptor.capture());
        assertThat(optionsCaptor.getValue()).containsEntry("format", "jpg");
        assertThat(result).isEqualTo(secureUrl);
    }

    /**
     * Cloudinary SDK에서 IOException이 발생하면 이미지 업로드 실패 예외로 변환되는지 검증합니다.
     *
     * @throws IOException mock SDK 업로드 메서드가 선언하는 checked exception
     */
    @Test
    @DisplayName("Cloudinary SDK 호출 실패 시 이미지 도메인 예외로 변환한다")
    void convertsCloudinaryFailureToImageException() throws IOException {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", "image".getBytes());

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenThrow(new IOException("upload failed"));

        // when
        // then
        assertThatThrownBy(() -> cloudinaryImageUploader.upload(file))
                .isInstanceOf(ImageException.class)
                .extracting("errorCode")
                .isEqualTo(ImageErrorCode.IMAGE_UPLOAD_FAILED);
    }
}
