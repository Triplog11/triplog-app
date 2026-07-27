package triplog.backend.bookmark.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import triplog.backend.bookmark.entity.Bookmark;
import triplog.backend.bookmark.entity.BookmarkType;
import java.util.Optional;

/**
 * 북마크 엔티티의 데이터 접근을 담당하는 JPA Repository입니다.
 */
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * 북마크 ID와 사용자 ID로 북마크를 조회합니다.
     *
     * @param bookmarkId 북마크 ID
     * @param usersId 사용자 ID
     * @return 북마크 정보, 존재하지 않으면 빈 값
     */
    Optional<Bookmark> findByBookmarkIdAndUsersUsersId(Long bookmarkId, String usersId);

    /**
     * 사용자 ID, 북마크 타입, 대상 식별자로 이미 등록된 북마크가 있는지 확인합니다.
     *
     * @param usersId 사용자 ID
     * @param bookmarkType 북마크 타입
     * @param bookmarkIdentifier 북마크 대상 식별자
     * @return 이미 등록되어 있으면 true
     */
    boolean existsByUsersUsersIdAndBookmarkTypeAndBookmarkIdentifier(
            String usersId, BookmarkType bookmarkType, Long bookmarkIdentifier
    );

    /**
     * 사용자 ID와 북마크 타입으로 북마크 목록을 페이지 단위로 조회합니다.
     *
     * @param usersId 사용자 ID
     * @param bookmarkType 북마크 타입
     * @param pageable 페이지네이션 정보
     * @return 북마크 페이지
     */
    Page<Bookmark> findByUsersUsersIdAndBookmarkType(String usersId, BookmarkType bookmarkType, Pageable pageable);
}
