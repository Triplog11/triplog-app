package triplog.backend.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.landmark.entity.Card;

import java.util.Optional;

/**
 * 랜드마크 카드 영속성 처리를 담당합니다.
 */
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * 랜드마크에 고정된 카드를 조회합니다.
     *
     * @param landmarkId 랜드마크 식별자
     * @return 랜드마크 카드
     */
    Optional<Card> findByLandmarkLandmarkId(Long landmarkId);
}
