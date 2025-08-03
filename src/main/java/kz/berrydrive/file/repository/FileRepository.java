package kz.berrydrive.file.repository;

import kz.berrydrive.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByIdAndUserId(Long id, Long userId);

    List<File> findAllByUserId(Long id);
}
