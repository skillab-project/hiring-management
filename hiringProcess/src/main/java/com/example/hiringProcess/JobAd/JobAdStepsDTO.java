package com.example.hiringProcess.JobAd;

// Candidates tab
public class JobAdStepsDTO {

    private int id;
    private String title;

    public JobAdStepsDTO() {}

    public JobAdStepsDTO(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
