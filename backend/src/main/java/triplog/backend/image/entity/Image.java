package triplog.backend.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리뷰 첨부 이미지 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long imageId;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name = "saved_name", nullable = false, length = 255)
    private String savedName;

    @Column(name = "image_url", nullable = false, length = 2048)
    private String imageUrl;

    @Column(name = "file_size", nullable = false)
    private Integer fileSize;

    @Column(name = "image_created_at", nullable = false)
    private LocalDateTime imageCreatedAt;

    /**
     * 리뷰 이미지를 생성합니다.
     *
     * @param reviewId     리뷰 식별자
     * @param originalName 원본 파일명
     * @param savedName    저장 파일명
     * @param imageUrl     이미지 URL
     * @param fileSize     파일 크기
     */
    public Image(Long reviewId, String originalName, String savedName,
                 String imageUrl, Integer fileSize) {
        this.reviewId = reviewId;
        this.originalName = originalName;
        this.savedName = savedName;
        this.imageUrl = imageUrl;
        this.fileSize = fileSize;
        this.imageCreatedAt = LocalDateTime.now();
    }
}
