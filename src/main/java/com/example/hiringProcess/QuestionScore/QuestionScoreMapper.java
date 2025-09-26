package com.example.hiringProcess.QuestionScore;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuestionScoreMapper {

    @Mapping(target = "questionId",   source = "questionId")
    @Mapping(target = "totalSkills",  source = "totalSkills")
    @Mapping(target = "ratedSkills",  source = "ratedSkills")
    @Mapping(target = "averageScore", source = "averageScore")
    QuestionMetricsItemDTO toDto(Integer questionId, int totalSkills, int ratedSkills, Integer averageScore);
}
