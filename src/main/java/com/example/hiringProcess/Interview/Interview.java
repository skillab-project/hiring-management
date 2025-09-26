//package com.example.hiringProcess.Interview;
//
//import com.example.hiringProcess.InterviewReport.InterviewReport;
//import com.example.hiringProcess.JobAd.JobAd;
//import com.example.hiringProcess.Step.Step;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "interview")
//public class Interview {
//
//    @Id
//    @SequenceGenerator(name = "interview_sequence", sequenceName = "interview_sequence", allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interview_sequence")
//    private int id;
//
//    private String title;
//    private String description;
//
//    /** inverse της 1–1, ΧΩΡΙΣ JoinColumn */
//    @OneToOne(mappedBy = "interview")
//    @JsonIgnore
//    private JobAd jobAd;
//
//    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OrderBy("position ASC")
//    private List<Step> steps = new ArrayList<>();
//
//    @OneToOne(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    private InterviewReport interviewReport;
//
//    public Interview() {}
//    public Interview(String title, String description, List<Step> steps) {
//        this.title = title;
//        this.description = description;
//        setSteps(steps);
//    }
//
//    public void addStep(Step step) {
//        if (step == null) return;
//        steps.add(step);
//        step.setInterview(this);
//    }
//
//    public void setSteps(List<Step> steps) {
//        this.steps.clear();
//        if (steps != null) steps.forEach(this::addStep);
//    }
//
//    // === getters/setters ===
//    public int getId() { return id; }
//    public void setId(int id) { this.id = id; }
//
//    public String getTitle() { return title; }
//    public void setTitle(String title) { this.title = title; }
//
//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }
//
//    public JobAd getJobAd() { return jobAd; }
//    public void setJobAd(JobAd jobAd) { this.jobAd = jobAd; }
//
//    public List<Step> getSteps() { return steps; }
//
//    public InterviewReport getInterviewReport() { return interviewReport; }
//    public void setInterviewReport(InterviewReport interviewReport) { this.interviewReport = interviewReport; }
//}

// src/main/java/com/example/hiringProcess/Interview/Interview.java
package com.example.hiringProcess.Interview;

import com.example.hiringProcess.InterviewReport.InterviewReport;
import com.example.hiringProcess.JobAd.JobAd;
import com.example.hiringProcess.Step.Step;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interview")
public class Interview {

    @Id
    @SequenceGenerator(name = "interview_sequence", sequenceName = "interview_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interview_sequence")
    private int id;

    private String title;
    private String description;

    /** inverse της 1–1, ΧΩΡΙΣ JoinColumn */
    @OneToOne(mappedBy = "interview")
    @JsonIgnore
    private JobAd jobAd;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Step> steps = new ArrayList<>();

    /** inverse της ManyToOne από InterviewReport προς Interview */
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<InterviewReport> interviewReports = new ArrayList<>();

    public Interview() {}
    public Interview(String title, String description, List<Step> steps) {
        this.title = title;
        this.description = description;
        setSteps(steps);
    }

    public void addStep(Step step) {
        if (step == null) return;
        steps.add(step);
        step.setInterview(this);
    }

    public void setSteps(List<Step> steps) {
        this.steps.clear();
        if (steps != null) steps.forEach(this::addStep);
    }

    // === getters/setters ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public JobAd getJobAd() { return jobAd; }
    public void setJobAd(JobAd jobAd) { this.jobAd = jobAd; }

    public List<Step> getSteps() { return steps; }

    public List<InterviewReport> getInterviewReports() { return interviewReports; }
    public void setInterviewReports(List<InterviewReport> interviewReports) {
        this.interviewReports.clear();
        if (interviewReports != null) {
            for (InterviewReport r : interviewReports) {
                if (r != null) {
                    this.interviewReports.add(r);
                    r.setInterview(this);
                }
            }
        }
    }
}
