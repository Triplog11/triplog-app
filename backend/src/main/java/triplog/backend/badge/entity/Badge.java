package triplog.backend.badge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * 배지의 표시 정보와 획득 조건을 관리하는 엔티티입니다.
 * <p>
 * 데이터베이스의 {@code badge} 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "badge")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id", nullable = false, unique = true)
    private Long badgeId;

    @Column(name = "badge_name", nullable = false, length = 100)
    private String badgeName;

    @Column(name = "badge_url", nullable = false, length = 2048)
    private String badgeUrl;

    @Column(name = "badge_group")
    private Integer badgeGroup;

    @Column(name = "badge_type", nullable = false, length = 50)
    private String badgeType;

    @Column(name = "badge_target", nullable = false, length = 50)
    private String badgeTarget;

    @Column(name = "badge_operator", nullable = false, length = 10)
    private String badgeOperator;

    @Column(name = "badge_value")
    private Integer badgeValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "badge_filter", nullable = false, columnDefinition = "json")
    private Map<String, Object> badgeFilter;

    /**
     * 획득 조건을 포함한 배지를 생성합니다.
     *
     * @param badgeUrl 배지 이미지 URL
     * @param badgeName 배지명
     * @param badgeGroup 배지 그룹
     * @param badgeType 배지 타입
     * @param badgeTarget 배지 획득 조건의 대상
     * @param badgeOperator 배지 획득 조건의 연산자
     * @param badgeValue 배지 획득 조건값
     * @param badgeFilter 배지의 세부 필터 조건
     */
    public Badge(String badgeUrl, String badgeName, Integer badgeGroup, String badgeType,
                 String badgeTarget, String badgeOperator, Integer badgeValue,
                 Map<String, Object> badgeFilter) {
        this.badgeUrl = badgeUrl;
        this.badgeName = badgeName;
        this.badgeGroup = badgeGroup;
        this.badgeType = badgeType;
        this.badgeTarget = badgeTarget;
        this.badgeOperator = badgeOperator;
        this.badgeValue = badgeValue;
        this.badgeFilter = badgeFilter;
    }
}
