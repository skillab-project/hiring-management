package com.example.hiringProcess.Analytics;

import java.util.List;

public class OrganizationStatsDto {
    private double approvalRate;
    private double rejectionRate;
    private double hireRate;
    private long hireCount;
    public long totalCandidates;
    private List<SkillAvgDto> top5Skills;
    private List<SkillAvgDto> weakest5Skills;
    private List<ScoreBucketDto> scoreDistribution;
    private double avgCandidatesPerJobAd;

    public OrganizationStatsDto() {}

    public OrganizationStatsDto(
            double approvalRate,
            double rejectionRate,
            double hireRate,
            long hireCount,
            long totalCandidates,
            List<SkillAvgDto> top5Skills,
            List<SkillAvgDto> weakest5Skills,
            List<ScoreBucketDto> scoreDistribution,
            double avgCandidatesPerJobAd
    ) {
        this.approvalRate = approvalRate;
        this.rejectionRate = rejectionRate;
        this.hireRate = hireRate;
        this.hireCount = hireCount;
        this.totalCandidates = totalCandidates;
        this.top5Skills = top5Skills;
        this.weakest5Skills = weakest5Skills;
        this.scoreDistribution = scoreDistribution;
        this.avgCandidatesPerJobAd = avgCandidatesPerJobAd;
    }

    public double getApprovalRate() { return approvalRate; }
    public void setApprovalRate(double v) { this.approvalRate = v; }

    public double getRejectionRate() { return rejectionRate; }
    public void setRejectionRate(double v) { this.rejectionRate = v; }

    public double getHireRate() { return hireRate; }
    public void setHireRate(double v) { this.hireRate = v; }

    public long getHireCount() { return hireCount; }
    public void setHireCount(long v) { this.hireCount = v; }

    public List<SkillAvgDto> getTop5Skills() { return top5Skills; }
    public void setTop5Skills(List<SkillAvgDto> v) { this.top5Skills = v; }

    public List<SkillAvgDto> getWeakest5Skills() { return weakest5Skills; }
    public void setWeakest5Skills(List<SkillAvgDto> v) { this.weakest5Skills = v; }

    public long getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(long v) { this.totalCandidates = v; }

    public List<ScoreBucketDto> getScoreDistribution() { return scoreDistribution; }
    public void setScoreDistribution(List<ScoreBucketDto> scoreDistribution) { this.scoreDistribution = scoreDistribution; }

    public double getAvgCandidatesPerJobAd() { return avgCandidatesPerJobAd; }
    public void setAvgCandidatesPerJobAd(double v) { this.avgCandidatesPerJobAd = v; }
}