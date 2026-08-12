package triplog.backend.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.review.entity.Review;

/**
 * Review 영속성 처리를 담당하는 Repository입니다.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
