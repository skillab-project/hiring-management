package com.example.hiringProcess.JobAd;

import java.util.List;

public class JobAdUpdateDTO {
    private String description;
    private List<String> skills;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}
