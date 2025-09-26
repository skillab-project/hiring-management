package com.example.hiringProcess.StepScore;

public class StepMetricsItemDTO {
    private Integer stepId;
    private int totalQuestions;
    private int ratedQuestions;
    private Integer averageScore;

    public StepMetricsItemDTO() {}

    public StepMetricsItemDTO(Integer stepId, int totalQuestions, int ratedQuestions, Integer averageScore) {
        this.stepId = stepId;
        this.totalQuestions = totalQuestions;
        this.ratedQuestions = ratedQuestions;
        this.averageScore = averageScore;
    }

    // Getters & Setters

    public Integer getStepId() { return stepId; }
    public void setStepId(Integer stepId) { this.stepId = stepId; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getRatedQuestions() { return ratedQuestions; }
    public void setRatedQuestions(int ratedQuestions) { this.ratedQuestions = ratedQuestions; }

    public Integer getAverageScore() { return averageScore; }
    public void setAverageScore(Integer averageScore) { this.averageScore = averageScore; }
}