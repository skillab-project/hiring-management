package com.example.hiringProcess.Analytics;

import java.util.List;

public class QuestionStatsDto {
    public double avgQuestionScore;           // μέσος όρος όλων των skills της ερώτησης
    public double passRate;                   // % υποψηφίων με avg >= 5.0 (50%)
    public SkillAvgDto bestSkill;             // υψηλότερο avg στα skills της ερώτησης
    public SkillAvgDto worstSkill;            // χαμηλότερο avg στα skills της ερώτησης
    public List<ScoreBucketDto> distribution; // κατανομή 0–100 ανά 10άδες, μετρημένη σε υποψηφίους

    public QuestionStatsDto(double avgQuestionScore,
                            double passRate,
                            SkillAvgDto bestSkill,
                            SkillAvgDto worstSkill,
                            List<ScoreBucketDto> distribution) {
        this.avgQuestionScore = avgQuestionScore;
        this.passRate = passRate;
        this.bestSkill = bestSkill;
        this.worstSkill = worstSkill;
        this.distribution = distribution;
    }
}