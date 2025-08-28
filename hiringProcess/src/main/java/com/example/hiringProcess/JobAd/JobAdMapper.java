package com.example.hiringProcess.JobAd;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobAdMapper {

    // DETAILS: στείλε id, description, status (και ό,τι άλλο θες)
    @Mapping(source = "id",          target = "id")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status",      target = "status")
    JobAdDetailsDTO jobAdToDetailsDTO(JobAd jobAd);

    // SUMMARY (όπως το είχες)
    @Mapping(source = "id",              target = "id")
    @Mapping(source = "title",           target = "jobTitle")
    @Mapping(source = "occupation.title", target = "occupationName")
    @Mapping(source = "status",          target = "status")
    @Mapping(
            target = "departmentName",
            expression = "java(jobAd.getDepartments().stream().findFirst().map(d -> d.getName()).orElse(null))"
    )
    JobAdSummaryDTO jobAdToSummaryDTO(JobAd jobAd);
}
