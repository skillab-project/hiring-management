package com.example.hiringProcess.QuestionScore;

public class QuestionMetricsItemDTO {
    private Integer questionId;
    private int totalSkills;
    private int ratedSkills;
    private Integer averageScore;

    public QuestionMetricsItemDTO() {}

    public QuestionMetricsItemDTO(Integer questionId, int totalSkills, int ratedSkills, Integer averageScore) {
        this.questionId = questionId;
        this.totalSkills = totalSkills;
        this.ratedSkills = ratedSkills;
        this.averageScore = averageScore;
    }

    // Getters & Setters

    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }

    public int getTotalSkills() { return totalSkills; }
    public void setTotalSkills(int totalSkills) { this.totalSkills = totalSkills; }

    public int getRatedSkills() { return ratedSkills; }
    public void setRatedSkills(int ratedSkills) { this.ratedSkills = ratedSkills; }

    public Integer getAverageScore() { return averageScore; }
    public void setAverageScore(Integer averageScore) { this.averageScore = averageScore; }
}
