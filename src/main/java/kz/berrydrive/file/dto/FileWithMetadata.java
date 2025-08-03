package kz.berrydrive.file.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
@Builder
public class FileWithMetadata {
    private Resource resource;
    private String contentType;
}
