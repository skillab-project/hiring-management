package com.example.hiringProcess.Analytics;

import java.util.List;

public class SkillStatsDto {
    private double avgSkillScore;            // μέση βαθμολογία δεξιότητας (0..10)
    private double passRate;                 // % υποψηφίων με avg >= 5.0
    private List<ScoreBucketDto> distribution; // histogram 0–100 ανά 10άδες

    public SkillStatsDto() {}

    public SkillStatsDto(double avgSkillScore, double passRate, List<ScoreBucketDto> distribution) {
        this.avgSkillScore = avgSkillScore;
        this.passRate = passRate;
        this.distribution = distribution;
    }

    public double getAvgSkillScore() { return avgSkillScore; }
    public void setAvgSkillScore(double avgSkillScore) { this.avgSkillScore = avgSkillScore; }

    public double getPassRate() { return passRate; }
    public void setPassRate(double passRate) { this.passRate = passRate; }

    public List<ScoreBucketDto> getDistribution() { return distribution; }
    public void setDistribution(List<ScoreBucketDto> distribution) { this.distribution = distribution; }
}
