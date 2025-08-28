package com.example.hiringProcess.SkillScore;

public record SkillScoreUpsertRequestDTO(
        int candidateId,
        int questionId,
        int skillId,
        Integer score,   // 0..100
        String comment,
        String ratedBy   // <-- 6ο πεδίο
) {}
