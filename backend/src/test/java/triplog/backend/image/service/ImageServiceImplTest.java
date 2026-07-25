package triplog.backend.image.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import triplog.backend.image.client.CloudinaryImageUploader;
import triplog.backend.image.config.ImageUploadProperties;
import triplog.backend.image.dto.response.ImageResponse.ImageUploadResponse;
import triplog.backend.image.exception.ImageErrorCode;
import triplog.backend.image.exception.ImageException;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * {@link ImageServiceImpl}의 이미지 검증 및 다중 업로드 흐름을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private CloudinaryImageUploader cloudinaryImageUploader;

    private ImageService imageService;

    @BeforeEach
    void setUp() {
        ImageUploadProperties imageUploadProperties = new ImageUploadProperties(Set.of(
                "image/jpeg",
                "image/png",
                "image/heic",
                "image/heif"
        ), Set.of(
                "image/heic",
                "image/heif"
        ), 10);
        imageService = new ImageServiceImpl(cloudinaryImageUploader, imageUploadProperties);
    }

    /**
     * 이미지 한 장을 업로드하면 Cloudinary URL 한 개를 응답하는지 검증합니다.
     */
    @Test
    @DisplayName("단일 이미지 업로드 시 이미지 URL을 반환한다")
    void uploadsImageAndReturnsSecureUrl() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", "image".getBytes());
        String secureUrl = "https://res.cloudinary.com/demo/image/upload/image.png";
        when(cloudinaryImageUploader.upload(file)).thenReturn(secureUrl);

        // when
        ImageUploadResponse response = imageService.upload(List.of(file));

        // then
        assertThat(response.getImageUrls()).containsExactly(secureUrl);
    }

    /**
     * 내용이 없는 이미지 파일을 전달하면 잘못된 이미지 파일 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("빈 이미지 파일을 업로드하면 예외가 발생한다")
    void rejectsEmptyFile() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", new byte[0]);

        // when
        // then
        assertThatThrownBy(() -> imageService.upload(List.of(file)))
                .isInstanceOf(ImageException.class)
                .extracting("errorCode")
                .isEqualTo(ImageErrorCode.INVALID_IMAGE_FILE);
    }

    /**
     * 허용되지 않은 MIME 타입의 파일을 전달하면 잘못된 이미지 파일 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("이미지가 아닌 파일을 업로드하면 예외가 발생한다")
    void rejectsNonImageFile() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.txt", "text/plain", "text".getBytes());

        // when
        // then
        assertThatThrownBy(() -> imageService.upload(List.of(file)))
                .isInstanceOf(ImageException.class)
                .extracting("errorCode")
                .isEqualTo(ImageErrorCode.INVALID_IMAGE_FILE);
    }

    /**
     * 여러 이미지를 업로드하면 요청 순서대로 모든 이미지 URL을 응답하는지 검증합니다.
     */
    @Test
    @DisplayName("다중 이미지 업로드 시 요청 순서대로 URL 목록을 반환한다")
    void uploadsMultipleImagesAndReturnsUrlsInOrder() {
        // given
        MockMultipartFile firstFile = new MockMultipartFile(
                "files", "first.jpg", "image/jpeg", "first".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile(
                "files", "second.heic", "image/heic", "second".getBytes());
        String firstUrl = "https://res.cloudinary.com/demo/image/upload/first.jpg";
        String secondUrl = "https://res.cloudinary.com/demo/image/upload/second.heic";

        when(cloudinaryImageUploader.upload(firstFile)).thenReturn(firstUrl);
        when(cloudinaryImageUploader.upload(secondFile)).thenReturn(secondUrl);

        // when
        ImageUploadResponse response = imageService.upload(List.of(firstFile, secondFile));

        // then
        assertThat(response.getImageUrls()).containsExactly(firstUrl, secondUrl);
    }
}
