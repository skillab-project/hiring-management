package com.example.hiringProcess.StepScore;

import com.example.hiringProcess.Candidate.CandidateService;
import com.example.hiringProcess.InterviewReport.InterviewReportService;
import com.example.hiringProcess.Organisation.OrganisationService;
import com.example.hiringProcess.Step.Step;
import com.example.hiringProcess.Step.StepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/v1/step-scores")

public class StepScoreController {

    private final StepScoreService stepScoreService;
    private final StepService stepService;
    private final OrganisationService organizationService;
    private final InterviewReportService interviewReportService;
    private final CandidateService candidateService;

    public StepScoreController(StepScoreService stepScoreService, CandidateService candidateService, StepService stepService, InterviewReportService interviewReportService, OrganisationService organizationService) {
        this.organizationService = organizationService;
        this.stepScoreService = stepScoreService;
        this.stepService = stepService;
        this.interviewReportService = interviewReportService;
        this.candidateService = candidateService;
    }

    // Επιστρέφει ένα συγκεκριμένο Step με βάση το stepId
    @GetMapping("/{stepId}")
    public ResponseEntity<Step> getStepById(@PathVariable("stepId") Integer stepId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!stepService.existsByOrg(stepId, orgId)){
            return ResponseEntity.notFound().build();
        }
        return stepService.getStepById(stepId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Επιστρέφει όλα τα Steps
    @GetMapping
    public ResponseEntity<List<Step>> getAllSteps(@RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return ResponseEntity.ok(stepService.getAllStepsByOrg(orgId));
    }

    // Επιστρέφει όλα τα Steps που ανήκουν σε συγκεκριμένο InterviewReport
    @GetMapping("/by-report")
    public ResponseEntity<List<Step>> getStepsByInterviewReport(
            @RequestParam Integer interviewReportId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        // 1. Security Check: Does this department belong to this organization?
        if (!interviewReportService.existsByOrg(interviewReportId, orgId)) {
            return ResponseEntity.notFound().build();
        }
        List<Step> steps = stepScoreService.getStepsByInterviewReportId(interviewReportId);
        return ResponseEntity.ok(steps);
    }

    // Επιστρέφει τα metrics (στατιστικά/αποτελέσματα) για έναν υποψήφιο
    @GetMapping("/metrics")
    public ResponseEntity<List<StepMetricsItemDTO>> getMetricsByCandidate(
            @RequestParam Integer candidateId,
            @RequestParam String stepIds,
            @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName
    ) {
        // 1. Get Org ID from header
        Integer orgId = organizationService.getIdByName(headerOrgName);

        // 2. Security Check: Does the candidate belong to this org?
        if (!candidateService.existsByOrg(candidateId, orgId)) {
            return ResponseEntity.notFound().build();
        }

        List<Integer> sids = Arrays.stream(stepIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .toList();

        if (!stepService.allStepsBelongToOrg(sids, orgId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(stepScoreService.getStepMetricsByCandidate(candidateId, sids));
    }
}
