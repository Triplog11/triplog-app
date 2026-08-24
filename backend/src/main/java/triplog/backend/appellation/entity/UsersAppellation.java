package triplog.backend.appellation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자가 획득한 칭호와 대표 칭호 여부를 관리합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users_appellation")
public class UsersAppellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_appellation_id", nullable = false)
    private Long usersAppellationId;

    @Column(name = "users_id", nullable = false, length = 36)
    private String usersId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appellation_id", nullable = false)
    private Appellation appellation;

    @Column(name = "is_representative", nullable = false)
    private boolean representative;

    /**
     * 현재 칭호의 대표 칭호 여부를 변경합니다.
     *
     * @param representative 대표 칭호로 사용할지 여부
     */
    public void changeRepresentative(boolean representative) {
        this.representative = representative;
    }
}
