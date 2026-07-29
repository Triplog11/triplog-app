package triplog.backend.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.review.entity.Review;

/**
 * Review 영속성 처리를 담당하는 Repository입니다.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 사용자가 특정 관광 콘텐츠에 이미 리뷰를 작성했는지 확인합니다.
     *
     * @param usersId          사용자 식별자
     * @param tourismContentId 관광 콘텐츠 식별자
     * @return 이미 존재하면 true
     */
    boolean existsByUsersIdAndTourismContentTourismContentId(String usersId, Long tourismContentId);
}
