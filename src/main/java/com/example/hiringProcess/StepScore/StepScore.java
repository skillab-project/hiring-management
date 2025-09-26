package com.example.hiringProcess.StepScore;

import com.example.hiringProcess.InterviewReport.InterviewReport;
import com.example.hiringProcess.QuestionScore.QuestionScore;
import com.example.hiringProcess.Step.Step;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class StepScore {
    @Id
    @SequenceGenerator(
            name = "stepScore_sequence",
            sequenceName = "stepScore_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "stepScore_sequence"
    )
    private int id;

    // Σχέση StepResults με Step
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "step_id", referencedColumnName = "id")
    @JsonIgnore
    Step step;

    // Σχέση StepResults με InterviewReport
    @ManyToOne
    @JoinColumn(name = "interviewReport_id", referencedColumnName = "id")
    @JsonIgnore
    private InterviewReport interviewReport;

    // Σχέση StepResults με QuestionScore
    @OneToMany(mappedBy = "stepScore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionScore> questionScores = new ArrayList<>();

    public StepScore() { }

    public StepScore(Step step, InterviewReport interviewReport) {
        this.step = step;
        this.interviewReport = interviewReport;
    }

    // Getters & Setters

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Step getStep() {
        return step;
    }
    public void setStep(Step step) {
        this.step = step;
    }

    public InterviewReport getInterviewReport() {
        return interviewReport;
    }
    public void setInterviewReport(InterviewReport interviewReport) {
        this.interviewReport = interviewReport;
    }

    public List<QuestionScore> getQuestionScores() {
        return questionScores;
    }
    public void setQuestionScores(List<QuestionScore> questionScores) {
        this.questionScores = questionScores;
    }
}
