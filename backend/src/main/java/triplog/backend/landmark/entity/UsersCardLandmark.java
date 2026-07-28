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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 사용자의 랜드마크 카드 획득 정보를 관리하는 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users_card_landmark")
public class UsersCardLandmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_card_landmark_id", nullable = false)
    private Long usersCardLandmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landmark_id", nullable = false)
    private Landmark landmark;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Column(name = "users_id", nullable = false, length = 36)
    private String usersId;

    @Column(name = "users_card_landmark_visited_at", nullable = false)
    private LocalDateTime usersCardLandmarkVisitedAt;

    @Column(name = "users_card_landmark_count", nullable = false)
    private Integer usersCardLandmarkCount;
}
