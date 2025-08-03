package kz.berrydrive.file.service.impl;

import kz.berrydrive.file.config.properties.S3Properties;
import kz.berrydrive.file.exception.FileStorageException;
import kz.berrydrive.file.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Properties s3Properties;
    private final S3Client s3Client;

    @Override
    public void uploadFile(MultipartFile file, String keyName) throws IOException {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(s3Properties.getBucket())
                            .key(keyName)
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new FileStorageException("Failed to read file content: " + file.getOriginalFilename(), e);
        } catch (S3Exception e) {
            throw new FileStorageException("Failed to upload file to S3: " + keyName, e);
        }
    }

    @Override
    public byte[] downloadFile(String keyName) {
        try {
            ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(s3Properties.getBucket())
                            .key(keyName)
                            .build());
            return objectAsBytes.asByteArray();
        } catch (NoSuchKeyException e) {
            throw new FileStorageException("File with key '" + keyName + "' not found in S3", e);
        } catch (S3Exception e) {
            throw new FileStorageException("Failed to download file from S3: " + keyName, e);
        }
    }

    @Override
    public void deleteFile(String keyName) {
        try {
            deleteObject(keyName);
        } catch (S3Exception e) {
            throw new FileStorageException("Failed to delete file from S3: " + keyName, e);
        }
    }

    @Override
    public void renameFile(String oldKey, String newKey) {
        try {
            copyObject(oldKey, newKey);
            deleteObject(oldKey);
        } catch (S3Exception e) {
            throw new FileStorageException("Failed to rename file in S3: " + oldKey + " → " + newKey, e);
        }
    }

    private void copyObject(String oldKey, String newKey) {
        s3Client.copyObject(CopyObjectRequest.builder()
                .sourceBucket(s3Properties.getBucket())
                .sourceKey(oldKey)
                .destinationBucket(s3Properties.getBucket())
                .destinationKey(newKey)
                .build());
    }

    private void deleteObject(String keyName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .key(keyName)
                .bucket(s3Properties.getBucket())
                .build());
    }
}
