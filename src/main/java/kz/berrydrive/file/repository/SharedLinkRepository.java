package kz.berrydrive.file.repository;

import kz.berrydrive.file.entity.SharedLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SharedLinkRepository extends JpaRepository<SharedLink, Long> {

    Optional<SharedLink> findByFileIdAndUserId(Long fileId, Long userId);
    void deleteByFileIdAndUserId(Long fileId, Long userId);
    Optional<SharedLink> findBySlug(String slug);
}
