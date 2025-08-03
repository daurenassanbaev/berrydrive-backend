package kz.berrydrive.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    void uploadFile(MultipartFile file, String keyName) throws IOException;
    byte[] downloadFile(String keyName);
    void deleteFile(String keyName);
    void renameFile(String oldKey, String newKey);
}
