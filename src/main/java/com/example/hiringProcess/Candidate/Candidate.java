package com.example.hiringProcess.Candidate;

import com.example.hiringProcess.InterviewReport.InterviewReport;
import com.example.hiringProcess.JobAd.JobAd;
import com.example.hiringProcess.SkillScore.SkillScore;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Candidate {

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

    private String firstName;
    private String lastName;
    private String email;
    private String cvPath;
    private String cvOriginalName;
    private String status;

    @Column(name = "comments", columnDefinition = "text")
    private String comments;

    // Σχέση Candidate με JobAd
    @ManyToOne
    @JoinColumn(name = "job_ad_id", referencedColumnName = "id")
    @JsonIgnore
    private JobAd jobAd;

    // Σχέση Candidate με InterviewReport
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "interview_report_id", referencedColumnName = "id")
    @JsonIgnore
    private InterviewReport interviewReport;

    // Σχέση Candidate με SkillScore
    @OneToMany(mappedBy = "candidate")
    @JsonIgnore
    private List<SkillScore> skillScores = new ArrayList<>();

    public Candidate() {}

    public Candidate(String firstName, String lastName, String email,
                     String cvPath, String cvOriginalName, String status, String comments) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cvPath = cvPath;
        this.cvOriginalName = cvOriginalName;
        this.status = status;
        this.comments = comments;
    }

    // Getters & Setters

    public int getId() {
        return id;
    }
    public void setId(int id) {this.id = id;}

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCvPath() {
        return cvPath;
    }
    public void setCvPath(String cvPath) {this.cvPath = cvPath;}

    public String getCvOriginalName() { return cvOriginalName; }
    public void setCvOriginalName(String cvOriginalName) { this.cvOriginalName = cvOriginalName; }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }

    public InterviewReport getInterviewReport() {
        return interviewReport;
    }
    public void setInterviewReport(InterviewReport interviewReport) {
        this.interviewReport = interviewReport;
    }

    public JobAd getJobAd() {
        return jobAd;
    }
    public void setJobAd(JobAd jobAd) {
        this.jobAd = jobAd;
    }

    public List<SkillScore> getSkillScores() {return skillScores;}
    public void setSkillScores(List<SkillScore> skillScores) {this.skillScores = skillScores;}
}
