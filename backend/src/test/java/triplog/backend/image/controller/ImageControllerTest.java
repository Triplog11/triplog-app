package triplog.backend.image.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.image.dto.response.ImageResponse.ImageUploadResponse;
import triplog.backend.image.service.ImageService;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link ImageController}의 multipart 다중 파일 바인딩과 응답 형식을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private ImageService imageService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ImageController(imageService)).build();
    }

    /**
     * 동일한 files 파트로 전달된 모든 파일이 서비스에 전달되고 URL 목록으로 응답되는지 검증합니다.
     *
     * @throws Exception MockMvc 요청 수행 중 발생할 수 있는 예외
     */
    @Test
    @DisplayName("multipart 이미지 세 장을 서비스에 모두 전달한다")
    void passesAllMultipartFilesToService() throws Exception {
        // given
        MockMultipartFile firstFile = new MockMultipartFile(
                "files", "first.jpg", "image/jpeg", "first".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile(
                "files", "second.png", "image/png", "second".getBytes());
        MockMultipartFile thirdFile = new MockMultipartFile(
                "files", "third.heic", "image/heic", "third".getBytes());
        List<String> imageUrls = List.of("first-url", "second-url", "third-url");

        when(imageService.upload(anyList())).thenReturn(ImageUploadResponse.toDto(imageUrls));

        // when
        // then
        mockMvc.perform(multipart("/images")
                        .file(firstFile)
                        .file(secondFile)
                        .file(thirdFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrls.length()").value(3));

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<MultipartFile>> filesCaptor = ArgumentCaptor.forClass(List.class);
        verify(imageService).upload(filesCaptor.capture());
        assertThat(filesCaptor.getValue()).hasSize(3);
    }
}
