package com.example.hiringProcess.Analytics;

public class QuestionScoreDto {
    private String question;
    private Double score;

    public QuestionScoreDto() {}

    public QuestionScoreDto(String question, Double score) {
        this.question = question;
        this.score = score;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
}
