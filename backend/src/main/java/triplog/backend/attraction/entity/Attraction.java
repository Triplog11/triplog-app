package triplog.backend.attraction.entity;

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
 * 서비스에서 일반 관광지로 선정한 관광 콘텐츠입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "attraction",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_attraction_tourism_content",
                columnNames = "tourism_content_id"
        )
)
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attraction_id", nullable = false)
    private Long attractionId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tourism_content_id", nullable = false)
    private TourismContent tourismContent;

    /**
     * 선정한 관광 콘텐츠와 연결된 일반 관광지를 생성합니다.
     *
     * @param tourismContent contentTypeId가 12, 14, 28 중 하나인 TourAPI 공통 관광 콘텐츠
     */
    public Attraction(TourismContent tourismContent) {
        this.tourismContent = tourismContent;
    }
}
