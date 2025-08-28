package com.example.hiringProcess.Step;

import lombok.Data;

@Data
public class StepCreateRequest {
    private Integer interviewId;
    private String title;
    private String description;
}
