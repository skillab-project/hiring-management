package com.example.hiringProcess.Question;

public class QuestionCreateRequest {
    private String name;
    private String description; // optional

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
