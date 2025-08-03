package kz.berrydrive.file.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameFileRequestDto {
    @NotBlank
    private String newFilename;
}
