package com.example.hiringProcess.Analytics;

import java.util.List;

public class OrganizationStatsDto {
    private double approvalRate;
    private double rejectionRate;
    private double hireRate;
    private long hireCount;
    private List<SkillAvgDto> top5Skills;
    private List<SkillAvgDto> weakest5Skills;

    public OrganizationStatsDto() {}

    public OrganizationStatsDto(double approvalRate, double rejectionRate, double hireRate, long hireCount,
                                List<SkillAvgDto> top5Skills, List<SkillAvgDto> weakest5Skills) {
        this.approvalRate = approvalRate;
        this.rejectionRate = rejectionRate;
        this.hireRate = hireRate;
        this.hireCount = hireCount;
        this.top5Skills = top5Skills;
        this.weakest5Skills = weakest5Skills;
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
}