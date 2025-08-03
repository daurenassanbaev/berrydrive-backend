package kz.berrydrive.file.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileInfoResponseDto {
    private String filename;
    private Long fileSize;
    private String contentType;
}
