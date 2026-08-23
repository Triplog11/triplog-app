package triplog.backend.landmark.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 사용자의 랜드마크 카드 획득 정보를 관리하는 엔티티입니다.
 */
@Entity
@Getter
@Table(name = "users_card_landmark")
public class UsersCardLandmark {

    /**
     * JPA 엔티티 생성을 위한 기본 생성자입니다.
     */
    protected UsersCardLandmark() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_card_landmark_id", nullable = false)
    private Long usersCardLandmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landmark_id", nullable = false)
    private Landmark landmark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "users_id", nullable = false, length = 36)
    private String usersId;

    @Column(name = "users_card_landmark_visited_at", nullable = false)
    private LocalDateTime usersCardLandmarkVisitedAt;

    @Column(name = "users_card_landmark_count", nullable = false)
    private Integer usersCardLandmarkCount;

    /**
     * 사용자가 최초로 획득한 랜드마크 카드를 생성합니다.
     *
     * @param landmark 획득한 랜드마크
     * @param card 랜드마크에 고정된 카드
     * @param usersId 사용자 식별자
     * @param visitedAt 획득 일시
     */
    public UsersCardLandmark(
            Landmark landmark,
            Card card,
            String usersId,
            LocalDateTime visitedAt
    ) {
        this.landmark = landmark;
        this.card = card;
        this.usersId = usersId;
        this.usersCardLandmarkVisitedAt = visitedAt;
        this.usersCardLandmarkCount = 1;
    }
}
