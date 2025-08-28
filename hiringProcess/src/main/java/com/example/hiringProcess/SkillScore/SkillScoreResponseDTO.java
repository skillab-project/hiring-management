package com.example.hiringProcess.SkillScore;

import java.time.Instant;

/** Response προς το front για skill score */
public record SkillScoreResponseDTO(
        long id,
        int candidateId,
        int questionId,
        int skillId,
        Integer score,
        String comment,
        Instant ratedAt,
        String ratedBy,
        boolean created   // true = πρώτη φορά (Saved), false = update (Modified)
) {}
