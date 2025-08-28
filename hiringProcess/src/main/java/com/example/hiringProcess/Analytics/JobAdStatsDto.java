package com.example.hiringProcess.Analytics;

import java.util.List;

public class JobAdStatsDto {

    private double approvalRate;
    private double rejectionRate;
    private double hireRate;
    private double avgCandidateScore;

    private List<ScoreBucketDto> scoreDistribution;
    private List<StepAvgDto> stepAverages;            // Avg Score per Step
    private List<QuestionAvgDto> questionDifficulty;  // Avg score per question
    private List<SkillAvgDto> skillDifficulty;        // Avg score per skill

    public JobAdStatsDto() {}

    public JobAdStatsDto(double approvalRate,
                         double rejectionRate,
                         double hireRate,
                         double avgCandidateScore,
                         List<ScoreBucketDto> scoreDistribution,
                         List<StepAvgDto> stepAverages,
                         List<QuestionAvgDto> questionDifficulty,
                         List<SkillAvgDto> skillDifficulty) {
        this.approvalRate = approvalRate;
        this.rejectionRate = rejectionRate;
        this.hireRate = hireRate;
        this.avgCandidateScore = avgCandidateScore;
        this.scoreDistribution = scoreDistribution;
        this.stepAverages = stepAverages;
        this.questionDifficulty = questionDifficulty;
        this.skillDifficulty = skillDifficulty;
    }

    public double getApprovalRate() { return approvalRate; }
    public void setApprovalRate(double approvalRate) { this.approvalRate = approvalRate; }

    public double getRejectionRate() { return rejectionRate; }
    public void setRejectionRate(double rejectionRate) { this.rejectionRate = rejectionRate; }

    public double getHireRate() { return hireRate; }
    public void setHireRate(double hireRate) { this.hireRate = hireRate; }

    public double getAvgCandidateScore() { return avgCandidateScore; }
    public void setAvgCandidateScore(double avgCandidateScore) { this.avgCandidateScore = avgCandidateScore; }

    public List<ScoreBucketDto> getScoreDistribution() { return scoreDistribution; }
    public void setScoreDistribution(List<ScoreBucketDto> scoreDistribution) { this.scoreDistribution = scoreDistribution; }

    public List<StepAvgDto> getStepAverages() { return stepAverages; }
    public void setStepAverages(List<StepAvgDto> stepAverages) { this.stepAverages = stepAverages; }

    public List<QuestionAvgDto> getQuestionDifficulty() { return questionDifficulty; }
    public void setQuestionDifficulty(List<QuestionAvgDto> questionDifficulty) { this.questionDifficulty = questionDifficulty; }

    public List<SkillAvgDto> getSkillDifficulty() { return skillDifficulty; }
    public void setSkillDifficulty(List<SkillAvgDto> skillDifficulty) { this.skillDifficulty = skillDifficulty; }
}
