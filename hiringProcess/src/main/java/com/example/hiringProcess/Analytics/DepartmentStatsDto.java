package com.example.hiringProcess.Analytics;

import java.util.List;

public class DepartmentStatsDto {
    private double approvalRate;          // %
    private double rejectionRate;         // %
    private double candidatesPerJobAd;    // μέσος αριθμός υποψηφίων ανά αγγελία

    private List<BucketDto> scoreDistribution;        // 0–10,10–20,...,90–100
    private List<StepAvgDto> stepDifficulty;          // μέσος όρος ανά step
    private List<OccupationAvgDto> occupationDifficulty; // μέσος όρος ανά occupation

    public DepartmentStatsDto() {}

    public DepartmentStatsDto(double approvalRate, double rejectionRate, double candidatesPerJobAd,
                              List<BucketDto> scoreDistribution,
                              List<StepAvgDto> stepDifficulty,
                              List<OccupationAvgDto> occupationDifficulty) {
        this.approvalRate = approvalRate;
        this.rejectionRate = rejectionRate;
        this.candidatesPerJobAd = candidatesPerJobAd;
        this.scoreDistribution = scoreDistribution;
        this.stepDifficulty = stepDifficulty;
        this.occupationDifficulty = occupationDifficulty;
    }

    public double getApprovalRate() { return approvalRate; }
    public void setApprovalRate(double v) { this.approvalRate = v; }

    public double getRejectionRate() { return rejectionRate; }
    public void setRejectionRate(double v) { this.rejectionRate = v; }

    public double getCandidatesPerJobAd() { return candidatesPerJobAd; }
    public void setCandidatesPerJobAd(double v) { this.candidatesPerJobAd = v; }

    public List<BucketDto> getScoreDistribution() { return scoreDistribution; }
    public void setScoreDistribution(List<BucketDto> v) { this.scoreDistribution = v; }

    public List<StepAvgDto> getStepDifficulty() { return stepDifficulty; }
    public void setStepDifficulty(List<StepAvgDto> v) { this.stepDifficulty = v; }

    public List<OccupationAvgDto> getOccupationDifficulty() { return occupationDifficulty; }
    public void setOccupationDifficulty(List<OccupationAvgDto> v) { this.occupationDifficulty = v; }
}