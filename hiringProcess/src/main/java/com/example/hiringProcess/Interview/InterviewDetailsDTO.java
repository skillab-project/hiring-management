package com.example.hiringProcess.Interview;

import java.util.List;

public class InterviewDetailsDTO {
    private int id;
    private String title;
    private String description;
    private List<StepDTO> steps; // Μικρό DTO μόνο για τα step

    public InterviewDetailsDTO(int id, String title, String description, List<StepDTO> steps) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.steps = steps;
    }

    public static class StepDTO {
        private int id;
        private String title;

        public StepDTO(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<StepDTO> getSteps() { return steps; }
}
