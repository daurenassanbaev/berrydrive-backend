package kz.berrydrive.file.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class FileResponseDto {
    private Long id;
    private String filename;
    private Long fileSize;
    private boolean isPublic;
    private String contentType;
    private LocalDateTime uploadedAt;
}
