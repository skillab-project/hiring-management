package com.example.hiringProcess.JobAd;

import com.example.hiringProcess.Candidate.Candidate;
import com.example.hiringProcess.Department.Department;
import com.example.hiringProcess.Interview.Interview;
import com.example.hiringProcess.Occupation.Occupation;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "job_ad")
public class JobAd {

    @Id
    @SequenceGenerator(name = "jobAd_sequence", sequenceName = "jobAd_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jobAd_sequence")
    private int id;

    private String title;
    private String description;
    private LocalDate publishDate;
    private String status;

    /** 1:N προς Candidate – διαγράφονται με το JobAd */
    @OneToMany(mappedBy = "jobAd", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Candidate> candidates = new ArrayList<>();

    /** 1:1 προς Interview – το JobAd κρατάει το FK */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "interview_id", referencedColumnName = "id", nullable = true, unique = true)
    private Interview interview;

    /** N:1 προς Occupation – ΔΕΝ την διαγράφουμε */
    @ManyToOne
    @JoinColumn(name = "occupation_id", referencedColumnName = "id")
    private Occupation occupation;

    /** M:N προς Department – καθαρίζουμε πριν το delete */
    @ManyToMany
    @JoinTable(
            name = "jobad_department",
            joinColumns = @JoinColumn(name = "jobad_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> departments = new HashSet<>();

    public JobAd() {}

    public JobAd(String title, String description, LocalDate publishDate, String status, Interview interview) {
        this.title = title;
        this.description = description;
        this.publishDate = publishDate;
        this.status = status;
        setInterview(interview); // κρατάει και τη διπλή αναφορά
    }

    /** Καλό helper για να κρατάμε bidirectional συνέπεια */
    public void setInterview(Interview interview) {
        this.interview = interview;
        if (interview != null) {
            interview.setJobAd(this);
        }
    }

    @PreRemove
    private void preRemove() {
        // καθάρισε μόνο τα M2M join-rows για να μην μείνει τίποτα πίσω
        if (departments != null) departments.clear();
        // candidates + interview φεύγουν με orphanRemoval
    }

    // === getters/setters ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDate publishDate) { this.publishDate = publishDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Candidate> getCandidates() { return candidates; }
    public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }

    public Interview getInterview() { return interview; }

    public Occupation getOccupation() { return occupation; }
    public void setOccupation(Occupation occupation) { this.occupation = occupation; }

    public Set<Department> getDepartments() { return departments; }
    public void setDepartments(Set<Department> departments) { this.departments = departments; }
}
