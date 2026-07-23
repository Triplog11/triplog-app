package triplog.backend.tourismcontent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.region.entity.Region;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.exception.TourismContentNotFoundException;
import triplog.backend.tourismcontent.repository.TourismContentRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * {@link TourismContentService}의 기본 구현체입니다.
 * TourAPI 콘텐츠의 저장과 동기화 상태 변경을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class TourismContentServiceImpl implements TourismContentService {

    private final TourismContentRepository tourismContentRepository;

    /**
     * TourAPI contentId로 관광 콘텐츠를 조회합니다.
     *
     * @param externalContentId TourAPI contentId
     * @return 일치하는 관광 콘텐츠
     * @throws TourismContentNotFoundException 콘텐츠가 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public TourismContent findByExternalContentId(String externalContentId) {
        return tourismContentRepository.findByExternalContentId(externalContentId)
                .orElseThrow(() -> new TourismContentNotFoundException(externalContentId));
    }

    /**
     * TourAPI contentId로 관광 콘텐츠를 선택 조회합니다.
     *
     * @param externalContentId TourAPI contentId
     * @return 일치하는 관광 콘텐츠 또는 빈 Optional
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TourismContent> findOptionalByExternalContentId(String externalContentId) {
        return tourismContentRepository.findByExternalContentId(externalContentId);
    }

    /**
     * TourAPI contentId 기준으로 공통 관광정보를 생성하거나 갱신합니다.
     *
     * @param region 관광 콘텐츠가 속한 Region
     * @param syncData TourAPI 공통정보 동기화 입력값
     * @param syncedAt 동기화 완료 시각
     * @return 생성하거나 갱신한 TourismContent
     */
    @Override
    @Transactional
    public TourismContent upsert(
            Region region,
            TourismContentSyncData syncData,
            LocalDateTime syncedAt
    ) {
        return tourismContentRepository.findByExternalContentId(syncData.externalContentId())
                .map(content -> {
                    content.update(region, syncData, syncedAt);
                    return content;
                })
                .orElseGet(() -> tourismContentRepository.save(
                        new TourismContent(region, syncData, syncedAt)
                ));
    }

    /**
     * 외부 목록 누락 횟수를 증가시킵니다.
     *
     * @param externalContentId TourAPI contentId
     * @param threshold 비활성 후보 기준 횟수
     */
    @Override
    @Transactional
    public void markMissing(String externalContentId, int threshold) {
        findByExternalContentId(externalContentId).markMissing(threshold);
    }

    /**
     * 관광 콘텐츠를 논리적으로 비활성화합니다.
     *
     * @param externalContentId TourAPI contentId
     */
    @Override
    @Transactional
    public void deactivate(String externalContentId) {
        findByExternalContentId(externalContentId).deactivate();
    }
}
