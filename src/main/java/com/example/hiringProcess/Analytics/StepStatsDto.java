package com.example.hiringProcess.Analytics;

import java.util.List;

public class StepStatsDto {
    private double passRate;                        // % υποψηφίων με avg score >= 50 (0–100 scale)
    private long passCount;                         // αριθμός υποψηφίων που πέρασαν (avg >= 50)
    private double avgStepScore;                    // μέση βαθμολογία όλων των ερωτήσεων του step (0–100)
    private List<ScoreBucketDto> scoreDistribution; // histogram 0..100
    private List<QuestionAvgDto> questionRanking;   // ευκολότερη -> δυσκολότερη (desc)
    private List<SkillAvgDto> skillRanking;         // ευκολότερη -> δυσκολότερη (desc)

    public StepStatsDto() {}

    public StepStatsDto(double passRate,
                        long passCount,
                        double avgStepScore,
                        List<ScoreBucketDto> scoreDistribution,
                        List<QuestionAvgDto> questionRanking,
                        List<SkillAvgDto> skillRanking) {
        this.passRate = passRate;
        this.passCount = passCount;
        this.avgStepScore = avgStepScore;
        this.scoreDistribution = scoreDistribution;
        this.questionRanking = questionRanking;
        this.skillRanking = skillRanking;
    }

    public double getPassRate() {
        return passRate;
    }

    public void setPassRate(double passRate) {
        this.passRate = passRate;
    }

    public long getPassCount() {
        return passCount;
    }

    public void setPassCount(long passCount) {
        this.passCount = passCount;
    }

    public double getAvgStepScore() {
        return avgStepScore;
    }

    public void setAvgStepScore(double avgStepScore) {
        this.avgStepScore = avgStepScore;
    }

    public List<ScoreBucketDto> getScoreDistribution() {
        return scoreDistribution;
    }

    public void setScoreDistribution(List<ScoreBucketDto> scoreDistribution) {
        this.scoreDistribution = scoreDistribution;
    }

    public List<QuestionAvgDto> getQuestionRanking() {
        return questionRanking;
    }

    public void setQuestionRanking(List<QuestionAvgDto> questionRanking) {
        this.questionRanking = questionRanking;
    }

    public List<SkillAvgDto> getSkillRanking() {
        return skillRanking;
    }

    public void setSkillRanking(List<SkillAvgDto> skillRanking) {
        this.skillRanking = skillRanking;
    }
}
