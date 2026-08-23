package triplog.backend.review.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 방문 인증 목록 native query의 실행 가능 여부를 검증합니다.
 */
@SpringBootTest(properties = {
        "spring.batch.job.enabled=false",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none",
        "mission.scheduling.enabled=false"
})
@Transactional(readOnly = true)
class ReviewRepositoryIntegrationTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("사용자 방문 인증 목록 쿼리를 페이지 단위로 실행한다")
    void findReviewListByUsersId() {
        // Given
        String usersId = "00000000-0000-0000-0000-000000000000";
        PageRequest pageable = PageRequest.of(0, 20);

        // When
        Page<ReviewListQueryResult> result = reviewRepository.findReviewListByUsersId(usersId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("사용자 소유권을 포함한 방문 인증 상세 조회 쿼리를 실행한다")
    void findReviewDetailByReviewIdAndUsersId() {
        // Given
        String usersId = "00000000-0000-0000-0000-000000000000";

        // When
        Optional<ReviewDetailQueryResult> result = reviewRepository
                .findReviewDetailByReviewIdAndUsersId(Long.MAX_VALUE, usersId);

        // Then
        assertThat(result).isEmpty();
    }
}
