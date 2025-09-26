package com.example.hiringProcess.StepScore;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StepScoreMapper {

    @Mapping(target = "stepId",         source = "stepId")
    @Mapping(target = "totalQuestions", source = "totalQ")
    @Mapping(target = "ratedQuestions", source = "ratedQ")
    @Mapping(target = "averageScore",
            expression = "java( avg == null ? null : (int) Math.round(avg) )")
    StepMetricsItemDTO toDto(Integer stepId, int totalQ, int ratedQ, Double avg);
}
