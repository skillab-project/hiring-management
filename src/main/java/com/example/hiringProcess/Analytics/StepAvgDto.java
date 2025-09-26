package com.example.hiringProcess.Analytics;

public class StepAvgDto {
    private String step;
    private Double averageScore;

    public StepAvgDto() {}
    public StepAvgDto(String step, Double averageScore) {
        this.step = step;
        this.averageScore = averageScore;
    }

    public String getStep() { return step; }
    public void setStep(String step) { this.step = step; }

    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
}