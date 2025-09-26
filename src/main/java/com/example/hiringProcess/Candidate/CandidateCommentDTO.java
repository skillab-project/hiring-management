package com.example.hiringProcess.Candidate;

public class CandidateCommentDTO {
    private int candidateId;   // σε ποιον υποψήφιο μπαίνει το σχόλιο
    private String comments;   // το σχόλιο

    public CandidateCommentDTO() {}

    public CandidateCommentDTO(int candidateId, String comments) {
        this.candidateId = candidateId;
        this.comments = comments;
    }

    // Getters & Setters

    public int getCandidateId() {
        return candidateId;
    }
    public String getComments() {
        return comments;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
}
