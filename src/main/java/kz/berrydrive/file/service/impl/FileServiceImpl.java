package kz.berrydrive.file.service.impl;

import kz.berrydrive.common.exception.NotFoundException;
import kz.berrydrive.file.config.properties.S3Properties;
import kz.berrydrive.file.dto.FileWithMetadata;
import kz.berrydrive.file.dto.request.RenameFileRequestDto;
import kz.berrydrive.file.dto.response.FileResponseDto;
import kz.berrydrive.file.entity.File;
import kz.berrydrive.file.exception.FileStorageException;
import kz.berrydrive.file.repository.FileRepository;
import kz.berrydrive.file.service.FileService;
import kz.berrydrive.file.service.S3Service;
import kz.berrydrive.file.util.S3KeyUtils;
import kz.berrydrive.file.util.mapper.FileMapper;
import kz.berrydrive.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final S3Service s3Service;
    private final FileMapper fileMapper;
    private final S3Properties s3Properties;

    @Override
    public FileResponseDto uploadFile(MultipartFile file, User user) {
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Файл пустой");
            }

            String generatedKey = S3KeyUtils.generateKey(file.getOriginalFilename(), user.getId());
            log.info("Uploading file '{}' to bucket '{}'", generatedKey, s3Properties.getBucket());

            s3Service.uploadFile(file, generatedKey);

            File createdFile = createFile(file, user, generatedKey);
            return fileMapper.toFileResponseDto(createdFile);

        } catch (IOException e) {
            throw new FileStorageException("Failed to read file content", e);
        } catch (S3Exception e) {
            throw new FileStorageException("Failed to upload file to S3", e);
        } catch (Exception e) {
            throw new FileStorageException("File upload failed", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<FileResponseDto> getUserFiles(User user) {
        return fileMapper.toFileResponseDtoList(
                fileRepository.findAllByUserId(user.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public FileWithMetadata downloadFile(Long fileId, User user) {
        File foundFile = findByIdAndUserId(fileId, user.getId());

        try {
            byte[] fileBytes = s3Service.downloadFile(foundFile.getS3Key());

            Resource resource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return foundFile.getFilename();
                }
            };

            return buildFileWithMetadata(resource, foundFile.getContentType());
        } catch (S3Exception e) {
            throw new FileStorageException("Failed to download file from S3: " + foundFile.getS3Key(), e);
        }
    }

    @Override
    public void deleteFile(Long fileId, User user) {
        File foundFile = findByIdAndUserId(fileId, user.getId());
        try {
            s3Service.deleteFile(foundFile.getS3Key());
            fileRepository.deleteById(fileId);
        } catch (S3Exception e) {
            throw new FileStorageException("Failed to delete file from S3: " + foundFile.getS3Key(), e);
        }
    }

    @Override
    public FileResponseDto renameFile(Long fileId, RenameFileRequestDto renameFileRequestDto, User user) {
        File foundFile = findByIdAndUserId(fileId, user.getId());
        String newFileName = renameFileRequestDto.getNewFilename();
        String newKey = S3KeyUtils.generateKey(newFileName, user.getId());
        try {
            return fileMapper.toFileResponseDto(renameFile(foundFile, newFileName, newKey));
        } catch (S3Exception e) {
            throw new FileStorageException("Failed to rename file in S3", e);
        }
    }

    @Override
    public void setIsPublic(Boolean isPublic, File file) {
        file.setPublic(isPublic);
        fileRepository.save(file);
    }

    @Override
    public File findByIdAndUserId(Long id, Long userId) {
        return fileRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("File with ID %d not found".formatted(id)));
    }

    private File renameFile(File foundFile, String newFileName, String newKey) {
        s3Service.renameFile(foundFile.getS3Key(), newKey);

        foundFile.setFilename(newFileName);
        foundFile.setS3Key(newKey);

        return fileRepository.save(foundFile);
    }

    private File createFile(MultipartFile file, User user, String generatedKey) {
        String filename = file.getOriginalFilename();
        return fileRepository.save(
                File.builder()
                        .user(user)
                        .filename(filename)
                        .s3Key(generatedKey)
                        .fileSize(file.getSize())
                        .contentType(file.getContentType())
                        .uploadedAt(LocalDateTime.now())
                        .build());
    }

    private FileWithMetadata buildFileWithMetadata(Resource resource, String contentType) {
        return FileWithMetadata.builder()
                .resource(resource)
                .contentType(contentType)
                .build();
    }
}
