package com.example.hiringProcess.SkillScore;

import com.example.hiringProcess.Candidate.Candidate;
import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.Skill.Skill;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * ΜΟΝΑΔΙΚΗ πηγή αλήθειας για βαθμολογίες:
 * κάθε εγγραφή = (candidate, question, skill) με score & comment.
 */
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

    @ManyToOne(optional = false) @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne(optional = false) @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(optional = false) @JoinColumn(name = "skill_id")
    private Skill skill;

    /** Κλίμακα 0..100 (άλλαξε τύπο/κλίμακα αν χρειάζεται) */
    @Column(nullable = false)
    private Integer score;

    /** Σχόλιο για αυτό το skill πάνω σε αυτή την ερώτηση για τον συγκεκριμένο υποψήφιο */
    @Column(length = 2000)
    private String comment;

    private Instant ratedAt;
    private String ratedBy;

    /* ===== Getters / Setters ===== */
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

    public Instant getRatedAt() { return ratedAt; }
    public void setRatedAt(Instant ratedAt) { this.ratedAt = ratedAt; }

    public String getRatedBy() { return ratedBy; }
    public void setRatedBy(String ratedBy) { this.ratedBy = ratedBy; }
}
