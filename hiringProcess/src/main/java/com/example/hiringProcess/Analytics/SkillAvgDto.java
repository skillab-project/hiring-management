package com.example.hiringProcess.Analytics;

public class SkillAvgDto {
    private String skill;
    private Double averageScore;

    public SkillAvgDto() {}
    public SkillAvgDto(String skill, Double averageScore) {
        this.skill = skill;
        this.averageScore = averageScore;
    }
    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }
    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
}