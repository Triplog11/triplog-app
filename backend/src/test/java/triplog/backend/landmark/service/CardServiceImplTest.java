package triplog.backend.landmark.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.landmark.config.CardProperties;
import triplog.backend.landmark.entity.Card;
import triplog.backend.landmark.entity.CardTier;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.exception.LandmarkException;
import triplog.backend.landmark.repository.CardRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * {@link CardServiceImpl}의 카드 저장과 조회 규칙을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    private static final String DEFAULT_CARD_URL =
            "https://res.cloudinary.com/demo/image/upload/default-card.png";

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardProperties cardProperties;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    @DisplayName("카드가 없으면 기본 이미지로 랜드마크 카드를 생성한다")
    void upsertCreatesCardWithDefaultImage() {
        // Given
        Landmark landmark = mock(Landmark.class);
        given(landmark.getLandmarkId()).willReturn(1L);
        given(cardRepository.findByLandmarkLandmarkId(1L)).willReturn(Optional.empty());
        given(cardProperties.defaultImageUrl()).willReturn(DEFAULT_CARD_URL);
        given(cardRepository.save(any(Card.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // When
        Card result = cardService.upsert(landmark, "수원 화성", CardTier.RARE, "");

        // Then
        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(cardCaptor.capture());
        assertThat(result).isSameAs(cardCaptor.getValue());
        assertThat(result.getLandmark()).isSameAs(landmark);
        assertThat(result.getCardName()).isEqualTo("수원 화성");
        assertThat(result.getCardTier()).isEqualTo(CardTier.RARE);
        assertThat(result.getCardUrl()).isEqualTo(DEFAULT_CARD_URL);
    }

    @Test
    @DisplayName("기존 카드가 있으면 새로 저장하지 않고 표시 정보를 갱신한다")
    void upsertUpdatesExistingCard() {
        // Given
        Landmark landmark = mock(Landmark.class);
        Card card = mock(Card.class);
        given(landmark.getLandmarkId()).willReturn(1L);
        given(cardRepository.findByLandmarkLandmarkId(1L)).willReturn(Optional.of(card));

        // When
        Card result = cardService.upsert(
                landmark,
                "TourAPI 공식명",
                CardTier.EPIC,
                "https://cdn.triplog.com/cards/1.png"
        );

        // Then
        assertThat(result).isSameAs(card);
        verify(card).update(
                "TourAPI 공식명",
                CardTier.EPIC,
                "https://cdn.triplog.com/cards/1.png"
        );
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    @DisplayName("랜드마크 카드가 없으면 조회 예외가 발생한다")
    void findByLandmarkIdThrowsWhenCardDoesNotExist() {
        // Given
        given(cardRepository.findByLandmarkLandmarkId(999L)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> cardService.findByLandmarkId(999L))
                .isInstanceOf(LandmarkException.class);
    }
}
