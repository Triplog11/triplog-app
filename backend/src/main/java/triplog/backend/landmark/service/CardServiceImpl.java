package triplog.backend.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import triplog.backend.landmark.config.CardProperties;
import triplog.backend.landmark.entity.Card;
import triplog.backend.landmark.entity.CardTier;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.exception.LandmarkException;
import triplog.backend.landmark.repository.CardRepository;

import java.util.Optional;

import static triplog.backend.landmark.exception.LandmarkErrorCode.LANDMARK_CARD_NOT_FOUND;

/**
 * 카드 저장소를 통해 랜드마크 카드의 생명주기를 관리합니다.
 */
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardProperties cardProperties;

    /**
     * 랜드마크에 고정된 카드를 생성하거나 표시 정보를 갱신합니다.
     *
     * @param landmark 카드가 속한 랜드마크
     * @param cardName 카드 표시명
     * @param cardTier 카드 희귀도
     * @param cardUrl 카드 이미지 URL, 비어 있으면 설정된 기본 이미지 사용
     * @return 생성하거나 갱신한 카드
     */
    @Override
    @Transactional
    public Card upsert(Landmark landmark, String cardName, CardTier cardTier, String cardUrl) {
        String resolvedCardUrl = resolveCardUrl(cardUrl);
        return cardRepository.findByLandmarkLandmarkId(landmark.getLandmarkId())
                .map(card -> {
                    card.update(cardName, cardTier, resolvedCardUrl);
                    return card;
                })
                .orElseGet(() -> cardRepository.save(
                        new Card(landmark, cardName, cardTier, resolvedCardUrl)
                ));
    }

    /**
     * 랜드마크에 연결된 카드를 선택적으로 조회합니다.
     *
     * @param landmarkId 랜드마크 식별자
     * @return 연결된 카드, 존재하지 않으면 빈 값
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Card> findOptionalByLandmarkId(Long landmarkId) {
        return cardRepository.findByLandmarkLandmarkId(landmarkId);
    }

    /**
     * 랜드마크에 연결된 카드를 조회하고, 없으면 도메인 예외를 발생시킵니다.
     *
     * @param landmarkId 랜드마크 식별자
     * @return 연결된 카드
     * @throws LandmarkException 연결된 카드가 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public Card findByLandmarkId(Long landmarkId) {
        return findOptionalByLandmarkId(landmarkId)
                .orElseThrow(() -> new LandmarkException(LANDMARK_CARD_NOT_FOUND));
    }

    private String resolveCardUrl(String cardUrl) {
        if (StringUtils.hasText(cardUrl)) {
            return cardUrl;
        }
        if (!StringUtils.hasText(cardProperties.defaultImageUrl())) {
            throw new IllegalStateException("기본 카드 이미지 URL이 설정되지 않았습니다.");
        }
        return cardProperties.defaultImageUrl();
    }
}
