package kz.berrydrive.file.service;

import kz.berrydrive.file.dto.FileWithMetadata;
import kz.berrydrive.file.dto.response.FileInfoResponseDto;
import kz.berrydrive.file.dto.response.SharedLinkResponseDto;
import kz.berrydrive.user.entity.User;

public interface SharedLinkService {
    SharedLinkResponseDto getSharedLink(Long fileId, User user);
    FileWithMetadata downloadBySharedLink(String slug);
    FileInfoResponseDto getFileInfo(String slug);
    void unshareSharedLink(Long fileId, User user);
}
