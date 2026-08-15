package triplog.backend.tourismcontent.service;

import triplog.backend.region.entity.Region;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.exception.TourismContentNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * TourismContent 생성, 갱신, 동기화 상태 변경 기능을 정의하는 도메인 서비스입니다.
 */
public interface TourismContentService {

    /**
     * 관광 콘텐츠 식별자로 콘텐츠를 선택 조회합니다.
     *
     * @param tourismContentId 관광 콘텐츠 식별자
     * @return 관광 콘텐츠 또는 빈 Optional
     */
    Optional<TourismContent> findOptionalById(Long tourismContentId);

    /**
     * TourAPI contentId로 관광 콘텐츠를 조회합니다.
     *
     * @param externalContentId TourAPI contentId
     * @return 일치하는 관광 콘텐츠
     * @throws TourismContentNotFoundException 콘텐츠가 없는 경우
     */
    TourismContent findByExternalContentId(String externalContentId);

    /**
     * TourAPI contentId로 관광 콘텐츠를 선택 조회합니다.
     *
     * @param externalContentId TourAPI contentId
     * @return 일치하는 관광 콘텐츠 또는 빈 Optional
     */
    Optional<TourismContent> findOptionalByExternalContentId(String externalContentId);

    /**
     * TourAPI contentId 기준으로 공통 관광정보를 생성하거나 갱신합니다.
     *
     * @param region 관광 콘텐츠가 속한 Region
     * @param syncData TourAPI 공통정보 동기화 입력값
     * @param syncedAt 동기화 완료 시각
     * @return 생성하거나 갱신한 TourismContent
     */
    TourismContent upsert(
            Region region,
            TourismContentSyncData syncData,
            LocalDateTime syncedAt
    );

    /**
     * 외부 목록 누락 횟수를 증가시킵니다.
     *
     * @param externalContentId TourAPI contentId
     * @param threshold 비활성 후보 기준 횟수
     */
    void markMissing(String externalContentId, int threshold);

    /**
     * 관광 콘텐츠를 논리적으로 비활성화합니다.
     *
     * @param externalContentId TourAPI contentId
     */
    void deactivate(String externalContentId);
}
