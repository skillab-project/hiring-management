package com.example.hiringProcess.Analytics;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/organization/{orgId}")
    public OrganizationStatsDto organization(@PathVariable int orgId) {
        return service.getOrganizationStats(orgId);
    }

    @GetMapping("/department/{deptId}")
    public DepartmentStatsDto department(@PathVariable int deptId) {
        return service.getDepartmentStats(deptId);
    }

    @GetMapping("/occupation/{deptId}/{occId}")
    public OccupationStatsDto occupation(@PathVariable int deptId, @PathVariable int occId) {
        return service.getOccupationStats(deptId, occId);
    }

    // === JOB AD SCOPE ===
    @GetMapping("/jobad/{jobAdId}")
    public JobAdStatsDto jobAd(@PathVariable int jobAdId) {
        return service.getJobAdStats(jobAdId);
    }

    @GetMapping("/candidate/{candidateId}")
    public CandidateStatsDto candidateStats(@PathVariable int candidateId) {
        return service.getCandidateStats(candidateId);
    }

    // Λίστα υποψηφίων ενός Job Ad
    @GetMapping("/jobad/{jobAdId}/candidates")
    public List<CandidateLiteDto> jobAdCandidates(@PathVariable int jobAdId) {
        return service.getJobAdCandidates(jobAdId);
    }

    @GetMapping("/candidate/{candidateId}/stats")
    public CandidateStatsDto candidateStats2(@PathVariable int candidateId) {
        return service.getCandidateStats(candidateId);
    }

    // STEP analytics για συγκεκριμένο jobAd + step
    @GetMapping("/jobad/{jobAdId}/step/{stepId}")
    public StepStatsDto stepStats(@PathVariable int jobAdId, @PathVariable int stepId) {
        return service.getStepStats(jobAdId, stepId);
    }

    // Λίστα steps για συγκεκριμένο job ad
    @GetMapping("/jobad/{jobAdId}/steps")
    public java.util.List<StepLiteDto> jobAdSteps(@PathVariable int jobAdId) {
        return service.getStepsForJobAd(jobAdId);
    }

    // Question analytics για συγκεκριμένη ερώτηση μέσα σε συγκεκριμένο job ad
    @GetMapping("/jobad/{jobAdId}/question/{questionId}")
    public QuestionStatsDto questionStats(@PathVariable int jobAdId, @PathVariable int questionId) {
        return service.getQuestionStats(jobAdId, questionId);
    }

    // Λίστα ερωτήσεων για συγκεκριμένο job ad & step
    @GetMapping("/jobad/{jobAdId}/step/{stepId}/questions")
    public java.util.List<QuestionLiteDto> questionsForStep(
            @PathVariable int jobAdId, @PathVariable int stepId) {
        return service.getQuestionsForJobAdStep(jobAdId, stepId);
    }

    // Λίστα δεξιοτήτων μιας ερώτησης
    @GetMapping("/question/{questionId}/skills")
    public java.util.List<SkillLiteDto> questionSkills(@PathVariable int questionId) {
        return service.getSkillsForQuestion(questionId);
    }

    // Στατιστικά δεξιότητας (global, σε όλο το dataset)
    @GetMapping("/skill/{skillId}")
    public SkillStatsDto skillStats(@PathVariable int skillId) {
        return service.getSkillStats(skillId);
    }

    // AnalyticsController.java
    @GetMapping("/jobad/{jobAdId}/question/{questionId}/skill/{skillId}")
    public SkillStatsDto skillStatsForJobAdQuestion(
            @PathVariable int jobAdId,
            @PathVariable int questionId,
            @PathVariable int skillId) {
        return service.getSkillStatsForJobAdQuestion(jobAdId, questionId, skillId);
    }


}
