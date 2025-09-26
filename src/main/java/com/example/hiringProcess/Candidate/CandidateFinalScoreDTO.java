package com.example.hiringProcess.Candidate;

public class CandidateFinalScoreDTO {
    private final int candidateId;
    private final String firstName;
    private final String lastName;
    private final String status;
    private final Integer avgScore;
    private final long ratedSkills;
    private final long totalSkills;

    public CandidateFinalScoreDTO(
            int candidateId,
            String firstName,
            String lastName,
            String status,
            Double avgScoreRaw,
            Long ratedSkills,
            Long totalSkills
    ) {
        this.candidateId = candidateId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.avgScore = (avgScoreRaw == null ? null : (int)Math.round(avgScoreRaw));
        this.ratedSkills = ratedSkills == null ? 0L : ratedSkills;
        this.totalSkills = totalSkills == null ? 0L : totalSkills;
    }

    // Getters

    public int getCandidateId() { return candidateId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getStatus() { return status; }
    public Integer getAvgScore() { return avgScore; }
    public long getRatedSkills() { return ratedSkills; }
    public long getTotalSkills() { return totalSkills; }
}
