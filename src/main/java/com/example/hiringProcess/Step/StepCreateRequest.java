package com.example.hiringProcess.Step;

public class StepCreateRequest {
    private String title;
    private String description; // optional

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
