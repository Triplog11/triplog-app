package triplog.backend.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import triplog.backend.image.client.CloudinaryImageUploader;
import triplog.backend.image.config.ImageUploadProperties;
import triplog.backend.image.dto.response.ImageResponse.ImageUploadResponse;
import triplog.backend.image.exception.ImageException;

import java.util.List;

import static triplog.backend.image.dto.response.ImageResponse.ImageUploadResponse.toDto;
import static triplog.backend.image.exception.ImageErrorCode.INVALID_IMAGE_FILE;

/**
 * 이미지 파일을 검증하고 Cloudinary 업로드를 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final CloudinaryImageUploader cloudinaryImageUploader;
    private final ImageUploadProperties imageUploadProperties;

    /**
     * 요청 파일들이 유효한 이미지인지 검증한 후 Cloudinary에 업로드합니다.
     *
     * @param files 업로드할 이미지 파일 목록
     * @return 업로드된 이미지 URL 목록을 포함한 응답
     * @throws ImageException 파일 목록이 비어 있거나 개수, MIME 타입이 유효하지 않은 경우
     */
    @Override
    public ImageUploadResponse upload(List<MultipartFile> files) {
        if (files == null
                || files.isEmpty()
                || files.size() > imageUploadProperties.maxFileCount()
                || files.stream().anyMatch(file -> file == null
                        || file.isEmpty()
                        || file.getContentType() == null
                        || !imageUploadProperties.allowedContentTypes().contains(file.getContentType()))) {
            throw new ImageException(INVALID_IMAGE_FILE);
        }

        List<String> imageUrls = files.stream()
                .map(cloudinaryImageUploader::upload)
                .toList();

        return toDto(imageUrls);
    }
}
