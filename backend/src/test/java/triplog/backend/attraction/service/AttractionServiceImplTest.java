package triplog.backend.attraction.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.attraction.entity.Attraction;
import triplog.backend.attraction.exception.InvalidAttractionContentTypeException;
import triplog.backend.attraction.repository.AttractionRepository;
import triplog.backend.tourismcontent.entity.TourismContent;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link AttractionServiceImpl}의 콘텐츠 타입 검증과 저장 흐름을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class AttractionServiceImplTest {

    @Mock
    private AttractionRepository attractionRepository;

    @Mock
    private TourismContent tourismContent;

    private AttractionServiceImpl attractionService;

    @BeforeEach
    void setUp() {
        attractionService = new AttractionServiceImpl(attractionRepository);
    }

    /** 허용한 콘텐츠 타입을 일반 관광지로 저장하는지 검증합니다. */
    @ParameterizedTest
    @ValueSource(strings = {"12", "14", "28"})
    @DisplayName("허용한 contentTypeId의 관광 콘텐츠를 일반 관광지로 저장한다")
    void 허용한_관광_콘텐츠를_일반_관광지로_저장한다(String contentTypeId) {
        // Given
        when(tourismContent.getContentTypeId()).thenReturn(contentTypeId);
        when(tourismContent.getTourismContentId()).thenReturn(1L);
        when(attractionRepository.findByTourismContentTourismContentId(1L))
                .thenReturn(Optional.empty());
        when(attractionRepository.save(any(Attraction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Attraction attraction = attractionService.upsert(tourismContent);

        // Then
        assertThat(attraction.getTourismContent()).isSameAs(tourismContent);
        verify(attractionRepository).save(any(Attraction.class));
    }

    /** 허용하지 않은 콘텐츠 타입의 저장을 거부하는지 검증합니다. */
    @Test
    @DisplayName("허용하지 않은 contentTypeId는 일반 관광지로 저장하지 않는다")
    void 허용하지_않은_콘텐츠는_저장하지_않는다() {
        // Given
        when(tourismContent.getContentTypeId()).thenReturn("15");

        // When
        // Then
        assertThatThrownBy(() -> attractionService.upsert(tourismContent))
                .isInstanceOf(InvalidAttractionContentTypeException.class);
        verify(attractionRepository, never()).save(any());
    }
}
