package kz.berrydrive.file.controller;

import kz.berrydrive.common.constant.RestEndpointPrefixes;
import kz.berrydrive.file.dto.FileWithMetadata;
import kz.berrydrive.file.dto.response.FileInfoResponseDto;
import kz.berrydrive.file.service.SharedLinkService;
import kz.berrydrive.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestEndpointPrefixes.API + "/public")
@RequiredArgsConstructor
public class PublicFileController {

    private final SharedLinkService sharedLinkService;

    @GetMapping("/{slug}")
    public ResponseEntity<Resource> downloadBySharedLink(@PathVariable String slug) {
        FileWithMetadata metadata = sharedLinkService.downloadBySharedLink(slug);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .body(metadata.getResource());
    }

    @GetMapping("/{slug}/info")
    public ResponseEntity<FileInfoResponseDto> getFileInfo(@PathVariable String slug) {
        return ResponseEntity.ok(sharedLinkService.getFileInfo(slug));
    }

}
