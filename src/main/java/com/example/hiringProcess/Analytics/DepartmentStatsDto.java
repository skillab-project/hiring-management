package com.example.hiringProcess.Analytics;

import java.util.List;

public class DepartmentStatsDto {
    private double approvalRate;
    private double rejectionRate;
    private double candidatesPerJobAd;
    private List<BucketDto> scoreDistribution;
    private List<OccupationAvgDto> occupationDifficulty;
    private double hireRate;
    private long hireCount;
    private long totalCandidates;

    public DepartmentStatsDto() {}

    public DepartmentStatsDto(
            double approvalRate,
            double rejectionRate,
            double candidatesPerJobAd,
            List<BucketDto> scoreDistribution,
            List<OccupationAvgDto> occupationDifficulty,
            double hireRate,
            long hireCount,
            long totalCandidates
    ) {
        this.approvalRate = approvalRate;
        this.rejectionRate = rejectionRate;
        this.candidatesPerJobAd = candidatesPerJobAd;
        this.scoreDistribution = scoreDistribution;
        this.occupationDifficulty = occupationDifficulty;
        this.hireRate = hireRate;
        this.hireCount = hireCount;
        this.totalCandidates = totalCandidates;
    }

    public double getApprovalRate() { return approvalRate; }
    public void setApprovalRate(double approvalRate) { this.approvalRate = approvalRate; }

    public double getRejectionRate() { return rejectionRate; }
    public void setRejectionRate(double rejectionRate) { this.rejectionRate = rejectionRate; }

    public double getCandidatesPerJobAd() { return candidatesPerJobAd; }
    public void setCandidatesPerJobAd(double candidatesPerJobAd) { this.candidatesPerJobAd = candidatesPerJobAd; }

    public List<BucketDto> getScoreDistribution() { return scoreDistribution; }
    public void setScoreDistribution(List<BucketDto> scoreDistribution) { this.scoreDistribution = scoreDistribution; }

    public List<OccupationAvgDto> getOccupationDifficulty() { return occupationDifficulty; }
    public void setOccupationDifficulty(List<OccupationAvgDto> occupationDifficulty) { this.occupationDifficulty = occupationDifficulty; }

    public double getHireRate() { return hireRate; }
    public void setHireRate(double hireRate) { this.hireRate = hireRate; }

    public long getHireCount() { return hireCount; }
    public void setHireCount(long hireCount) { this.hireCount = hireCount; }

    public long getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(long totalCandidates) { this.totalCandidates = totalCandidates; }

    public long getTotal() { return totalCandidates; }
}
