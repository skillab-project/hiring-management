package com.example.hiringProcess.SkillScore;

public record SkillScoreResponseDTO(
        long id,
        int candidateId,
        int questionId,
        int skillId,
        Integer score,
        String comment,
        boolean created
) {}
