package triplog.backend.region.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 법정동 시군구 단위의 Triplog 지역을 관리하는 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "region",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_region_legal_code",
                columnNames = {"legal_region_code", "legal_district_code"}
        )
)
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "region_name", nullable = false, length = 100)
    private String regionName;

    @Column(name = "region_overview", columnDefinition = "text")
    private String regionOverview;

    @Column(name = "legal_region_code", nullable = false, length = 10)
    private String legalRegionCode;

    @Column(name = "legal_district_code", nullable = false, length = 10)
    private String legalDistrictCode;

    /**
     * TourAPI 법정동 코드로 Region을 생성합니다.
     *
     * @param regionName 시군구를 포함한 표시명
     * @param legalRegionCode 법정동 시도 코드
     * @param legalDistrictCode 법정동 시군구 코드
     */
    public Region(String regionName, String legalRegionCode, String legalDistrictCode) {
        this.regionName = regionName;
        this.legalRegionCode = legalRegionCode;
        this.legalDistrictCode = legalDistrictCode;
    }

    /**
     * 법정동 코드 동기화 결과로 지역명을 갱신합니다.
     * 지역 소개는 Triplog 관리 데이터이므로 변경하지 않습니다.
     *
     * @param regionName 최신 시군구 표시명
     */
    public void updateSyncedName(String regionName) {
        this.regionName = regionName;
    }
}
