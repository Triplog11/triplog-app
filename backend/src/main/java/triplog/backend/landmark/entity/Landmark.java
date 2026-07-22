package triplog.backend.landmark.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.tourismcontent.entity.TourismContent;

/**
 * 카드와 방문 인증 대상으로 선정된 관광지 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "landmark",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_landmark_tourism_content",
                columnNames = "tourism_content_id"
        )
)
public class Landmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "landmark_id", nullable = false)
    private Long landmarkId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tourism_content_id", nullable = false)
    private TourismContent tourismContent;

    @Column(name = "landmark_name", length = 100)
    private String landmarkName;

    /**
     * 관광 콘텐츠와 표시명으로 Landmark를 생성합니다.
     *
     * @param tourismContent contentTypeId가 12인 관광 콘텐츠
     * @param landmarkName 서비스 표시명 오버라이드
     */
    public Landmark(TourismContent tourismContent, String landmarkName) {
        this.tourismContent = tourismContent;
        this.landmarkName = landmarkName;
    }

    /**
     * CSV 시드의 최신 표시명 오버라이드를 반영합니다.
     *
     * @param landmarkName 서비스 표시명 오버라이드
     */
    public void updateName(String landmarkName) {
        this.landmarkName = landmarkName;
    }
}
