package com.example.hiringProcess.SkillScore;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillScoreMapper {

    // Entity -> ResponseDTO
    @Mapping(source = "candidate.id", target = "candidateId")
    @Mapping(source = "question.id",  target = "questionId")
    @Mapping(source = "skill.id",     target = "skillId")
    SkillScoreResponseDTO toResponseDTO(SkillScore entity);

    // RequestDTO -> Entity (για NEW)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "question",  ignore = true)
    @Mapping(target = "skill",     ignore = true)
    SkillScore toNewEntity(SkillScoreUpsertRequestDTO dto);

    // Helper: πάρε ένα base DTO και γράψε το created flag
    default SkillScoreResponseDTO withCreated(SkillScoreResponseDTO base, boolean created) {
        return new SkillScoreResponseDTO(
                base.id(),
                base.candidateId(),
                base.questionId(),
                base.skillId(),
                base.score(),
                base.comment(),
                created
        );
    }
}
