package com.example.hiringProcess.Assessment;

public record StepAssessmentDTO(
        int stepId,
        String stepTitle,
        int totalQuestions,
        int ratedQuestions,   // fully-rated only (όλα τα skills του question έχουν score)
        Double averageScore   // avg των question averages από fully-rated questions
) {}
