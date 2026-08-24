package triplog.backend.appellation.entity;

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
 * 칭호의 표시 정보와 자동 획득 조건을 관리합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "appellation")
public class Appellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appellation_id", nullable = false)
    private Long appellationId;

    @Column(name = "appellation_name", nullable = false, length = 100)
    private String appellationName;

    @Column(name = "appellation_group")
    private Integer appellationGroup;

    @Column(name = "appellation_type", nullable = false, length = 50)
    private String appellationType;

    @Column(name = "appellation_target", nullable = false, length = 50)
    private String appellationTarget;

    @Column(name = "appellation_operator", nullable = false, length = 10)
    private String appellationOperator;

    @Column(name = "appellation_value")
    private Integer appellationValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "appellation_filter", nullable = false, columnDefinition = "json")
    private Map<String, Object> appellationFilter;
}
