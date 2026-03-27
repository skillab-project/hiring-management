package com.example.hiringProcess.Occupation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.example.hiringProcess.JobAd.JobAd;

@Entity
@Table(name = "occupations") // Matches Employee
public class Occupation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "occupations_seq")
    @SequenceGenerator(name = "occupations_seq", sequenceName = "occupations_seq", allocationSize = 1)
    private Integer id;

    private String title;

    private String escoId;

    @Column(length = 1000)
    private String description; // Added to match Employee/DB

    // Relationship with JobAd (Hiring specific)
    @OneToMany(mappedBy = "occupation", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<JobAd> jobAds = new ArrayList<>();

    public Occupation() {}

    public Occupation(String title, String escoId) {
        this.title = title;
        this.escoId = escoId;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEscoId() { return escoId; }
    public void setEscoId(String escoId) { this.escoId = escoId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<JobAd> getJobAds() { return jobAds; }
    public void setJobAds(List<JobAd> jobAds) { this.jobAds = jobAds; }
}