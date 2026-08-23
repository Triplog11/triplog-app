package triplog.backend.landmark.service;

import triplog.backend.landmark.entity.Card;
import triplog.backend.landmark.entity.CardTier;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.exception.LandmarkException;

import java.util.Optional;

/**
 * 랜드마크 카드의 생성·갱신·조회 기능을 정의합니다.
 */
public interface CardService {

    /**
     * 랜드마크에 고정된 카드를 생성하거나 표시 정보를 갱신합니다.
     *
     * @param landmark 카드가 속한 랜드마크
     * @param cardName 카드 표시명
     * @param cardTier 카드 희귀도
     * @param cardUrl 카드 이미지 URL, 비어 있으면 설정된 기본 이미지 사용
     * @return 생성하거나 갱신한 카드
     */
    Card upsert(Landmark landmark, String cardName, CardTier cardTier, String cardUrl);

    /**
     * 랜드마크에 연결된 카드를 조회합니다.
     *
     * @param landmarkId 랜드마크 식별자
     * @return 연결된 카드, 존재하지 않으면 빈 값
     */
    Optional<Card> findOptionalByLandmarkId(Long landmarkId);

    /**
     * 랜드마크에 연결된 카드를 조회합니다.
     *
     * @param landmarkId 랜드마크 식별자
     * @return 연결된 카드
     * @throws LandmarkException 연결된 카드가 없는 경우
     */
    Card findByLandmarkId(Long landmarkId);
}
