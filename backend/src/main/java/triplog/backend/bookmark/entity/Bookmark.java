package triplog.backend.bookmark.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * 북마크 정보를 관리하는 엔티티 클래스입니다.
 * <p>
 * 데이터베이스의 {@code bookmark} 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bookmark")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id", nullable = false, unique = true)
    private Long bookmarkId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "users_id", nullable = false)
    private Users users;

    @Enumerated(EnumType.STRING)
    @Column(name = "bookmark_type", nullable = false)
    private BookmarkType bookmarkType;

    @Column(name = "bookmark_identifier", nullable = false)
    private Long bookmarkIdentifier;

    /**
     * 북마크를 생성합니다.
     *
     * @param users 사용자
     * @param bookmarkType 북마크 타입
     * @param bookmarkIdentifier 북마크 대상 식별자
     */
    public Bookmark(Users users, BookmarkType bookmarkType, Long bookmarkIdentifier) {
        this.users = users;
        this.bookmarkType = bookmarkType;
        this.bookmarkIdentifier = bookmarkIdentifier;
    }
}
