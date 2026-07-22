package triplog.backend.batch.tourapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import triplog.backend.event.service.EventSyncData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TourAPI 축제 소개정보 응답을 Event 동기화 입력값으로 변환합니다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TourApiEventIntroItem(
        @JsonProperty("contentid") String contentId,
        @JsonProperty("contenttypeid") String contentTypeId,
        @JsonProperty("eventstartdate") String eventStartDate,
        @JsonProperty("eventenddate") String eventEndDate,
        @JsonProperty("eventplace") String eventPlace,
        @JsonProperty("playtime") String playTime,
        @JsonProperty("agelimit") String ageLimit,
        @JsonProperty("usetimefestival") String usageFee,
        @JsonProperty("sponsor1") String sponsorName,
        @JsonProperty("sponsor1tel") String sponsorTelephone,
        @JsonProperty("progressType") String progressType,
        @JsonProperty("festivalType") String festivalType,
        @JsonProperty("program") String program,
        @JsonProperty("spendtimefestival") String spendTime
) {
    private static final DateTimeFormatter EVENT_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    /**
     * 문자열 날짜와 축제 전용 필드를 Event 저장 입력값으로 변환합니다.
     *
     * @return Event 동기화 입력값
     */
    public EventSyncData toSyncData() {
        Map<String, Object> detailData = new LinkedHashMap<>();
        putIfPresent(detailData, "program", program);
        putIfPresent(detailData, "spendTime", spendTime);
        return new EventSyncData(
                parseDate(eventStartDate),
                parseDate(eventEndDate),
                eventPlace,
                playTime,
                ageLimit,
                usageFee,
                sponsorName,
                sponsorTelephone,
                progressType,
                festivalType,
                Map.copyOf(detailData)
        );
    }

    /**
     * TourAPI 날짜 문자열을 날짜 객체로 변환합니다.
     *
     * @param value yyyyMMdd 형식의 날짜 문자열
     * @return 변환한 날짜 또는 입력값이 비어 있으면 {@code null}
     */
    private LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value, EVENT_DATE);
    }

    /**
     * 값이 존재하는 상세 항목만 부가정보 Map에 추가합니다.
     *
     * @param detailData 부가정보를 저장할 Map
     * @param key 저장할 항목 이름
     * @param value 저장할 항목 값
     */
    private void putIfPresent(Map<String, Object> detailData, String key, String value) {
        if (value != null && !value.isBlank()) {
            detailData.put(key, value);
        }
    }
}
