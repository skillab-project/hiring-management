package com.example.hiringProcess.Analytics;

import com.example.hiringProcess.Organisation.OrganisationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService service;
    private final OrganisationService organizationService;

    public AnalyticsController(AnalyticsService service, OrganisationService organizationService) {
        this.organizationService = organizationService;
        this.service = service;
    }

    @GetMapping("/organization") //TODO OK
    public OrganizationStatsDto organization(@RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getOrganizationStats(orgId);
    }

    @GetMapping("/department/{deptId}") //TODO OK
    public DepartmentStatsDto department(@PathVariable int deptId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getDepartmentStats(deptId, orgId);
    }

    @GetMapping("/occupation/{deptId}/{occId}") //TODO OK
    public OccupationStatsDto occupation(@PathVariable int deptId, @PathVariable int occId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getOccupationStats(deptId, occId, orgId);
    }

    // === JOB AD SCOPE ===
    @GetMapping("/jobad/{jobAdId}") //TODO OK
    public JobAdStatsDto jobAd(@PathVariable int jobAdId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getJobAdStats(jobAdId, orgId);
    }

    @GetMapping("/candidate/{candidateId}") //TODO check to remove
    public CandidateStatsDto candidateStats(@PathVariable int candidateId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getCandidateStats(candidateId, orgId);
    }

    // Λίστα υποψηφίων ενός Job Ad
    @GetMapping("/jobad/{jobAdId}/candidates") //TODO OK
    public List<CandidateLiteDto> jobAdCandidates(@PathVariable int jobAdId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getJobAdCandidates(jobAdId, orgId);
    }

    @GetMapping("/candidate/{candidateId}/stats") //TODO OK
    public CandidateStatsDto candidateStats2(@PathVariable int candidateId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getCandidateStats(candidateId, orgId);
    }

    // STEP analytics για συγκεκριμένο jobAd + step
    @GetMapping("/jobad/{jobAdId}/step/{stepId}") //TODO OK
    public StepStatsDto stepStats(@PathVariable int jobAdId, @PathVariable int stepId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getStepStats(jobAdId, stepId, orgId);
    }

    // Λίστα steps για συγκεκριμένο job ad
    @GetMapping("/jobad/{jobAdId}/steps") //TODO OK
    public java.util.List<StepLiteDto> jobAdSteps(@PathVariable int jobAdId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getStepsForJobAd(jobAdId, orgId);
    }

    // Question analytics για συγκεκριμένη ερώτηση μέσα σε συγκεκριμένο job ad
    @GetMapping("/jobad/{jobAdId}/question/{questionId}") //TODO OK
    public QuestionStatsDto questionStats(@PathVariable int jobAdId, @PathVariable int questionId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getQuestionStats(jobAdId, questionId, orgId);
    }

    // Λίστα ερωτήσεων για συγκεκριμένο job ad & step
    @GetMapping("/jobad/{jobAdId}/step/{stepId}/questions") //TODO OK
    public java.util.List<QuestionLiteDto> questionsForStep(
            @PathVariable int jobAdId, @PathVariable int stepId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getQuestionsForJobAdStep(jobAdId, stepId, orgId);
    }

    // Λίστα δεξιοτήτων μιας ερώτησης
    @GetMapping("/question/{questionId}/skills")  //TODO OK //TODO add OrgId check in order to see if question belong to Organization
    public java.util.List<SkillLiteDto> questionSkills(@PathVariable int questionId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getSkillsForQuestion(questionId);
    }

    // Στατιστικά δεξιότητας (global, σε όλο το dataset)
    @GetMapping("/skill/{skillId}") //TODO OK
    public SkillStatsDto skillStats(@PathVariable int skillId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getSkillStats(skillId, orgId);
    }

    // AnalyticsController.java
    @GetMapping("/jobad/{jobAdId}/question/{questionId}/skill/{skillId}") //TODO OK
    public SkillStatsDto skillStatsForJobAdQuestion(
            @PathVariable int jobAdId,
            @PathVariable int questionId,
            @PathVariable int skillId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return service.getSkillStatsForJobAdQuestion(jobAdId, questionId, skillId, orgId);
    }


}
