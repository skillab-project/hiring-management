package com.example.hiringProcess.Assessment;

/**
 * Σύνοψη ανά Question για συγκεκριμένο υποψήφιο.
 * - ratedSkills: πόσα skills έχουν βαθμολογηθεί
 * - totalSkills: πόσα skills έχει η ερώτηση
 * - averageScore: avg των skill scores
 */
public record QuestionAssessmentDTO(
        int questionId,
        String questionTitle,
        int ratedSkills,
        int totalSkills,
        Double averageScore
) {}
