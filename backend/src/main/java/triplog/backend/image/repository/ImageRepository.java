package triplog.backend.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.image.entity.Image;

/**
 * Image 영속성 처리를 담당하는 Repository입니다.
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}
