package triplog.backend.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.landmark.entity.Card;

import java.util.Collection;
import java.util.List;
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

    /**
     * 여러 랜드마크에 연결된 카드를 한 번에 조회합니다.
     *
     * @param landmarkIds 조회할 랜드마크 식별자 목록
     * @return 랜드마크에 연결된 카드 목록
     */
    List<Card> findByLandmarkLandmarkIdIn(Collection<Long> landmarkIds);
}
