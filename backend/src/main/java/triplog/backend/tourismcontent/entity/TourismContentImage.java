package triplog.backend.tourismcontent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.tourismcontent.service.TourismContentImageSyncData;

/**
 * TourAPI가 제공하는 관광 콘텐츠 이미지 URL과 저작권 정보를 관리합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "tourism_content_image",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tourism_content_image_serial",
                columnNames = {"tourism_content_id", "external_serial_number"}
        ),
        indexes = @Index(
                name = "idx_tourism_content_image_content",
                columnList = "tourism_content_id"
        )
)
public class TourismContentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tourism_content_image_id", nullable = false)
    private Long tourismContentImageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tourism_content_id", nullable = false)
    private TourismContent tourismContent;

    @Column(name = "external_serial_number", nullable = false, length = 50)
    private String externalSerialNumber;

    @Column(name = "image_name", length = 255)
    private String imageName;

    @Column(name = "original_image_url", nullable = false, length = 2048)
    private String originalImageUrl;

    @Column(name = "thumbnail_image_url", length = 2048)
    private String thumbnailImageUrl;

    @Column(name = "copyright_type", length = 20)
    private String copyrightType;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    /**
     * 관광 콘텐츠와 TourAPI 이미지 정보로 이미지를 생성합니다.
     *
     * @param tourismContent 이미지가 속한 관광 콘텐츠
     * @param syncData TourAPI 이미지 동기화 입력값
     */
    public TourismContentImage(
            TourismContent tourismContent,
            TourismContentImageSyncData syncData
    ) {
        this.tourismContent = tourismContent;
        this.externalSerialNumber = syncData.externalSerialNumber();
        update(syncData);
    }

    /**
     * 최신 이미지 URL과 메타데이터를 반영하고 활성화합니다.
     *
     * @param syncData TourAPI 이미지 동기화 입력값
     */
    public void update(TourismContentImageSyncData syncData) {
        this.imageName = syncData.imageName();
        this.originalImageUrl = syncData.originalImageUrl();
        this.thumbnailImageUrl = syncData.thumbnailImageUrl();
        this.copyrightType = syncData.copyrightType();
        this.active = true;
    }

    /**
     * 최신 TourAPI 전체 이미지 응답에서 누락된 이미지를 비활성화합니다.
     */
    public void deactivate() {
        this.active = false;
    }
}
