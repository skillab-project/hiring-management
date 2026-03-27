package com.example.hiringProcess.Step;

import com.example.hiringProcess.Interview.Interview;
import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.StepScore.StepScore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "step",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_interview_position", columnNames = {"interview_id","position"})
        }
)
public class Step {
    @Id
    @SequenceGenerator(
            name = "step_sequence",
            sequenceName = "step_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "step_sequence"
    )
    private int id;
    private double score;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int position; // ΝΕΟ: σειρά (0,1,2,...)

    // Σχέση Step με Interview
    @ManyToOne()
    @JoinColumn(name = "interview_id", referencedColumnName = "id")
    @JsonIgnore
    private Interview interview;

    // Σχέση Step με Question
    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Question> questions = new ArrayList<>();

    // Σχέση Step με StepResults
    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<StepScore> stepResults = new ArrayList<>();



    public Step() {}

    public Step(String title, String description, double score ){
        this.title = title;
        this.description = description;
        this.score =score;
    }

    @Override
    public String toString() {
        return "Step{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", position=" + position +
                ", interview=" + (interview != null ? interview.getId() : "null") +
                ", questions=" + questionsToString() +
                '}';
    }

    public void addQuestion(Question question) {
        if (questions != null) {
            questions.add(question);
            question.setStep(this);
        }
    }

    private String questionsToString() {
        if (questions == null || questions.isEmpty()) return "[]";
        return questions.stream()
                .map(q -> "{id=" + q.getId() + ", name=" + q.getTitle() + "}")
                .toList()
                .toString();
    }

    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public Interview getInterview() { return interview; }
    public void setInterview(Interview interview) { this.interview = interview; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public List<StepScore> getStepResults() { return stepResults; }
    public void setStepResults(List<StepScore> stepResults) { this.stepResults = stepResults; }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
