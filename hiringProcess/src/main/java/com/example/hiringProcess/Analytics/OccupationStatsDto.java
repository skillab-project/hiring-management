package com.example.hiringProcess.Analytics;

import java.util.List;

public class OccupationStatsDto {
    private double approvalRate;
    private double rejectionRate;
    private double candidatesPerJobAd;
    private List<ScoreBucketDto> scoreDistribution;

    public OccupationStatsDto() {}

    public OccupationStatsDto(double approvalRate, double rejectionRate,
                              double candidatesPerJobAd, List<ScoreBucketDto> scoreDistribution) {
        this.approvalRate = approvalRate;
        this.rejectionRate = rejectionRate;
        this.candidatesPerJobAd = candidatesPerJobAd;
        this.scoreDistribution = scoreDistribution;
    }

    public double getApprovalRate() { return approvalRate; }
    public void setApprovalRate(double approvalRate) { this.approvalRate = approvalRate; }

    public double getRejectionRate() { return rejectionRate; }
    public void setRejectionRate(double rejectionRate) { this.rejectionRate = rejectionRate; }

    public double getCandidatesPerJobAd() { return candidatesPerJobAd; }
    public void setCandidatesPerJobAd(double candidatesPerJobAd) { this.candidatesPerJobAd = candidatesPerJobAd; }

    public List<ScoreBucketDto> getScoreDistribution() { return scoreDistribution; }
    public void setScoreDistribution(List<ScoreBucketDto> scoreDistribution) { this.scoreDistribution = scoreDistribution; }
}
