package triplog.backend.reviewlog.entity;

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
 * 리뷰 작성 및 변경 로그 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_log")
public class ReviewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_log_id", nullable = false)
    private Long reviewLogId;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "review_created_at", nullable = false)
    private LocalDateTime reviewCreatedAt;

    @Column(name = "review_log_content", nullable = false, length = 500)
    private String reviewLogContent;

    @Column(name = "review_gain_xp", nullable = false)
    private Integer reviewGainXp;

    /**
     * 리뷰 로그를 생성합니다.
     *
     * @param reviewId         리뷰 식별자
     * @param reviewLogContent 로그 내용
     * @param reviewGainXp     받은 경험치
     */
    public ReviewLog(Long reviewId, String reviewLogContent, Integer reviewGainXp) {
        this.reviewId = reviewId;
        this.reviewCreatedAt = LocalDateTime.now();
        this.reviewLogContent = reviewLogContent;
        this.reviewGainXp = reviewGainXp;
    }
}
