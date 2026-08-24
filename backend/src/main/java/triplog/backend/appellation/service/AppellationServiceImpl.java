package triplog.backend.appellation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.achievement.service.AchievementContext;
import triplog.backend.appellation.dto.response.AppellationResponse.RepresentativeResponse;
import triplog.backend.appellation.dto.response.AppellationResponse.AcquiredListResponse;
import triplog.backend.appellation.entity.Appellation;
import triplog.backend.appellation.entity.UsersAppellation;
import triplog.backend.appellation.exception.AppellationErrorCode;
import triplog.backend.appellation.exception.AppellationException;
import triplog.backend.appellation.repository.AppellationRepository;
import triplog.backend.appellation.repository.UsersAppellationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static triplog.backend.achievement.service.AchievementConditionEvaluator.isSatisfied;

/**
 * DB 칭호 정책을 판정하고 사용자별 최초 획득 상태를 저장합니다.
 */
@Service
@RequiredArgsConstructor
public class AppellationServiceImpl implements AppellationService {

    private final AppellationRepository appellationRepository;
    private final UsersAppellationRepository usersAppellationRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<RepresentativeAppellationInfo> getRepresentativeAppellation(
            String usersId
    ) {
        return usersAppellationRepository.findRepresentativeByUsersId(usersId)
                .map(RepresentativeAppellationInfo::from);
    }

    @Override
    @Transactional(readOnly = true)
    public AcquiredListResponse getAcquiredAppellations(String usersId) {
        return AcquiredListResponse.toDto(
                usersAppellationRepository.findAllAcquiredByUsersId(usersId)
        );
    }

    @Override
    @Transactional
    public List<AcquiredAppellationInfo> acquireEligibleAppellations(
            String usersId,
            AchievementContext context
    ) {
        List<AcquiredAppellationInfo> acquiredAppellations = new ArrayList<>();
        for (Appellation appellation
                : appellationRepository.findUnacquiredAppellations(usersId)) {
            if (!isSatisfied(
                    context,
                    appellation.getAppellationTarget(),
                    appellation.getAppellationOperator(),
                    appellation.getAppellationValue()
            )) {
                continue;
            }
            if (usersAppellationRepository.insertIfAbsent(
                    usersId, appellation.getAppellationId()
            ) == 1) {
                acquiredAppellations.add(new AcquiredAppellationInfo(
                        appellation.getAppellationId(),
                        appellation.getAppellationName()
                ));
            }
        }
        return List.copyOf(acquiredAppellations);
    }

    /**
     * 획득한 칭호 행을 잠근 뒤 기존 대표 칭호를 해제하고 요청 칭호만 대표로 지정합니다.
     *
     * @throws AppellationException 사용자가 요청한 칭호를 획득하지 않은 경우
     */
    @Override
    @Transactional
    public RepresentativeResponse changeRepresentativeAppellation(
            String usersId,
            Long appellationId
    ) {
        List<UsersAppellation> acquiredAppellations =
                usersAppellationRepository.findAllByUsersIdForUpdate(usersId);

        UsersAppellation representativeAppellation = acquiredAppellations.stream()
                .filter(usersAppellation -> usersAppellation.getAppellation()
                        .getAppellationId().equals(appellationId))
                .findFirst()
                .orElseThrow(() -> new AppellationException(
                        AppellationErrorCode.APPELLATION_NOT_ACQUIRED
                ));

        acquiredAppellations.forEach(usersAppellation ->
                usersAppellation.changeRepresentative(
                        usersAppellation == representativeAppellation
                ));

        return RepresentativeResponse.toDto(
                representativeAppellation.getAppellation()
        );
    }
}
