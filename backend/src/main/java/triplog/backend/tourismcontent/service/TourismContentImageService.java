package triplog.backend.tourismcontent.service;

import triplog.backend.tourismcontent.entity.TourismContent;

import java.util.List;

/**
 * 관광 콘텐츠 이미지 동기화 기능을 정의하는 도메인 서비스입니다.
 */
public interface TourismContentImageService {

    /**
     * 이미지 일련번호 기준으로 신규·변경·재등장 이미지를 반영하고 누락 이미지를 비활성화합니다.
     *
     * @param tourismContent 이미지가 속한 관광 콘텐츠
     * @param syncDataList 최신 TourAPI 전체 이미지 목록
     */
    void synchronize(
            TourismContent tourismContent,
            List<TourismContentImageSyncData> syncDataList
    );
}
