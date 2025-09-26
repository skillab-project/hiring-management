package com.example.hiringProcess.Analytics;

import java.util.List;

public class OccupationStatsDto {
    private double approvalRate;
    private double rejectionRate;
    private double hireRate;
    private long hireCount;
    private double candidatesPerJobAd;
    private List<ScoreBucketDto> scoreDistribution;

    private long totalCandidates;
    private List<JobAdAvgDto> jobAdDifficulty;

    public OccupationStatsDto() {}

    public OccupationStatsDto(
            double approvalRate,
            double rejectionRate,
            double hireRate,
            long hireCount,
            double candidatesPerJobAd,
            List<ScoreBucketDto> scoreDistribution,
            long totalCandidates,
            List<JobAdAvgDto> jobAdDifficulty
    ) {
        this.approvalRate = approvalRate;
        this.rejectionRate = rejectionRate;
        this.hireRate = hireRate;
        this.hireCount = hireCount;
        this.candidatesPerJobAd = candidatesPerJobAd;
        this.scoreDistribution = scoreDistribution;
        this.totalCandidates = totalCandidates;
        this.jobAdDifficulty = jobAdDifficulty;
    }

    public double getApprovalRate() { return approvalRate; }
    public void setApprovalRate(double approvalRate) { this.approvalRate = approvalRate; }

    public double getRejectionRate() { return rejectionRate; }
    public void setRejectionRate(double rejectionRate) { this.rejectionRate = rejectionRate; }

    public double getHireRate() { return hireRate; }
    public void setHireRate(double hireRate) { this.hireRate = hireRate; }

    public long getHireCount() { return hireCount; }
    public void setHireCount(long hireCount) { this.hireCount = hireCount; }

    public double getCandidatesPerJobAd() { return candidatesPerJobAd; }
    public void setCandidatesPerJobAd(double candidatesPerJobAd) { this.candidatesPerJobAd = candidatesPerJobAd; }

    public List<ScoreBucketDto> getScoreDistribution() { return scoreDistribution; }
    public void setScoreDistribution(List<ScoreBucketDto> scoreDistribution) { this.scoreDistribution = scoreDistribution; }

    public long getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(long totalCandidates) { this.totalCandidates = totalCandidates; }

    public List<JobAdAvgDto> getJobAdDifficulty() { return jobAdDifficulty; }
    public void setJobAdDifficulty(List<JobAdAvgDto> jobAdDifficulty) { this.jobAdDifficulty = jobAdDifficulty; }
}
