package com.example.hiringProcess.Analytics;

public class OccupationAvgDto {
    private String occupation;
    private Double averageScore;

    public OccupationAvgDto() {}

    public OccupationAvgDto(String occupation, Double averageScore) {
        this.occupation = occupation;
        this.averageScore = averageScore;
    }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
}