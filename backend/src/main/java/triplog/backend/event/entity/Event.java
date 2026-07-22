package triplog.backend.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import triplog.backend.event.service.EventSyncData;
import triplog.backend.tourismcontent.entity.TourismContent;

import java.time.LocalDate;
import java.util.Map;

/**
 * TourAPI contentTypeId 15의 축제 전용 상세정보 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "event",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_event_tourism_content",
                columnNames = "tourism_content_id"
        )
)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tourism_content_id", nullable = false)
    private TourismContent tourismContent;

    @Column(name = "event_start_date")
    private LocalDate eventStartDate;

    @Column(name = "event_end_date")
    private LocalDate eventEndDate;

    @Column(name = "event_place", length = 500)
    private String eventPlace;

    @Column(name = "play_time", length = 500)
    private String playTime;

    @Column(name = "age_limit", length = 255)
    private String ageLimit;

    @Column(name = "usage_fee", columnDefinition = "text")
    private String usageFee;

    @Column(name = "sponsor_name", length = 255)
    private String sponsorName;

    @Column(name = "sponsor_telephone", length = 255)
    private String sponsorTelephone;

    @Column(name = "progress_type", length = 100)
    private String progressType;

    @Column(name = "festival_type", length = 100)
    private String festivalType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_detail_data", columnDefinition = "json")
    private Map<String, Object> eventDetailData;

    /**
     * 관광 콘텐츠와 축제 상세정보로 Event를 생성합니다.
     *
     * @param tourismContent contentTypeId가 15인 관광 콘텐츠
     * @param syncData 축제 상세정보 동기화 입력값
     */
    public Event(TourismContent tourismContent, EventSyncData syncData) {
        this.tourismContent = tourismContent;
        apply(syncData);
    }

    /**
     * 최신 TourAPI 축제 상세정보를 반영합니다.
     *
     * @param syncData 축제 상세정보 동기화 입력값
     */
    public void update(EventSyncData syncData) {
        apply(syncData);
    }

    private void apply(EventSyncData data) {
        this.eventStartDate = data.eventStartDate();
        this.eventEndDate = data.eventEndDate();
        this.eventPlace = data.eventPlace();
        this.playTime = data.playTime();
        this.ageLimit = data.ageLimit();
        this.usageFee = data.usageFee();
        this.sponsorName = data.sponsorName();
        this.sponsorTelephone = data.sponsorTelephone();
        this.progressType = data.progressType();
        this.festivalType = data.festivalType();
        this.eventDetailData = data.detailData();
    }
}
