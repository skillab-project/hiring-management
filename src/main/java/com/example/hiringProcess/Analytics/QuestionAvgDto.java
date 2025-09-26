package com.example.hiringProcess.Analytics;

public class QuestionAvgDto {
    private String question;
    private Double averageScore;

    public QuestionAvgDto() {}

    public QuestionAvgDto(String question, Double averageScore) {
        this.question = question;
        this.averageScore = averageScore;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
}
