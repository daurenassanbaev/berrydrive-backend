package kz.berrydrive.file.service;

import kz.berrydrive.file.dto.FileWithMetadata;
import kz.berrydrive.file.dto.request.RenameFileRequestDto;
import kz.berrydrive.file.dto.response.FileResponseDto;
import kz.berrydrive.file.entity.File;
import kz.berrydrive.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    FileResponseDto uploadFile(MultipartFile file, User user);
    List<FileResponseDto> getUserFiles(User user);
    FileWithMetadata downloadFile(Long fileId, User user);
    void deleteFile(Long fileId, User user);
    FileResponseDto renameFile(Long fileId, RenameFileRequestDto newFilename, User user);
    void setIsPublic(Boolean isPublic, File file);
    File findByIdAndUserId(Long fileId, Long userId);
}
