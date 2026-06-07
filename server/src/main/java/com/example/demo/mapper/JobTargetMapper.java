package com.example.demo.mapper;

import com.example.demo.dto.ScrapedJobResponse;
import com.example.demo.models.JobTarget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobTargetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    JobTarget toEntity(ScrapedJobResponse dto);
}
