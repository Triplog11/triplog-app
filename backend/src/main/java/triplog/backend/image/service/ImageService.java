package triplog.backend.image.service;

import org.springframework.web.multipart.MultipartFile;
import triplog.backend.image.dto.response.ImageResponse.ImageUploadResponse;

import java.util.List;

/**
 * 이미지 업로드 기능을 정의하는 서비스 인터페이스입니다.
 */
public interface ImageService {

    /**
     * 한 개 이상의 이미지 파일을 외부 이미지 저장소에 업로드합니다.
     *
     * @param files 업로드할 이미지 파일 목록
     * @return 업로드된 이미지 URL 목록을 포함한 응답
     */
    ImageUploadResponse upload(List<MultipartFile> files);

    /**
     * 이미지 파일을 업로드하고 리뷰에 연결하여 저장합니다.
     *
     * @param reviewId 리뷰 식별자
     * @param files    이미지 파일 목록
     */
    void uploadAndSave(Long reviewId, List<MultipartFile> files);
}
