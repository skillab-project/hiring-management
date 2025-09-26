package com.example.hiringProcess.Skill;

import com.example.hiringProcess.JobAd.JobAd;
import com.example.hiringProcess.Question.Question;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Skill {

    @Id
    @SequenceGenerator(name = "skill_sequence", sequenceName = "skill_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skill_sequence")
    private int id;

    @Column(unique = true)
    private String title;

    private double score;
    private String escoId;

    // === Many-to-Many inverse προς Question ===
    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private Set<Question> questions = new HashSet<>();


    public Skill() {}

    public Skill(String title, double score) {
        this.title = title;
        this.score = score;
    }

    // Getters/Setters

    public int getId() {
        return id;
    }
    public void setId(int id) { // προαιρετικό – συνήθως δεν το χρησιμοποιούμε γιατί το id παράγεται από τη ΒΔ
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }

    public String getEscoId() {
        return escoId;
    }
    public void setEscoId(String escoId) {
        this.escoId = escoId;
    }

    public Set<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

}
