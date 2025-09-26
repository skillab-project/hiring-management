package com.example.hiringProcess.Analytics;

public class JobAdAvgDto {
    private String jobAd;
    private Double averageScore;

    public JobAdAvgDto() {}

    public JobAdAvgDto(String jobAd, Double averageScore) {
        this.jobAd = jobAd;
        this.averageScore = averageScore;
    }

    public String getJobAd() { return jobAd; }
    public void setJobAd(String jobAd) { this.jobAd = jobAd; }

    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
}
