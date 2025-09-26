package com.example.hiringProcess.Analytics;

public class CandidateLiteDto {
    private Integer id;
    private String fullName;
    private String status;

    public CandidateLiteDto() {}

    public CandidateLiteDto(Integer id, String fullName, String status) {
        this.id = id;
        this.fullName = fullName;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
