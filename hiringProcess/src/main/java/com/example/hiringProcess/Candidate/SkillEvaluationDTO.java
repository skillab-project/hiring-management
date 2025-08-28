package com.example.hiringProcess.Candidate;

//Candidates/Result tab
public class SkillEvaluationDTO {
    private int candidateId;   // σε ποιον υποψήφιο ανήκει η αξιολόγηση
    private int skillId;       // ποιο skill βαθμολογείται
    private int rating;        // βαθμολογία (π.χ. 0–100)
    private String comments;   // σχόλια αξιολογητή

    public SkillEvaluationDTO() {
    }

    public SkillEvaluationDTO(int candidateId, int skillId, int rating, String comments) {
        this.candidateId = candidateId;
        this.skillId = skillId;
        this.rating = rating;
        this.comments = comments;
    }

    // Getters
    public int getCandidateId() {
        return candidateId;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    // Setters
    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
