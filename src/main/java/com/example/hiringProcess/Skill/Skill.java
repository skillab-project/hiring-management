package com.example.hiringProcess.Skill;

import com.example.hiringProcess.Question.Question;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skills_seq")
    @SequenceGenerator(name = "skills_seq", sequenceName = "skills_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "name", unique = true) // Maps Hiring 'title' to DB 'name'
    private String title;

    private double score;

    private String escoId;

    @Column(length = 1000)
    private String description; // Added to match Employee/DB

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private Set<Question> questions = new HashSet<>();

    public Skill() {}

    public Skill(String title, double score) {
        this.title = title;
        this.score = score;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getEscoId() { return escoId; }
    public void setEscoId(String escoId) { this.escoId = escoId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<Question> getQuestions() { return questions; }
    public void setQuestions(Set<Question> questions) { this.questions = questions; }
}