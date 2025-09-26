package com.example.hiringProcess.Question;

import java.util.List;

public class QuestionUpdateRequest {
    private String description;
    private List<String> skillNames; // π.χ. ["Java","OOP"]

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getSkillNames() { return skillNames; }
    public void setSkillNames(List<String> skillNames) { this.skillNames = skillNames; }
}
