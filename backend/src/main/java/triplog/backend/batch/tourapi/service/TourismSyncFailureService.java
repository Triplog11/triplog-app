package triplog.backend.batch.tourapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.batch.tourapi.entity.TourismSyncFailure;
import triplog.backend.batch.tourapi.entity.TourismSyncType;
import triplog.backend.batch.tourapi.entity.TourismSyncFailureStatus;
import triplog.backend.batch.tourapi.repository.TourismSyncFailureRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TourAPI 콘텐츠 동기화 실패 이력의 생성, 갱신, 해결을 담당합니다.
 */
@Service
@RequiredArgsConstructor
public class TourismSyncFailureService {

    private final TourismSyncFailureRepository failureRepository;

    /**
     * 아직 해결되지 않은 실패 이력을 조회합니다.
     *
     * @return 재처리 대기 실패 이력
     */
    @Transactional(readOnly = true)
    public List<TourismSyncFailure> findPendingFailures() {
        return failureRepository.findAllBySyncFailureStatus(TourismSyncFailureStatus.PENDING);
    }

    /**
     * 실패 이력을 재처리 중 상태로 변경합니다.
     *
     * @param syncType 동기화 작업 유형
     * @param externalContentId TourAPI contentId
     * @param retriedAt 재처리 시작 시각
     */
    @Transactional
    public void markRetrying(
            TourismSyncType syncType,
            String externalContentId,
            LocalDateTime retriedAt
    ) {
        failureRepository.findBySyncTypeAndExternalContentId(syncType, externalContentId)
                .ifPresent(failure -> failure.markRetrying(retriedAt));
    }

    /**
     * 동일 작업과 콘텐츠의 실패 이력을 생성하거나 최신 실패 정보로 갱신합니다.
     *
     * @param syncType 동기화 작업 유형
     * @param externalContentId TourAPI contentId
     * @param legalRegionCode 법정동 시도 코드
     * @param legalDistrictCode 법정동 시군구 코드
     * @param errorCode 애플리케이션 오류 코드
     * @param errorMessage 비밀정보를 제외한 실패 메시지
     * @param failedAt 실패 시각
     */
    @Transactional
    public void recordFailure(
            TourismSyncType syncType,
            String externalContentId,
            String legalRegionCode,
            String legalDistrictCode,
            String errorCode,
            String errorMessage,
            LocalDateTime failedAt
    ) {
        failureRepository.findBySyncTypeAndExternalContentId(syncType, externalContentId)
                .ifPresentOrElse(
                        failure -> failure.retryFailed(
                                legalRegionCode,
                                legalDistrictCode,
                                errorCode,
                                errorMessage,
                                failedAt
                        ),
                        () -> failureRepository.save(new TourismSyncFailure(
                                syncType,
                                externalContentId,
                                legalRegionCode,
                                legalDistrictCode,
                                errorCode,
                                errorMessage,
                                failedAt
                        ))
                );
    }

    /**
     * 동일 작업과 콘텐츠의 실패 이력을 해결 상태로 변경합니다.
     *
     * @param syncType 동기화 작업 유형
     * @param externalContentId TourAPI contentId
     * @param resolvedAt 해결 시각
     */
    @Transactional
    public void resolve(
            TourismSyncType syncType,
            String externalContentId,
            LocalDateTime resolvedAt
    ) {
        failureRepository.findBySyncTypeAndExternalContentId(syncType, externalContentId)
                .ifPresent(failure -> failure.resolve(resolvedAt));
    }
}
