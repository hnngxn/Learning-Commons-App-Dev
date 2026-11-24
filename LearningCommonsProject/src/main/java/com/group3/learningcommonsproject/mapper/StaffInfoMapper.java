package com.group3.learningcommonsproject.mapper;

import com.group3.learningcommonsproject.dto.StaffInfoDto;
import com.group3.learningcommonsproject.entity.StaffInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StaffInfoMapper {
    StaffInfoDto toDto(StaffInfo staffInfo);

    // Staff details updates must preserve program memberships.
    @Mapping(target = "programs", ignore = true)
    @Mapping(target = "id", ignore = true)
    StaffInfo toEntity(StaffInfoDto staffInfoDto);

    // Staff details updates must preserve program memberships.
    @Mapping(target = "programs", ignore = true)
    @Mapping(target = "id", ignore = true)
    void update(StaffInfoDto staffInfoDto, @MappingTarget StaffInfo staffInfo);
}
