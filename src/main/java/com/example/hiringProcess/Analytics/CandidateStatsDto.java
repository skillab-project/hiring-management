package com.example.hiringProcess.Analytics;

import java.util.List;

public class CandidateStatsDto {
    private double overallScore;

    private List<StepAvgDto>      stepScores;      // per step
    private List<QuestionScoreDto> questionScores; // per question
    private List<SkillAvgDto>     skillScores;     // per skill
    private List<SkillAvgDto> strengthProfile; // top 3 skills
    private List<SkillAvgDto> weaknessProfile; // bottom 3 skills

    public CandidateStatsDto() {}

    public CandidateStatsDto(double overallScore,
                             List<StepAvgDto> stepScores,
                             List<QuestionScoreDto> questionScores,
                             List<SkillAvgDto> skillScores,
                             List<SkillAvgDto> strengthProfile,
                             List<SkillAvgDto> weaknessProfile) {
        this.overallScore = overallScore;
        this.stepScores = stepScores;
        this.questionScores = questionScores;
        this.skillScores = skillScores;
        this.strengthProfile = strengthProfile;
        this.weaknessProfile = weaknessProfile;
    }

    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }

    public List<StepAvgDto> getStepScores() { return stepScores; }
    public void setStepScores(List<StepAvgDto> stepScores) { this.stepScores = stepScores; }

    public List<QuestionScoreDto> getQuestionScores() { return questionScores; }
    public void setQuestionScores(List<QuestionScoreDto> questionScores) { this.questionScores = questionScores; }

    public List<SkillAvgDto> getSkillScores() { return skillScores; }
    public void setSkillScores(List<SkillAvgDto> skillScores) { this.skillScores = skillScores; }

    public List<SkillAvgDto> getStrengthProfile() { return strengthProfile; }
    public void setStrengthProfile(List<SkillAvgDto> strengthProfile) { this.strengthProfile = strengthProfile; }

    public List<SkillAvgDto> getWeaknessProfile() { return weaknessProfile; }
    public void setWeaknessProfile(List<SkillAvgDto> weaknessProfile) { this.weaknessProfile = weaknessProfile; }
}
