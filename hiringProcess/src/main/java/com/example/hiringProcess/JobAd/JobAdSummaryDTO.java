package com.example.hiringProcess.JobAd;

public class JobAdSummaryDTO {
    private int id;
    private String jobTitle;
    private String occupationName;
    private String status;
    private String departmentName; // ✅ Προστέθηκε

    public JobAdSummaryDTO() {}

    public JobAdSummaryDTO(int id, String jobTitle, String occupationName, String status, String departmentName) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.occupationName = occupationName;
        this.status = status;
        this.departmentName = departmentName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getOccupationName() {
        return occupationName;
    }

    public void setOccupationName(String occupationName) {
        this.occupationName = occupationName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
