package triplog.backend.batch.tourapi.seed;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import triplog.backend.batch.tourapi.config.TourismSyncProperties;
import triplog.backend.landmark.entity.CardTier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 랜드마크 카드 정보와 일반 관광지 contentId 선정 CSV를 읽습니다.
 */
@Component
@RequiredArgsConstructor
public class SelectedContentSeedReader {

    private static final String CONTENT_ID_HEADER = "content_id";
    private static final String RARITY_HEADER = "rarity";
    private static final String CARD_URL_HEADER = "card_url";

    private final ResourceLoader resourceLoader;
    private final TourismSyncProperties properties;

    /**
     * 두 선정 목록을 읽고 파일 내부 및 파일 간 contentId 중복을 검증합니다.
     *
     * @return 검증된 선정 목록
     * @throws InvalidSelectedContentSeedException CSV를 읽을 수 없거나 형식·중복 검증에 실패한 경우
     */
    public SelectedContentSeeds read() {
        Map<String, LandmarkSeed> landmarkSeeds = readLandmarkSeeds(
                properties.landmarkSeedPath()
        );
        Set<String> attractionContentIds = readContentIds(
                properties.attractionSeedPath(),
                "관광지"
        );

        Set<String> duplicatedContentIds = new LinkedHashSet<>(landmarkSeeds.keySet());
        duplicatedContentIds.retainAll(attractionContentIds);
        if (!duplicatedContentIds.isEmpty()) {
            throw new InvalidSelectedContentSeedException(
                    "랜드마크와 관광지 CSV에 중복 content_id가 있습니다: " + duplicatedContentIds
            );
        }
        return new SelectedContentSeeds(landmarkSeeds, attractionContentIds);
    }

    private Map<String, LandmarkSeed> readLandmarkSeeds(String path) {
        Resource resource = resourceLoader.getResource(path);
        Map<String, LandmarkSeed> landmarkSeeds = new LinkedHashMap<>();
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = csvParser(reader)) {
            validateHeader(
                    parser,
                    "랜드마크",
                    List.of(CONTENT_ID_HEADER, RARITY_HEADER, CARD_URL_HEADER)
            );
            for (CSVRecord record : parser) {
                String contentId = requiredContentId(record, "랜드마크");
                CardTier cardTier = parseCardTier(record, contentId);
                String cardUrl = record.get(CARD_URL_HEADER).trim();
                if (StringUtils.hasText(cardUrl) && !cardUrl.startsWith("https://")) {
                    throw new InvalidSelectedContentSeedException(
                            "랜드마크 CSV card_url은 HTTPS URL이어야 합니다: " + contentId
                    );
                }
                LandmarkSeed previous = landmarkSeeds.putIfAbsent(
                        contentId,
                        new LandmarkSeed(contentId, cardTier, cardUrl)
                );
                if (previous != null) {
                    throw duplicateContentId("랜드마크", contentId);
                }
            }
            return landmarkSeeds;
        } catch (IOException | IllegalArgumentException exception) {
            throw convertReadException("랜드마크", exception);
        }
    }

    /**
     * 지정된 CSV에서 contentId를 읽고 빈 값과 파일 내부 중복을 검증합니다.
     *
     * @param path CSV 리소스 경로
     * @param label 오류 메시지에 사용할 선정 유형 이름
     * @return CSV 순서를 유지하는 contentId 집합
     * @throws InvalidSelectedContentSeedException CSV를 읽을 수 없거나 값 검증에 실패한 경우
     */
    private Set<String> readContentIds(String path, String label) {
        Resource resource = resourceLoader.getResource(path);
        Set<String> contentIds = new LinkedHashSet<>();
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = csvParser(reader)) {
            validateHeader(parser, label, List.of(CONTENT_ID_HEADER));
            for (CSVRecord record : parser) {
                String contentId = requiredContentId(record, label);
                if (!contentIds.add(contentId)) {
                    throw duplicateContentId(label, contentId);
                }
            }
            return contentIds;
        } catch (IOException | IllegalArgumentException exception) {
            throw convertReadException(label, exception);
        }
    }

    private CSVParser csvParser(Reader reader) throws IOException {
        return CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .get()
                .parse(reader);
    }

    private String requiredContentId(CSVRecord record, String label) {
        String contentId = record.get(CONTENT_ID_HEADER);
        if (!StringUtils.hasText(contentId)) {
            throw new InvalidSelectedContentSeedException(
                    label + " CSV content_id가 비어 있습니다. record=" + record.getRecordNumber()
            );
        }
        return contentId;
    }

    private CardTier parseCardTier(CSVRecord record, String contentId) {
        String rarity = record.get(RARITY_HEADER);
        if (!StringUtils.hasText(rarity)) {
            throw new InvalidSelectedContentSeedException(
                    "랜드마크 CSV rarity가 비어 있습니다: " + contentId
            );
        }
        try {
            return CardTier.valueOf(rarity.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new InvalidSelectedContentSeedException(
                    "지원하지 않는 랜드마크 카드 등급입니다: " + rarity
            );
        }
    }

    private InvalidSelectedContentSeedException duplicateContentId(String label, String contentId) {
        return new InvalidSelectedContentSeedException(
                label + " CSV에 중복 content_id가 있습니다: " + contentId
        );
    }

    private InvalidSelectedContentSeedException convertReadException(
            String label,
            Exception exception
    ) {
        if (exception instanceof InvalidSelectedContentSeedException seedException) {
            return seedException;
        }
        return new InvalidSelectedContentSeedException(
                label + " CSV를 읽거나 검증할 수 없습니다.",
                exception
        );
    }

    /**
     * CSV 헤더가 content_id 한 열로만 구성됐는지 검증합니다.
     *
     * @param parser 헤더를 읽은 CSV 파서
     * @param label 오류 메시지에 사용할 선정 유형 이름
     * @throws InvalidSelectedContentSeedException 허용하지 않는 헤더가 포함된 경우
     */
    private void validateHeader(CSVParser parser, String label, List<String> expectedHeaders) {
        if (!parser.getHeaderNames().equals(expectedHeaders)) {
            throw new InvalidSelectedContentSeedException(
                    label + " CSV 헤더가 올바르지 않습니다. expected=" + expectedHeaders
            );
        }
    }
}
