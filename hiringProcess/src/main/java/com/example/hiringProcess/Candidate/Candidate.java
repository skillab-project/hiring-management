package com.example.hiringProcess.Candidate;

import com.example.hiringProcess.InterviewReport.InterviewReport;
import com.example.hiringProcess.JobAd.JobAd;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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

    /** Path/URL προς το αποθηκευμένο CV (π.χ. uploads/cv/nick_smith.pdf) */
    private String cvPath;

    private String info;
    private String status;
    private String comments;

    // Σχέση Candidate με JobAd
    @ManyToOne
    @JoinColumn(name = "job_ad_id", referencedColumnName = "id")
    @JsonIgnore
    private JobAd jobAd;

    // Σχέση Candidate με InterviewReport
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "interviewReport_id", referencedColumnName = "id")
    @JsonIgnore
    private InterviewReport interviewReport;

    // Required by JPA
    public Candidate() {}

    // Constructor χωρίς cvPath (για συμβατότητα)
    public Candidate(String firstName, String lastName, String email,
                     String info, String status, String comments) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.info = info;
        this.status = status;
        this.comments = comments;
    }

    // Νέος constructor ΜΕ cvPath
    public Candidate(String firstName, String lastName, String email,
                     String cvPath, String info, String status, String comments) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.cvPath = cvPath;
        this.info = info;
        this.status = status;
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", cvPath='" + cvPath + '\'' +
                ", info='" + info + '\'' +
                ", status='" + status + '\'' +
                ", comments='" + comments + '\'' +
                ", jobAdId=" + (jobAd != null ? jobAd.getId() : "null") +
                '}';
    }

    // Getters & Setters

    public int getId() {
        return id;
    }

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
    public void setCvPath(String cvPath) {
        this.cvPath = cvPath;
    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

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
}
