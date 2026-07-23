package triplog.backend.batch.tourapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.batch.tourapi.entity.TourismSyncCheckpoint;
import triplog.backend.batch.tourapi.entity.TourismSyncType;
import triplog.backend.batch.tourapi.repository.TourismSyncCheckpointRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 동기화 작업의 마지막 성공시각 조회와 갱신을 담당합니다.
 */
@Service
@RequiredArgsConstructor
public class TourismSyncCheckpointService {

    private final TourismSyncCheckpointRepository checkpointRepository;

    /**
     * 동기화 유형의 마지막 성공시각을 조회합니다.
     *
     * @param syncType 동기화 작업 유형
     * @return 마지막 성공시각 또는 빈 값
     */
    @Transactional(readOnly = true)
    public Optional<LocalDateTime> findLastSucceededAt(TourismSyncType syncType) {
        return checkpointRepository.findBySyncType(syncType)
                .map(TourismSyncCheckpoint::getLastSucceededAt);
    }

    /**
     * 모든 대상 처리가 성공한 경우에만 체크포인트를 생성하거나 갱신합니다.
     *
     * @param syncType 동기화 작업 유형
     * @param succeededAt 성공 완료 시각
     */
    @Transactional
    public void updateSucceededAt(TourismSyncType syncType, LocalDateTime succeededAt) {
        checkpointRepository.findBySyncType(syncType)
                .ifPresentOrElse(
                        checkpoint -> checkpoint.update(succeededAt),
                        () -> checkpointRepository.save(new TourismSyncCheckpoint(syncType, succeededAt))
                );
    }
}
