package com.example.hiringProcess.Candidate;

public class CandidateDTO {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String status;
    private final String cvPath;
    private final String cvOriginalName;
    private final Integer interviewReportId;

    public CandidateDTO(int id, String firstName, String lastName,
                        String email, String status, String cvPath, String cvOriginalName,
                        Integer interviewReportId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.cvPath = cvPath;
        this.cvOriginalName = cvOriginalName;
        this.interviewReportId = interviewReportId;
    }

    // Getters

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getCvPath() { return cvPath; }
    public Integer getInterviewReportId() { return interviewReportId; }
    public String getCvOriginalName() { return cvOriginalName; }

}
