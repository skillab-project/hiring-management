package com.example.hiringProcess.Analytics;

import java.util.List;

public class QuestionStatsDto {
    public double avgQuestionScore;           // 0..10
    public double passRate;                   // %
    public List<ScoreBucketDto> distribution; // 0–100 histogram
    public List<SkillAvgDto> skillRanking;    // δεξιότητες της ερώτησης ταξινομημένες (desc)

    public QuestionStatsDto(double avgQuestionScore,
                            double passRate,
                            List<ScoreBucketDto> distribution,
                            List<SkillAvgDto> skillRanking) {
        this.avgQuestionScore = avgQuestionScore;
        this.passRate = passRate;
        this.distribution = distribution;
        this.skillRanking = skillRanking;
    }
}
