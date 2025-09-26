package com.example.hiringProcess.Step;

import lombok.AllArgsConstructor;
import lombok.Data;

public class StepQuestionsDTO {
    private int id;
    private String title;

    public StepQuestionsDTO() {}

    public StepQuestionsDTO(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }

    public String getTitle() {
        return title;
    }
}
