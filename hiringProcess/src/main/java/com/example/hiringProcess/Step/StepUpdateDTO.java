package com.example.hiringProcess.Step;

import lombok.Data;

@Data
public class StepUpdateDTO {
    private String title;        // optional
    private String description;  // optional
    private Integer interviewId; // optional (αν θες να επιτρέπεις μεταφορά σε άλλη συνέντευξη)
    private Integer score;       // optional (αν πραγματικά χρειάζεται να αλλάζει)
}
