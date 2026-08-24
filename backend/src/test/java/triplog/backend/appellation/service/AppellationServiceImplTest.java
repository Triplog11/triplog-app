package triplog.backend.appellation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.achievement.service.AchievementContext;
import triplog.backend.appellation.entity.Appellation;
import triplog.backend.appellation.entity.UsersAppellation;
import triplog.backend.appellation.exception.AppellationException;
import triplog.backend.appellation.repository.AppellationRepository;
import triplog.backend.appellation.repository.UsersAppellationRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppellationServiceImplTest {

    @Mock
    private AppellationRepository appellationRepository;

    @Mock
    private UsersAppellationRepository usersAppellationRepository;

    private AppellationServiceImpl appellationService;

    @BeforeEach
    void setUp() {
        appellationService = new AppellationServiceImpl(
                appellationRepository, usersAppellationRepository
        );
    }

    @Test
    @DisplayName("조건을 충족한 여러 칭호를 사용자에게 최초 지급한다")
    void acquireEligibleAppellations() {
        // Given
        Appellation first = appellation(1L, "여행의 시작", "VISIT_COUNT", 1);
        Appellation second = appellation(2L, "랜드마크 탐험가", "LANDMARK_COUNT", 10);
        when(appellationRepository.findUnacquiredAppellations("user-id"))
                .thenReturn(List.of(first, second));
        when(usersAppellationRepository.insertIfAbsent("user-id", 1L)).thenReturn(1);
        when(usersAppellationRepository.insertIfAbsent("user-id", 2L)).thenReturn(1);

        // When
        List<AcquiredAppellationInfo> result = appellationService
                .acquireEligibleAppellations(
                        "user-id",
                        new AchievementContext(Map.of(
                                "VISIT_COUNT", 1L,
                                "LANDMARK_COUNT", 10L
                        ))
                );

        // Then
        assertThat(result).extracting(AcquiredAppellationInfo::appellationName)
                .containsExactly("여행의 시작", "랜드마크 탐험가");
    }

    @Test
    @DisplayName("이미 획득한 칭호는 동시 판정에서도 다시 지급하지 않는다")
    void acquireEligibleAppellations_AlreadyAcquired() {
        // Given
        Appellation appellation = appellation(1L, null, "VISIT_COUNT", 1);
        when(appellationRepository.findUnacquiredAppellations("user-id"))
                .thenReturn(List.of(appellation));
        when(usersAppellationRepository.insertIfAbsent("user-id", 1L)).thenReturn(0);

        // When
        List<AcquiredAppellationInfo> result = appellationService
                .acquireEligibleAppellations(
                        "user-id",
                        new AchievementContext(Map.of("VISIT_COUNT", 1L))
                );

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("획득 칭호 목록을 대표 칭호 우선 순서로 반환한다")
    void getAcquiredAppellations() {
        // Given
        Appellation representativeAppellation = mock(Appellation.class);
        when(representativeAppellation.getAppellationId()).thenReturn(2L);
        when(representativeAppellation.getAppellationName()).thenReturn("랜드마크 탐험가");
        UsersAppellation representative = mock(UsersAppellation.class);
        when(representative.getAppellation()).thenReturn(representativeAppellation);
        when(representative.isRepresentative()).thenReturn(true);

        Appellation normalAppellation = mock(Appellation.class);
        when(normalAppellation.getAppellationId()).thenReturn(1L);
        when(normalAppellation.getAppellationName()).thenReturn("여행의 시작");
        UsersAppellation normal = mock(UsersAppellation.class);
        when(normal.getAppellation()).thenReturn(normalAppellation);

        when(usersAppellationRepository.findAllAcquiredByUsersId("user-id"))
                .thenReturn(List.of(representative, normal));

        // When
        var result = appellationService.getAcquiredAppellations("user-id");

        // Then
        assertThat(result.totalElements()).isEqualTo(2L);
        assertThat(result.items())
                .extracting(item -> item.appellationName())
                .containsExactly("랜드마크 탐험가", "여행의 시작");
        assertThat(result.items().get(0).representative()).isTrue();
        assertThat(result.items().get(1).representative()).isFalse();
    }

    @Test
    @DisplayName("획득한 칭호가 없으면 빈 목록을 반환한다")
    void getAcquiredAppellations_Empty() {
        // Given
        when(usersAppellationRepository.findAllAcquiredByUsersId("user-id"))
                .thenReturn(List.of());

        // When
        var result = appellationService.getAcquiredAppellations("user-id");

        // Then
        assertThat(result.totalElements()).isZero();
        assertThat(result.items()).isEmpty();
    }

    @Test
    @DisplayName("대표 칭호를 마이페이지 조합용 정보로 조회한다")
    void getRepresentativeAppellation() {
        // Given
        Appellation appellation = mock(Appellation.class);
        when(appellation.getAppellationId()).thenReturn(2L);
        when(appellation.getAppellationName()).thenReturn("랜드마크 탐험가");
        UsersAppellation representative = mock(UsersAppellation.class);
        when(representative.getAppellation()).thenReturn(appellation);
        when(usersAppellationRepository.findRepresentativeByUsersId("user-id"))
                .thenReturn(java.util.Optional.of(representative));

        // When
        var result = appellationService.getRepresentativeAppellation("user-id");

        // Then
        assertThat(result).isPresent();
        assertThat(result.orElseThrow().appellationId()).isEqualTo(2L);
        assertThat(result.orElseThrow().appellationName())
                .isEqualTo("랜드마크 탐험가");
    }

    @Test
    @DisplayName("획득한 칭호를 대표 칭호로 변경하면 기존 대표 칭호는 해제된다")
    void changeRepresentativeAppellation() {
        // Given
        Appellation previousAppellation = mock(Appellation.class);
        when(previousAppellation.getAppellationId()).thenReturn(1L);
        Appellation selectedAppellation = mock(Appellation.class);
        when(selectedAppellation.getAppellationId()).thenReturn(2L);
        when(selectedAppellation.getAppellationName()).thenReturn("랜드마크 탐험가");

        UsersAppellation previous = mock(UsersAppellation.class);
        when(previous.getAppellation()).thenReturn(previousAppellation);
        UsersAppellation selected = mock(UsersAppellation.class);
        when(selected.getAppellation()).thenReturn(selectedAppellation);
        when(usersAppellationRepository.findAllByUsersIdForUpdate("user-id"))
                .thenReturn(List.of(previous, selected));

        // When
        var result = appellationService.changeRepresentativeAppellation(
                "user-id", 2L
        );

        // Then
        assertThat(result.getAppellationId()).isEqualTo(2L);
        assertThat(result.getAppellationName()).isEqualTo("랜드마크 탐험가");
        assertThat(result.isRepresentative()).isTrue();
        verify(previous).changeRepresentative(false);
        verify(selected).changeRepresentative(true);
    }

    @Test
    @DisplayName("획득하지 않은 칭호는 대표 칭호로 지정할 수 없다")
    void changeRepresentativeAppellation_NotAcquired() {
        // Given
        Appellation acquiredAppellation = mock(Appellation.class);
        when(acquiredAppellation.getAppellationId()).thenReturn(1L);
        UsersAppellation acquired = mock(UsersAppellation.class);
        when(acquired.getAppellation()).thenReturn(acquiredAppellation);
        when(usersAppellationRepository.findAllByUsersIdForUpdate("user-id"))
                .thenReturn(List.of(acquired));

        // When & Then
        assertThatThrownBy(() -> appellationService
                .changeRepresentativeAppellation("user-id", 99L))
                .isInstanceOf(AppellationException.class)
                .hasMessage("획득한 칭호를 찾을 수 없습니다.");
    }

    private Appellation appellation(
            Long appellationId,
            String appellationName,
            String target,
            int value
    ) {
        Appellation appellation = mock(Appellation.class);
        when(appellation.getAppellationId()).thenReturn(appellationId);
        if (appellationName != null) {
            when(appellation.getAppellationName()).thenReturn(appellationName);
        }
        when(appellation.getAppellationTarget()).thenReturn(target);
        when(appellation.getAppellationOperator()).thenReturn(">=");
        when(appellation.getAppellationValue()).thenReturn(value);
        return appellation;
    }
}
