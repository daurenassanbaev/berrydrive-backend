package kz.berrydrive.file.controller;

import jakarta.validation.Valid;
import kz.berrydrive.common.constant.RestEndpointPrefixes;
import kz.berrydrive.file.dto.FileWithMetadata;
import kz.berrydrive.file.dto.request.RenameFileRequestDto;
import kz.berrydrive.file.dto.response.FileResponseDto;
import kz.berrydrive.file.dto.response.SharedLinkResponseDto;
import kz.berrydrive.file.service.FileService;
import kz.berrydrive.file.service.SharedLinkService;
import kz.berrydrive.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(RestEndpointPrefixes.API + "/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final SharedLinkService sharedLinkService;

    @GetMapping
    public ResponseEntity<List<FileResponseDto>> listUserFiles(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(fileService.getUserFiles(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        FileWithMetadata metadata = fileService.downloadFile(id, user);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .body(metadata.getResource());
    }

    @PostMapping
    public ResponseEntity<FileResponseDto> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(fileService.uploadFile(file, user));
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<SharedLinkResponseDto> shareFile(@PathVariable Long id,
                                                           @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(sharedLinkService.getSharedLink(id, user));
    }

    @PatchMapping("/{id}/rename")
    public ResponseEntity<FileResponseDto> renameFile(
            @PathVariable Long id,
            @RequestBody @Valid RenameFileRequestDto request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(fileService.renameFile(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        fileService.deleteFile(id, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/unshare")
    public ResponseEntity<Void> unshareSharedLink(@PathVariable Long id,
                                                  @AuthenticationPrincipal User user) {
        sharedLinkService.unshareSharedLink(id, user);
        return ResponseEntity.noContent().build();
    }
}
