package com.example.hiringProcess.Step;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StepMapper {

    // Entity -> ResponseDTO
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    StepResponseDTO toResponseDTO(Step step);

    // Entity -> QuestionsDTO (για left panel / candidates tab)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    StepQuestionsDTO toQuestionsDTO(Step step);



    // DTO -> Entity (για update/patch)
    Step toEntity(StepUpdateDTO dto);
}
