package triplog.backend.landmark.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 랜드마크 방문으로 획득할 수 있는 카드의 표시 정보를 관리하는 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id", nullable = false, unique = true)
    private Long cardId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "landmark_id", nullable = false, unique = true)
    private Landmark landmark;

    @Column(name = "card_name", nullable = false, length = 255)
    private String cardName;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_tier", nullable = false, length = 10)
    private CardTier cardTier;

    @Column(name = "card_url", nullable = false, length = 2048)
    private String cardUrl;

    public Card(Landmark landmark, String cardName, CardTier cardTier, String cardUrl) {
        this.landmark = landmark;
        this.cardName = cardName;
        this.cardTier = cardTier;
        this.cardUrl = cardUrl;
    }

    /**
     * TourAPI 공식명과 선정 데이터의 카드 표시 정보를 갱신합니다.
     *
     * @param cardName TourAPI 공식명
     * @param cardTier 고정 카드 희귀도
     * @param cardUrl Cloudinary 카드 이미지 URL
     */
    public void update(String cardName, CardTier cardTier, String cardUrl) {
        this.cardName = cardName;
        this.cardTier = cardTier;
        this.cardUrl = cardUrl;
    }
}
