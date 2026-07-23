package triplog.backend.tourismcontent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import triplog.backend.region.entity.Region;
import triplog.backend.tourismcontent.service.TourismContentSyncData;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Landmark와 Event가 공유하는 TourAPI 관광 콘텐츠 원본 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "tourism_content",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tourism_content_external",
                columnNames = "external_content_id"
        ),
        indexes = {
                @Index(name = "idx_tourism_content_region_type", columnList = "region_id,content_type_id,is_active"),
                @Index(name = "idx_tourism_content_modified", columnList = "provider_modified_at")
        }
)
public class TourismContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tourism_content_id", nullable = false)
    private Long tourismContentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "external_content_id", nullable = false, length = 32)
    private String externalContentId;

    @Column(name = "previous_external_content_id", length = 32)
    private String previousExternalContentId;

    @Column(name = "content_type_id", nullable = false, length = 10)
    private String contentTypeId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "overview", columnDefinition = "text")
    private String overview;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "detail_address", length = 500)
    private String detailAddress;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "telephone", length = 255)
    private String telephone;

    @Column(name = "homepage", columnDefinition = "text")
    private String homepage;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "map_level")
    private Integer mapLevel;

    @Column(name = "legal_region_code", length = 10)
    private String legalRegionCode;

    @Column(name = "legal_district_code", length = 10)
    private String legalDistrictCode;

    @Column(name = "classification_depth1", length = 20)
    private String classificationDepth1;

    @Column(name = "classification_depth2", length = 20)
    private String classificationDepth2;

    @Column(name = "classification_depth3", length = 20)
    private String classificationDepth3;

    @Column(name = "primary_image_url", length = 2048)
    private String primaryImageUrl;

    @Column(name = "thumbnail_image_url", length = 2048)
    private String thumbnailImageUrl;

    @Column(name = "copyright_type", length = 20)
    private String copyrightType;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status", nullable = false, length = 20)
    private TourismSyncStatus syncStatus;

    @Column(name = "consecutive_failure_count", nullable = false)
    private int consecutiveFailureCount;

    @Column(name = "consecutive_missing_count", nullable = false)
    private int consecutiveMissingCount;

    @Column(name = "provider_created_at")
    private LocalDateTime providerCreatedAt;

    @Column(name = "provider_modified_at")
    private LocalDateTime providerModifiedAt;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "last_sync_failure_at")
    private LocalDateTime lastSyncFailureAt;

    /**
     * Region과 TourAPI 공통정보로 관광 콘텐츠를 생성합니다.
     *
     * @param region 콘텐츠가 속한 Region
     * @param syncData TourAPI 공통정보 동기화 입력값
     * @param syncedAt 동기화 완료 시각
     */
    public TourismContent(Region region, TourismContentSyncData syncData, LocalDateTime syncedAt) {
        this.region = region;
        this.externalContentId = syncData.externalContentId();
        this.active = true;
        this.syncStatus = TourismSyncStatus.COMPLETED;
        apply(syncData, syncedAt);
    }

    /**
     * 최신 TourAPI 공통정보와 Region을 반영합니다.
     *
     * @param region 최신 법정동 코드에 해당하는 Region
     * @param syncData TourAPI 공통정보 동기화 입력값
     * @param syncedAt 동기화 완료 시각
     */
    public void update(Region region, TourismContentSyncData syncData, LocalDateTime syncedAt) {
        this.region = region;
        if (!this.externalContentId.equals(syncData.externalContentId())) {
            this.previousExternalContentId = this.externalContentId;
            this.externalContentId = syncData.externalContentId();
        }
        this.active = true;
        this.syncStatus = TourismSyncStatus.COMPLETED;
        this.consecutiveFailureCount = 0;
        this.consecutiveMissingCount = 0;
        apply(syncData, syncedAt);
    }

    /**
     * 외부 조회 실패 횟수와 마지막 실패 시각을 기록합니다.
     *
     * @param failedAt 실패 발생 시각
     */
    public void markFailed(LocalDateTime failedAt) {
        this.consecutiveFailureCount++;
        this.lastSyncFailureAt = failedAt;
        this.syncStatus = TourismSyncStatus.FAILED;
    }

    /**
     * 외부 목록에서 누락된 횟수를 증가시키고 기준 도달 여부를 반영합니다.
     *
     * @param threshold 비활성 후보 판단 기준 횟수
     */
    public void markMissing(int threshold) {
        this.consecutiveMissingCount++;
        this.syncStatus = TourismSyncStatus.INACTIVE_CANDIDATE;
        if (this.consecutiveMissingCount >= threshold) {
            this.active = false;
        }
    }

    /**
     * 외부 콘텐츠가 정상 확인된 경우 누락 상태를 초기화합니다.
     */
    public void clearMissing() {
        this.consecutiveMissingCount = 0;
        this.active = true;
        if (this.syncStatus == TourismSyncStatus.INACTIVE_CANDIDATE) {
            this.syncStatus = TourismSyncStatus.COMPLETED;
        }
    }

    /**
     * 콘텐츠를 물리 삭제하지 않고 서비스 비활성 상태로 전환합니다.
     */
    public void deactivate() {
        this.active = false;
        this.syncStatus = TourismSyncStatus.INACTIVE_CANDIDATE;
    }

    /**
     * 외부 관광 콘텐츠 정보와 동기화 완료 시각을 엔티티에 반영합니다.
     *
     * @param data 반영할 관광 콘텐츠 동기화 값
     * @param syncedAt 동기화를 완료한 시각
     */
    private void apply(TourismContentSyncData data, LocalDateTime syncedAt) {
        this.contentTypeId = data.contentTypeId();
        this.title = data.title();
        this.overview = data.overview();
        this.address = data.address();
        this.detailAddress = data.detailAddress();
        this.postalCode = data.postalCode();
        this.telephone = data.telephone();
        this.homepage = data.homepage();
        this.longitude = data.longitude();
        this.latitude = data.latitude();
        this.mapLevel = data.mapLevel();
        this.legalRegionCode = data.legalRegionCode();
        this.legalDistrictCode = data.legalDistrictCode();
        this.classificationDepth1 = data.classificationDepth1();
        this.classificationDepth2 = data.classificationDepth2();
        this.classificationDepth3 = data.classificationDepth3();
        this.primaryImageUrl = data.primaryImageUrl();
        this.thumbnailImageUrl = data.thumbnailImageUrl();
        this.copyrightType = data.copyrightType();
        this.providerCreatedAt = data.providerCreatedAt();
        this.providerModifiedAt = data.providerModifiedAt();
        this.lastSyncedAt = syncedAt;
    }
}
