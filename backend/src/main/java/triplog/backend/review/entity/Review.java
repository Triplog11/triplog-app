package triplog.backend.review.entity;

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
import triplog.backend.tourismcontent.entity.TourismContent;

/**
 * 방문 인증 리뷰 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "users_id", nullable = false, length = 36)
    private String usersId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourism_content_id", nullable = false)
    private TourismContent tourismContent;

    @Column(name = "review_title", nullable = false, length = 100)
    private String reviewTitle;

    @Column(name = "review_content", nullable = false, length = 500)
    private String reviewContent;

    @Column(name = "review_score", nullable = false)
    private Float reviewScore;

    @Column(name = "review_point", nullable = false)
    private Integer reviewPoint;

    /**
     * 방문 인증 리뷰를 생성합니다.
     *
     * @param usersId        사용자 식별자
     * @param tourismContent 관광 콘텐츠
     * @param reviewTitle    리뷰 제목
     * @param reviewContent  리뷰 내용
     * @param reviewScore    만족도
     */
    public Review(String usersId, TourismContent tourismContent, String reviewTitle,
                  String reviewContent, Float reviewScore) {
        this.usersId = usersId;
        this.tourismContent = tourismContent;
        this.reviewTitle = reviewTitle;
        this.reviewContent = reviewContent;
        this.reviewScore = reviewScore;
        this.reviewPoint = 0;
    }

}
