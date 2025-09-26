package com.example.hiringProcess.SkillScore;

import com.example.hiringProcess.Candidate.Candidate;
import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.Skill.Skill;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(
        name = "skill_score",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_candidate_question_skill",
                columnNames = {"candidate_id","question_id","skill_id"}
        ),
        indexes = {
                @Index(name = "idx_skillscore_candidate", columnList = "candidate_id"),
                @Index(name = "idx_skillscore_question", columnList = "question_id"),
                @Index(name = "idx_skillscore_skill",    columnList = "skill_id")
        }
)
public class SkillScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Σχέση SkillScore με Candidate
    @ManyToOne(optional = false) @JoinColumn(name = "candidate_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Candidate candidate;

    // Σχέση SkillScore με Question
    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Question question;

    // Σχέση SkillScore με Skill
    @ManyToOne(optional = false) @JoinColumn(name = "skill_id")
    private Skill skill;

    // Κλίμακα 0..100
    @Column(nullable = true)
    private Integer score;

    // Σχόλιο για αυτό το skill πάνω σε αυτή την ερώτηση για τον συγκεκριμένο υποψήφιο
    @Column(length = 2000)
    private String comment;

    // Getters & Setters

    public Long getId() { return id; }

    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
