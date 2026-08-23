package triplog.backend.landmark.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import triplog.backend.landmark.entity.Card;
import triplog.backend.landmark.entity.CardTier;
import triplog.backend.landmark.dto.response.LandmarkResponse.LandmarkDetailResponse;
import triplog.backend.landmark.dto.response.LandmarkResponse.ObtainedCardListResponse;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.entity.UsersCardLandmark;
import triplog.backend.landmark.exception.InvalidLandmarkContentTypeException;
import triplog.backend.landmark.exception.LandmarkException;
import triplog.backend.landmark.repository.LandmarkRepository;
import triplog.backend.landmark.repository.UsersCardLandmarkRepository;
import triplog.backend.region.entity.Region;
import triplog.backend.tourismcontent.entity.TourismContent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static triplog.backend.landmark.exception.LandmarkErrorCode.LANDMARK_DETAIL_NOT_FOUND;

/**
 * {@link LandmarkServiceImpl}의 랜드마크 조회 흐름을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class LandmarkServiceImplTest {

    private static final String USERS_ID = "013d613e-de7d-4a31-8661-ba8e65ff14c6";

    @Mock
    private LandmarkRepository landmarkRepository;

    @Mock
    private CardService cardService;

    @Mock
    private UsersCardLandmarkRepository usersCardLandmarkRepository;

    @Mock
    private triplog.backend.landmarkvisitlog.service.LandmarkVisitLogService landmarkVisitLogService;

    private LandmarkServiceImpl landmarkService;

    @BeforeEach
    void setUp() {
        landmarkService = new LandmarkServiceImpl(
                landmarkRepository,
                cardService,
                usersCardLandmarkRepository,
                landmarkVisitLogService
        );
    }

    @Test
    @DisplayName("사용자가 수집한 서로 다른 랜드마크 카드 수를 조회한다")
    void countCollectedCards() {
        // given
        given(usersCardLandmarkRepository.countByUsersId(USERS_ID)).willReturn(8L);

        // when
        int result = landmarkService.countCollectedCards(USERS_ID);

        // then
        assertThat(result).isEqualTo(8);
    }

    /** 허용한 콘텐츠 타입을 랜드마크로 저장하는지 검증합니다. */
    @ParameterizedTest
    @ValueSource(strings = {"12", "14", "28"})
    @DisplayName("허용한 contentTypeId의 관광 콘텐츠를 랜드마크로 저장한다")
    void 허용한_관광_콘텐츠를_랜드마크로_저장한다(String contentTypeId) {
        // Given
        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getContentTypeId()).thenReturn(contentTypeId);
        when(tourismContent.getTourismContentId()).thenReturn(1L);
        when(tourismContent.getTitle()).thenReturn("TourAPI 공식명");
        given(landmarkRepository.findByTourismContentTourismContentId(1L))
                .willReturn(Optional.empty());
        given(landmarkRepository.save(any(Landmark.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // When
        Landmark landmark = landmarkService.upsert(
                tourismContent,
                "선정 랜드마크",
                CardTier.EPIC,
                ""
        );

        // Then
        assertThat(landmark.getTourismContent()).isSameAs(tourismContent);
        verify(landmarkRepository).save(any(Landmark.class));
        verify(cardService).upsert(landmark, "TourAPI 공식명", CardTier.EPIC, "");
    }

    /** 허용하지 않은 콘텐츠 타입의 랜드마크 저장을 거부하는지 검증합니다. */
    @Test
    @DisplayName("허용하지 않은 contentTypeId는 랜드마크로 저장하지 않는다")
    void 허용하지_않은_콘텐츠는_랜드마크로_저장하지_않는다() {
        // Given
        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getContentTypeId()).thenReturn("15");

        // When
        // Then
        assertThatThrownBy(() -> landmarkService.upsert(
                tourismContent,
                "축제",
                CardTier.COMMON,
                ""
        ))
                .isInstanceOf(InvalidLandmarkContentTypeException.class);
        verify(landmarkRepository, never()).save(any());
    }

    /**
     * 랜드마크 상세 조회 시 획득한 랜드마크의 정보가 정확히 반환되는지 검증합니다.
     */
    @Test
    @DisplayName("랜드마크 상세 정보를 조회한다 - 획득 상태")
    void getLandmarkDetail_Acquired() {
        // given
        Region region = mock(Region.class);
        when(region.getRegionId()).thenReturn(1L);
        when(region.getRegionName()).thenReturn("수원시");
        when(region.getLegalRegionCode()).thenReturn("41");
        when(region.getLegalDistrictCode()).thenReturn("110");

        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getRegion()).thenReturn(region);
        when(tourismContent.getExternalContentId()).thenReturn("EXT-101");

        Landmark landmark = mock(Landmark.class);
        when(landmark.getLandmarkId()).thenReturn(1L);
        when(landmark.getLandmarkName()).thenReturn("수원 화성");
        when(landmark.getTourismContent()).thenReturn(tourismContent);

        UsersCardLandmark usersCardLandmark = mock(UsersCardLandmark.class);
        Card card = mock(Card.class);
        when(card.getCardName()).thenReturn("수원 화성");
        when(card.getCardTier()).thenReturn(CardTier.RARE);
        when(card.getCardUrl()).thenReturn("https://res.cloudinary.com/demo/image/upload/1.png");
        when(usersCardLandmark.getUsersCardLandmarkVisitedAt())
                .thenReturn(LocalDateTime.of(2026, 6, 20, 14, 30, 0));
        when(usersCardLandmark.getUsersCardLandmarkCount()).thenReturn(2);

        given(landmarkRepository.findByIdWithTourismContentAndRegion(1L))
                .willReturn(Optional.of(landmark));
        given(usersCardLandmarkRepository.findByUsersIdAndLandmarkLandmarkIdWithCard(USERS_ID, 1L))
                .willReturn(Optional.of(usersCardLandmark));
        given(cardService.findOptionalByLandmarkId(1L)).willReturn(Optional.of(card));

        // when
        LandmarkDetailResponse response = landmarkService.getLandmarkDetail(USERS_ID, 1L);

        // then
        assertThat(response.getLandmarkId()).isEqualTo(1L);
        assertThat(response.getLandmarkName()).isEqualTo("수원 화성");
        assertThat(response.getRegionId()).isEqualTo(1L);
        assertThat(response.getRegionName()).isEqualTo("수원시");
        assertThat(response.getContentId()).isEqualTo("EXT-101");
        assertThat(response.getLegalRegionCode()).isEqualTo("41");
        assertThat(response.getLegalDistrictCode()).isEqualTo("110");
        assertThat(response.getCardName()).isEqualTo("수원 화성");
        assertThat(response.getCardTier()).isEqualTo("RARE");
        assertThat(response.getCardUrl())
                .isEqualTo("https://res.cloudinary.com/demo/image/upload/1.png");
        assertThat(response.getAcquired()).isTrue();
        assertThat(response.getAcquiredAt()).isEqualTo("2026-06-20T14:30");
        assertThat(response.getVisitCount()).isEqualTo(2);
    }

    /**
     * 랜드마크 상세 조회 시 미획득 상태의 정보가 정확히 반환되는지 검증합니다.
     */
    @Test
    @DisplayName("랜드마크 상세 정보를 조회한다 - 미획득 상태")
    void getLandmarkDetail_NotAcquired() {
        // given
        Region region = mock(Region.class);
        when(region.getRegionId()).thenReturn(1L);
        when(region.getRegionName()).thenReturn("수원시");
        when(region.getLegalRegionCode()).thenReturn("41");
        when(region.getLegalDistrictCode()).thenReturn("110");

        TourismContent tourismContent = mock(TourismContent.class);
        when(tourismContent.getRegion()).thenReturn(region);
        when(tourismContent.getExternalContentId()).thenReturn("EXT-101");

        Landmark landmark = mock(Landmark.class);
        when(landmark.getLandmarkId()).thenReturn(1L);
        when(landmark.getLandmarkName()).thenReturn("수원 화성");
        when(landmark.getTourismContent()).thenReturn(tourismContent);

        given(landmarkRepository.findByIdWithTourismContentAndRegion(1L))
                .willReturn(Optional.of(landmark));
        given(usersCardLandmarkRepository.findByUsersIdAndLandmarkLandmarkIdWithCard(USERS_ID, 1L))
                .willReturn(Optional.empty());
        Card card = mock(Card.class);
        when(card.getCardName()).thenReturn("수원 화성");
        when(card.getCardTier()).thenReturn(CardTier.RARE);
        when(card.getCardUrl()).thenReturn("https://res.cloudinary.com/demo/image/upload/1.png");
        given(cardService.findOptionalByLandmarkId(1L)).willReturn(Optional.of(card));

        // when
        LandmarkDetailResponse response = landmarkService.getLandmarkDetail(USERS_ID, 1L);

        // then
        assertThat(response.getCardTier()).isEqualTo("RARE");
        assertThat(response.getAcquired()).isFalse();
        assertThat(response.getAcquiredAt()).isNull();
        assertThat(response.getVisitCount()).isNull();
    }

    /**
     * 랜드마크 상세 조회 시 랜드마크가 존재하지 않으면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("랜드마크 상세 조회 시 랜드마크가 없으면 예외가 발생한다")
    void getLandmarkDetail_NotFound() {
        // given
        given(landmarkRepository.findByIdWithTourismContentAndRegion(999L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> landmarkService.getLandmarkDetail(USERS_ID, 999L))
                .isInstanceOf(LandmarkException.class)
                .extracting("errorCode")
                .isEqualTo(LANDMARK_DETAIL_NOT_FOUND);
    }

    /**
     * 지역별 전체 랜드마크 수를 정확히 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("지역별 전체 랜드마크 수를 조회한다")
    void countLandmarksByRegion() {
        // given
        given(landmarkRepository.countLandmarksByRegion())
                .willReturn(List.<Object[]>of(new Object[]{1L, 4L}, new Object[]{2L, 3L}));

        // when
        Map<Long, Long> result = landmarkService.countLandmarksByRegion();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(1L)).isEqualTo(4L);
        assertThat(result.get(2L)).isEqualTo(3L);
    }

    /**
     * 지역별 사용자 방문 랜드마크 수를 정확히 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("지역별 사용자 방문 랜드마크 수를 조회한다")
    void countVisitedLandmarksByRegionAndUser() {
        // given
        given(landmarkRepository.countVisitedLandmarksByRegionAndUser(USERS_ID))
                .willReturn(List.<Object[]>of(new Object[]{1L, 2L}));

        // when
        Map<Long, Long> result = landmarkService.countVisitedLandmarksByRegionAndUser(USERS_ID);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(1L)).isEqualTo(2L);
    }

    /**
     * 사용자가 획득한 랜드마크 ID 집합을 정확히 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("사용자가 획득한 랜드마크 ID 집합을 조회한다")
    void findAcquiredLandmarkIdsByUsersId() {
        // given
        given(usersCardLandmarkRepository.findAcquiredLandmarkIdsByUsersId(USERS_ID))
                .willReturn(Set.of(1L, 3L, 5L));

        // when
        Set<Long> result = landmarkService.findAcquiredLandmarkIdsByUsersId(USERS_ID);

        // then
        assertThat(result).containsExactlyInAnyOrder(1L, 3L, 5L);
    }

    @Test
    @DisplayName("사용자가 획득한 카드 목록을 페이지 단위로 조회한다")
    void getObtainedCards() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        Landmark landmark = mock(Landmark.class);
        Card card = mock(Card.class);
        UsersCardLandmark obtainedCard = mock(UsersCardLandmark.class);
        when(landmark.getLandmarkId()).thenReturn(301L);
        when(landmark.getLandmarkName()).thenReturn("수원화성");
        when(card.getCardId()).thenReturn(501L);
        when(card.getCardName()).thenReturn("수원 화성");
        when(card.getCardTier()).thenReturn(CardTier.RARE);
        when(card.getCardUrl()).thenReturn("https://cdn.triplog.com/cards/501.png");
        when(obtainedCard.getLandmark()).thenReturn(landmark);
        when(obtainedCard.getCard()).thenReturn(card);
        when(obtainedCard.getUsersCardLandmarkVisitedAt())
                .thenReturn(LocalDateTime.of(2026, 6, 20, 14, 30));
        given(usersCardLandmarkRepository
                .findByUsersIdOrderByUsersCardLandmarkVisitedAtDescUsersCardLandmarkIdDesc(
                        USERS_ID,
                        pageable
                ))
                .willReturn(new PageImpl<>(List.of(obtainedCard), pageable, 1));

        // When
        ObtainedCardListResponse response = landmarkService.getObtainedCards(USERS_ID, pageable);

        // Then
        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getItems()).singleElement().satisfies(item -> {
            assertThat(item.getCardId()).isEqualTo(501L);
            assertThat(item.getLandmarkId()).isEqualTo(301L);
            assertThat(item.getLandmarkName()).isEqualTo("수원화성");
            assertThat(item.getCardName()).isEqualTo("수원 화성");
            assertThat(item.getCardTier()).isEqualTo("RARE");
            assertThat(item.getCardUrl()).isEqualTo("https://cdn.triplog.com/cards/501.png");
            assertThat(item.getAcquiredAt()).isEqualTo("2026-06-20T14:30");
        });
    }

    @Test
    @DisplayName("획득한 카드가 없으면 빈 목록을 반환한다")
    void getObtainedCards_Empty() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        given(usersCardLandmarkRepository
                .findByUsersIdOrderByUsersCardLandmarkVisitedAtDescUsersCardLandmarkIdDesc(
                        USERS_ID,
                        pageable
                ))
                .willReturn(new PageImpl<>(List.of(), pageable, 0));

        // When
        ObtainedCardListResponse response = landmarkService.getObtainedCards(USERS_ID, pageable);

        // Then
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getTotalPages()).isZero();
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    @DisplayName("홈 화면용 최근 획득 카드 정보를 조회한다")
    void getRecentObtainedCardInfo() {
        // given
        PageRequest pageable = PageRequest.of(0, 3);
        Region region = mock(Region.class);
        when(region.getLegalRegionCode()).thenReturn("41");
        when(region.getLegalDistrictCode()).thenReturn("110");
        TourismContent content = mock(TourismContent.class);
        when(content.getRegion()).thenReturn(region);
        Landmark landmark = mock(Landmark.class);
        when(landmark.getLandmarkId()).thenReturn(301L);
        when(landmark.getLandmarkName()).thenReturn("수원화성");
        when(landmark.getTourismContent()).thenReturn(content);
        Card card = mock(Card.class);
        when(card.getCardTier()).thenReturn(CardTier.RARE);
        when(card.getCardName()).thenReturn("수원 화성");
        when(card.getCardUrl()).thenReturn("image.com");
        UsersCardLandmark obtainedCard = mock(UsersCardLandmark.class);
        when(obtainedCard.getLandmark()).thenReturn(landmark);
        when(obtainedCard.getCard()).thenReturn(card);
        given(usersCardLandmarkRepository
                .findByUsersIdOrderByUsersCardLandmarkVisitedAtDescUsersCardLandmarkIdDesc(
                        USERS_ID,
                        pageable
                ))
                .willReturn(new PageImpl<>(List.of(obtainedCard), pageable, 1));

        // when
        List<LandmarkHomeCardInfo> result =
                landmarkService.getRecentObtainedCardInfo(USERS_ID, 3);

        // then
        assertThat(result).singleElement().satisfies(item -> {
            assertThat(item.landmarkId()).isEqualTo(301L);
            assertThat(item.landmarkZipcode()).isEqualTo("41110");
            assertThat(item.cardTier()).isEqualTo("RARE");
        });
    }

    @Test
    @DisplayName("랜드마크에 고정된 카드를 사용자에게 최초 한 번 저장한다")
    void acquireCard_SavesLinkedCard() {
        // Given
        Landmark landmark = mock(Landmark.class);
        Card card = mock(Card.class);
        given(usersCardLandmarkRepository.findByUsersIdAndLandmarkLandmarkId(USERS_ID, 1L))
                .willReturn(Optional.empty());
        given(landmarkRepository.findById(1L)).willReturn(Optional.of(landmark));
        given(cardService.findByLandmarkId(1L)).willReturn(card);

        // When
        boolean acquired = landmarkService.acquireCard(USERS_ID, 1L);

        // Then
        ArgumentCaptor<UsersCardLandmark> acquisitionCaptor =
                ArgumentCaptor.forClass(UsersCardLandmark.class);
        verify(usersCardLandmarkRepository).save(acquisitionCaptor.capture());
        assertThat(acquired).isTrue();
        assertThat(acquisitionCaptor.getValue().getLandmark()).isSameAs(landmark);
        assertThat(acquisitionCaptor.getValue().getCard()).isSameAs(card);
        assertThat(acquisitionCaptor.getValue().getUsersId()).isEqualTo(USERS_ID);
        assertThat(acquisitionCaptor.getValue().getUsersCardLandmarkCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 획득한 랜드마크 카드는 다시 저장하지 않는다")
    void acquireCard_AlreadyAcquired() {
        // Given
        given(usersCardLandmarkRepository.findByUsersIdAndLandmarkLandmarkId(USERS_ID, 1L))
                .willReturn(Optional.of(mock(UsersCardLandmark.class)));

        // When
        boolean acquired = landmarkService.acquireCard(USERS_ID, 1L);

        // Then
        assertThat(acquired).isFalse();
        verify(usersCardLandmarkRepository, never()).save(any());
    }
}
