package triplog.backend.tourismcontent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.tourismcontent.entity.TourismContent;
import triplog.backend.tourismcontent.entity.TourismContentImage;
import triplog.backend.tourismcontent.repository.TourismContentImageRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link TourismContentImageService}의 기본 구현체입니다.
 * TourAPI 전체 이미지 응답을 기준으로 관광 콘텐츠 이미지를 동기화합니다.
 */
@Service
@RequiredArgsConstructor
public class TourismContentImageServiceImpl implements TourismContentImageService {

    private final TourismContentImageRepository imageRepository;

    /**
     * 이미지 일련번호 기준으로 신규·변경·재등장 이미지를 반영하고 누락 이미지를 비활성화합니다.
     *
     * @param tourismContent 이미지가 속한 관광 콘텐츠
     * @param syncDataList 최신 TourAPI 전체 이미지 목록
     */
    @Override
    @Transactional
    public void synchronize(
            TourismContent tourismContent,
            List<TourismContentImageSyncData> syncDataList
    ) {
        List<TourismContentImage> existingImages = imageRepository
                .findAllByTourismContentTourismContentId(tourismContent.getTourismContentId());
        Map<String, TourismContentImage> existingBySerial = new HashMap<>();
        for (TourismContentImage image : existingImages) {
            existingBySerial.put(image.getExternalSerialNumber(), image);
        }

        Set<String> receivedSerials = new HashSet<>();
        for (TourismContentImageSyncData syncData : syncDataList) {
            receivedSerials.add(syncData.externalSerialNumber());
            TourismContentImage existing = existingBySerial.get(syncData.externalSerialNumber());
            if (existing == null) {
                imageRepository.save(new TourismContentImage(tourismContent, syncData));
            } else {
                existing.update(syncData);
            }
        }

        for (TourismContentImage existing : existingImages) {
            if (!receivedSerials.contains(existing.getExternalSerialNumber())) {
                existing.deactivate();
            }
        }
    }
}
