package com.example.hiringProcess.Candidate;

// Candidates tab
//get
public class CandidateDTO {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String status;
    private final String cvPath;

    public CandidateDTO(int id, String firstName, String lastName,
                        String email, String status, String cvPath) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.cvPath = cvPath;
    }

    // Getters (JavaBeans conventions)
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getCvPath() { return cvPath; }   // <-- ΔΙΟΡΘΩΜΕΝΟ
}
