package kz.berrydrive.file.util.mapper;

import kz.berrydrive.file.dto.response.FileResponseDto;
import kz.berrydrive.file.entity.File;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileMapper {
    FileResponseDto toFileResponseDto(File file);
    List<FileResponseDto> toFileResponseDtoList(List<File> files);
}
