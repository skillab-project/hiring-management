package com.example.hiringProcess.QuestionScore;

import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.StepScore.StepScore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table
public class QuestionScore {
    @Id
    @SequenceGenerator(
            name = "candidate_sequence",
            sequenceName = "candidate_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "candidate_sequence"
    )
    private int id;
    private double score;

    // Σχέση QuestionScore με Question
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    // Σχέση QuestionScore με StepResults
    @ManyToOne
    @JoinColumn(name = "stepScore_id", referencedColumnName = "id")
    @JsonIgnore
    private StepScore stepScore;

    public QuestionScore() { }

    public QuestionScore(double score){
        this.score=score;
    }

    // Getters & Setters

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setScore(double score) {
        this.score = score;
    }
    public double getScore() {
        return score;
    }

    public Question getQuestion() {
        return question;
    }
    public void setQuestion(Question question) {
        this.question = question;
    }

    public StepScore getStepScore() {
        return stepScore;
    }
    public void setStepScore(StepScore stepScore) {
        this.stepScore = stepScore;
    }
}
