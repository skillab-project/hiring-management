package com.example.hiringProcess.Candidate;

public class CandidateAndJobAdStatusDTO {
    private int candidateId;
    private String candidateStatus;
    private int jobAdId;
    private String jobAdStatus;
    private long hiredCount;

    public CandidateAndJobAdStatusDTO() {}

    public CandidateAndJobAdStatusDTO(int candidateId, String candidateStatus,
                                      int jobAdId, String jobAdStatus,
                                      long hiredCount) {
        this.candidateId = candidateId;
        this.candidateStatus = candidateStatus;
        this.jobAdId = jobAdId;
        this.jobAdStatus = jobAdStatus;
        this.hiredCount = hiredCount;
    }

    // Getters & Setters

    public int getCandidateId() { return candidateId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }

    public String getCandidateStatus() { return candidateStatus; }
    public void setCandidateStatus(String candidateStatus) { this.candidateStatus = candidateStatus; }

    public int getJobAdId() { return jobAdId; }
    public void setJobAdId(int jobAdId) { this.jobAdId = jobAdId; }

    public String getJobAdStatus() { return jobAdStatus; }
    public void setJobAdStatus(String jobAdStatus) { this.jobAdStatus = jobAdStatus; }

    public long getHiredCount() { return hiredCount; }
    public void setHiredCount(long hiredCount) { this.hiredCount = hiredCount; }
}
