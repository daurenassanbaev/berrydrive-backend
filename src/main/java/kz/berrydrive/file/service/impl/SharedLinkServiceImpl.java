package kz.berrydrive.file.service.impl;

import kz.berrydrive.common.exception.NotFoundException;
import kz.berrydrive.file.dto.FileWithMetadata;
import kz.berrydrive.file.dto.response.FileInfoResponseDto;
import kz.berrydrive.file.dto.response.SharedLinkResponseDto;
import kz.berrydrive.file.entity.File;
import kz.berrydrive.file.entity.SharedLink;
import kz.berrydrive.file.exception.FileStorageException;
import kz.berrydrive.file.repository.SharedLinkRepository;
import kz.berrydrive.file.service.FileService;
import kz.berrydrive.file.service.SharedLinkService;
import kz.berrydrive.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SharedLinkServiceImpl implements SharedLinkService {

    private static final String PUBLIC_LINK_FORMAT = "%s/%s";

    private final SharedLinkRepository linkRepository;
    private final FileService fileService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public SharedLinkResponseDto getSharedLink(Long fileId, User user) {
        File foundFile = fileService.findByIdAndUserId(fileId, user.getId());

        try {
            Optional<SharedLink> existing = findByFileIdAndUserId(foundFile.getId(), user.getId());
            if (existing.isPresent()) {
                return buildSharedLinkResponseDto(existing.get().getSlug());
            }

            String generatedSlug = UUID.randomUUID().toString();
            SharedLink sharedLink = SharedLink.builder()
                    .slug(generatedSlug)
                    .user(user)
                    .file(foundFile)
                    .createdAt(LocalDateTime.now())
                    .build();

            linkRepository.save(sharedLink);
            fileService.setIsPublic(true, foundFile);

            return buildSharedLinkResponseDto(generatedSlug);

        } catch (Exception e) {
            throw new FileStorageException("Failed to create public link for file ID %d".formatted(fileId), e);
        }
    }

    @Override
    public FileWithMetadata downloadBySharedLink(String slug) {
        return findBySlug(slug)
                .map(sharedLink -> {
                    try {
                        Long fileId = sharedLink.getFile().getId();
                        return fileService.downloadFile(fileId, sharedLink.getUser());
                    } catch (Exception e) {
                        throw new FileStorageException("Failed to download file by public link", e);
                    }
                })
                .orElseThrow(() -> new NotFoundException("Public link %s not found".formatted(slug)));
    }

    @Override
    @Transactional(readOnly = true)
    public FileInfoResponseDto getFileInfo(String slug) {
        return findBySlug(slug)
                .map(sharedLink -> buildFileInfoResponseDto(sharedLink.getFile()))
                .orElseThrow(() -> new NotFoundException("Public link %s not found".formatted(slug)));
    }

    @Override
    public void unshareSharedLink(Long fileId, User user) {
        File foundFile = fileService.findByIdAndUserId(fileId, user.getId());
        try {
            fileService.setIsPublic(false, foundFile);
            linkRepository.deleteByFileIdAndUserId(foundFile.getId(), user.getId());
        } catch (Exception e) {
            throw new FileStorageException("Failed to remove public link for file ID %d".formatted(fileId), e);
        }
    }

    private Optional<SharedLink> findByFileIdAndUserId(Long fileId, Long userId) {
        return linkRepository.findByFileIdAndUserId(fileId, userId);
    }

    private Optional<SharedLink> findBySlug(String slug) {
        return linkRepository.findBySlug(slug);
    }

    private SharedLinkResponseDto buildSharedLinkResponseDto(String slug) {
        return SharedLinkResponseDto.builder()
                .link(PUBLIC_LINK_FORMAT.formatted(baseUrl, slug))
                .build();
    }

    private FileInfoResponseDto buildFileInfoResponseDto(File file) {
        return FileInfoResponseDto.builder()
                .filename(file.getFilename())
                .fileSize(file.getFileSize())
                .contentType(file.getContentType())
                .build();
    }
}
