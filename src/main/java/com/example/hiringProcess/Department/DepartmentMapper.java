package com.example.hiringProcess.Department;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    DepartmentNameDTO toNameDTO(Department department);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    Department fromNameDTO(DepartmentNameDTO dto);
}
