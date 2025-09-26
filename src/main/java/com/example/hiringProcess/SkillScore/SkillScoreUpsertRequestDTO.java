package com.example.hiringProcess.SkillScore;

public record SkillScoreUpsertRequestDTO(
        int candidateId,
        int questionId,
        int skillId,
        Integer score,
        String comment
) {}
