package triplog.backend.badge.entity;

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
import triplog.backend.users.entity.Users;

/**
 * 사용자가 획득한 배지와 대표 배지 여부를 관리하는 엔티티입니다.
 * <p>
 * 데이터베이스의 {@code users_badge} 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users_badge")
public class UsersBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_badge_id", nullable = false, unique = true)
    private Long usersBadgeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @Column(name = "is_representative", nullable = false)
    private boolean representative;

    /**
     * 사용자가 획득한 배지 관계를 생성합니다.
     *
     * @param users 배지를 획득한 사용자
     * @param badge 획득한 배지
     * @param representative 대표 배지 여부
     */
    public UsersBadge(Users users, Badge badge, boolean representative) {
        this.users = users;
        this.badge = badge;
        this.representative = representative;
    }
}
