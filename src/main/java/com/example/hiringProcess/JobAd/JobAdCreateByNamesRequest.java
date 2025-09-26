package com.example.hiringProcess.JobAd;

import java.time.LocalDate;

public class JobAdCreateByNamesRequest {
    private String title;
    private String description;
    private String status;
    private LocalDate publishDate;
    private String departmentName;
    private String occupationTitle;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDate publishDate) { this.publishDate = publishDate; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getOccupationTitle() { return occupationTitle; }
    public void setOccupationTitle(String occupationTitle) { this.occupationTitle = occupationTitle; }
}
