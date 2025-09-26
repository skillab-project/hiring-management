package com.example.hiringProcess.Step;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StepMapper {

    // Entity -> ResponseDTO (create/read)
    @Mapping(source = "id",          target = "id")
    @Mapping(source = "title",       target = "title")
    @Mapping(source = "description", target = "description")
    StepResponseDTO toResponseDTO(Step step);

    // Entity -> QuestionsDTO (λίστα αριστερού panel)
    @Mapping(source = "id",    target = "id")
    @Mapping(source = "title", target = "title")
    StepQuestionsDTO toQuestionsDTO(Step step);

    // Προαιρετικά: PATCH ΜΟΝΟ description πάνω σε υπάρχον entity
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "description", target = "description")
    void patchDescription(StepUpdateDTO dto, @MappingTarget Step step);
}