package com.example.hiringProcess.Step;

import java.util.List;

public class StepReorderRequest {
    private List<Integer> stepIds; // νέα σειρά ids (index = position)

    public List<Integer> getStepIds() { return stepIds; }
    public void setStepIds(List<Integer> stepIds) { this.stepIds = stepIds; }
}
