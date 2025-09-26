package com.example.hiringProcess.Question;

import com.example.hiringProcess.QuestionScore.QuestionScore;
import com.example.hiringProcess.Skill.Skill;
import com.example.hiringProcess.SkillScore.SkillScore;
import com.example.hiringProcess.Step.Step;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @SequenceGenerator(name = "questions_sequence", sequenceName = "questions_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questions_sequence")
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Step step;

    // Θέση μέσα στο step (0-based)
    @Column(name = "position")
    private Integer position;

    // === Many-to-Many με Skill (owning side) ===
    @ManyToMany
    @JoinTable(name = "question_skill",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<Skill> skills = new HashSet<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<QuestionScore> questionScore = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SkillScore> skillScores = new ArrayList<>();




    public Question() {}
    public Question(String title){ this.title = title; }

    // Helpers
    public void addSkill(Skill s){ if (s!=null) skills.add(s); }
    public void removeSkill(Skill s){ if (s!=null) skills.remove(s); }

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Step getStep() { return step; }
    public void setStep(Step step) { this.step = step; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public Set<Skill> getSkills() { return skills; }
    public void setSkills(Set<Skill> skills) { this.skills = skills; }

    public List<QuestionScore> getQuestionScore() { return questionScore; }
    public void setQuestionScore(List<QuestionScore> questionScore) { this.questionScore = questionScore; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question q = (Question) o;
        return Objects.equals(id, q.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
