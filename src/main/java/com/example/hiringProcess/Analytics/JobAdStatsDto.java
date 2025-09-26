package com.example.hiringProcess.Analytics;

import java.util.List;

public class JobAdStatsDto {
    private double approvalRate;
    private double rejectionRate;

    // ΝΕΑ πεδία
    private double hireRate;
    private long   hireCount;

    private double avgCandidateScore;

    private List<ScoreBucketDto> scoreDistribution;
    private List<StepAvgDto>     stepAverages;
    private List<QuestionAvgDto> questionDifficulty;
    private List<SkillAvgDto>    skillDifficulty;

    private long totalCandidates;
    private boolean complete;

    public JobAdStatsDto() {}

    // ΝΕΟΣ constructor (προστέθηκαν hireRate, hireCount)
    public JobAdStatsDto(
            double approvalRate,
            double rejectionRate,
            double hireRate,
            long hireCount,
            double avgCandidateScore,
            List<ScoreBucketDto> scoreDistribution,
            List<StepAvgDto> stepAverages,
            List<QuestionAvgDto> questionDifficulty,
            List<SkillAvgDto> skillDifficulty,
            long totalCandidates,
            boolean complete
    ) {
        this.approvalRate = approvalRate;
        this.rejectionRate = rejectionRate;
        this.hireRate = hireRate;
        this.hireCount = hireCount;
        this.avgCandidateScore = avgCandidateScore;
        this.scoreDistribution = scoreDistribution;
        this.stepAverages = stepAverages;
        this.questionDifficulty = questionDifficulty;
        this.skillDifficulty = skillDifficulty;
        this.totalCandidates = totalCandidates;
        this.complete = complete;
    }

    public double getApprovalRate() { return approvalRate; }
    public void setApprovalRate(double approvalRate) { this.approvalRate = approvalRate; }

    public double getRejectionRate() { return rejectionRate; }
    public void setRejectionRate(double rejectionRate) { this.rejectionRate = rejectionRate; }

    // GETTERS/SETTERS για τα νέα πεδία
    public double getHireRate() { return hireRate; }
    public void setHireRate(double hireRate) { this.hireRate = hireRate; }

    public long getHireCount() { return hireCount; }
    public void setHireCount(long hireCount) { this.hireCount = hireCount; }

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

    public long getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(long totalCandidates) { this.totalCandidates = totalCandidates; }

    public boolean isComplete() { return complete; }
    public void setComplete(boolean complete) { this.complete = complete; }
}
