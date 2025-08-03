package kz.berrydrive.file.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SharedLinkResponseDto {
    private String link;
}
