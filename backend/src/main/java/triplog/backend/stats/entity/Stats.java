package triplog.backend.stats.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import triplog.backend.users.entity.Users;

/**
 * 사용자의 통계 정보(Stats)를 관리하는 엔티티 클래스입니다.
 * <p>
 * 데이터베이스의 {@code stats} 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stats")
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stats_id", nullable = false, unique = true)
    private int statsId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "users_id",
            nullable = false,
            unique = true
    )
    private Users users;

    @Column(name = "address_si", nullable = false, length = 20)
    private String addressSi;

    @Column(name = "address_do_gun", nullable = false, length = 20)
    private String addressDoGun;

    @Column(name = "address_gu", nullable = false, length = 30)
    private String addressGu;

    @Column(name = "overall_score", nullable = false)
    private int overallScore;

    @Column(name = "month_score", nullable = false)
    private int monthScore;

    @Column(name = "quarter_score", nullable = false)
    private int quarterScore;

    @Column(name = "current_tier", nullable = false, length = 10)
    private String currentTier;

    @Column(name = "stats_level", nullable = false)
    private int statsLevel;

    @Column(name = "stats_xp", nullable = false)
    private int statsXp;

    public Stats(Users users, String addressSi, String addressDoGun, String addressGu) {
        this.users = users;
        this.addressSi = addressSi;
        this.addressDoGun = addressDoGun;
        this.addressGu = addressGu;
        this.overallScore = 0;
        this.monthScore = 0;
        this.quarterScore = 0;
        this.currentTier = "BRONZE";
        this.statsLevel = 1;
        this.statsXp = 0;
    }
}
