package triplog.backend.batch.tourapi.seed;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import triplog.backend.batch.tourapi.config.TourismSyncProperties;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Optional;

/**
 * classpath 또는 파일 리소스의 랜드마크 CSV를 읽고 형식을 검증합니다.
 */
@Component
public class LandmarkSeedReader {

    private static final String LANDMARK_CONTENT_TYPE_ID = "12";
    private final ResourceLoader resourceLoader;
    private final TourismSyncProperties properties;

    /**
     * 리소스 로더와 동기화 설정을 주입받습니다.
     *
     * @param resourceLoader CSV 리소스 로더
     * @param properties 동기화 설정
     */
    public LandmarkSeedReader(ResourceLoader resourceLoader, TourismSyncProperties properties) {
        this.resourceLoader = resourceLoader;
        this.properties = properties;
    }

    /**
     * 설정된 CSV에서 활성 랜드마크만 읽습니다.
     *
     * @return 중복이 제거되고 검증된 활성 랜드마크 목록
     */
    public List<LandmarkSeed> readActiveSeeds() {
        Resource resource = resourceLoader.getResource(properties.landmarkSeedPath());
        Set<String> contentIds = new HashSet<>();
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .get()
                     .parse(reader)) {
            return parser.stream()
                    .map(record -> convert(record, contentIds))
                    .filter(LandmarkSeed::active)
                    .toList();
        } catch (IOException | IllegalArgumentException exception) {
            throw new InvalidLandmarkSeedException("랜드마크 CSV를 읽거나 검증할 수 없습니다.", exception);
        }
    }

    /**
     * 활성 CSV 시드에서 지정한 contentId를 조회합니다.
     *
     * @param contentId TourAPI 콘텐츠 식별자
     * @return 활성 랜드마크 시드 또는 빈 값
     */
    public Optional<LandmarkSeed> findActiveSeed(String contentId) {
        return readActiveSeeds().stream()
                .filter(seed -> seed.contentId().equals(contentId))
                .findFirst();
    }

    /**
     * CSV 레코드를 랜드마크 시드로 변환하고 contentId 중복 여부를 검증합니다.
     *
     * @param record 변환할 CSV 레코드
     * @param contentIds 앞서 읽은 contentId 집합
     * @return 검증을 마친 랜드마크 시드
     */
    private LandmarkSeed convert(CSVRecord record, Set<String> contentIds) {
        String contentId = required(record, "content_id");
        if (!contentIds.add(contentId)) {
            throw new InvalidLandmarkSeedException("중복 content_id가 있습니다: " + contentId);
        }
        String contentTypeId = required(record, "expected_content_type_id");
        if (!LANDMARK_CONTENT_TYPE_ID.equals(contentTypeId)) {
            throw new InvalidLandmarkSeedException("랜드마크 contentTypeId는 12여야 합니다: " + contentId);
        }
        String activeValue = required(record, "is_active");
        if (!"true".equalsIgnoreCase(activeValue) && !"false".equalsIgnoreCase(activeValue)) {
            throw new InvalidLandmarkSeedException("is_active는 true 또는 false여야 합니다: " + contentId);
        }
        return new LandmarkSeed(
                contentId,
                required(record, "display_name"),
                contentTypeId,
                required(record, "expected_legal_region_code"),
                required(record, "expected_legal_district_code"),
                Boolean.parseBoolean(activeValue)
        );
    }

    /**
     * CSV 레코드에서 필수 값을 읽고 빈 값 여부를 검증합니다.
     *
     * @param record 값을 읽을 CSV 레코드
     * @param header 필수 열 이름
     * @return 앞뒤 공백을 제거한 필수 값
     */
    private String required(CSVRecord record, String header) {
        String value = record.get(header);
        if (!StringUtils.hasText(value)) {
            throw new InvalidLandmarkSeedException(header + " 값이 비어 있습니다. record=" + record.getRecordNumber());
        }
        return value;
    }
}
