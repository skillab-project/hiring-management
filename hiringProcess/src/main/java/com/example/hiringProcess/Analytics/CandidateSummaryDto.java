package com.example.hiringProcess.Analytics;

public class CandidateSummaryDto {
    private long approvedCount;
    private long pendingCount;

    public CandidateSummaryDto() {}
    public CandidateSummaryDto(long approvedCount, long pendingCount) {
        this.approvedCount = approvedCount;
        this.pendingCount = pendingCount;
    }
    public long getApprovedCount() { return approvedCount; }
    public void setApprovedCount(long approvedCount) { this.approvedCount = approvedCount; }
    public long getPendingCount() { return pendingCount; }
    public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }
}
