package com.example.hiringProcess.Occupation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OccupationMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "name")  // Occupation.title -> DTO.name
    OccupationNameDTO toNameDTO(Occupation occupation);
}
