package com.example.hiringProcess.QuestionScore;

import com.example.hiringProcess.Candidate.CandidateService;
import com.example.hiringProcess.InterviewReport.InterviewReportService;
import com.example.hiringProcess.Organisation.OrganisationService;
import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.Question.QuestionService;
import com.example.hiringProcess.Step.StepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/v1/question-scores")
public class QuestionScoreController {

    private final QuestionScoreService questionScoreService;
    private final OrganisationService organizationService;
    private final StepService stepService;
    private final CandidateService candidateService;
    private final QuestionService questionService;
    private final InterviewReportService interviewReportService;

    public QuestionScoreController(QuestionScoreService questionScoreService, InterviewReportService interviewReportService, OrganisationService organizationService, StepService stepService, QuestionService questionService, CandidateService candidateService) {
        this.organizationService = organizationService;
        this.questionScoreService = questionScoreService;
        this.stepService = stepService;
        this.candidateService = candidateService;
        this.questionService = questionService;
        this.interviewReportService = interviewReportService;
    }

    // Επιστρέφει όλα τα QuestionScores
    @GetMapping
    public List<QuestionScore> getAll(@RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return questionScoreService.getAllByOrg(orgId);
    }

    // Επιστρέφει ένα QuestionScore με βάση το id
    @GetMapping("/{id}") //TODO check USE
    public ResponseEntity<QuestionScore> getById(@PathVariable("id") Integer id, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if (!questionScoreService.existsByOrg(id, orgId)) {
            return ResponseEntity.notFound().build();
        }

        return questionScoreService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Επιστρέφει μια λίστα QuestionScore που αντιστοιχούν σε ένα step
    @GetMapping("/by-step") //TODO check USE
    public ResponseEntity<List<Question>> getQuestionsByStep(
            @RequestParam Integer stepId,
            @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if (!stepService.existsByOrg(stepId, orgId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(questionScoreService.getQuestionsByStep(stepId));
    }

    // Επιστρέφει metrics για ερωτήσεις συγκεκριμένου candidate (μέσω του interviewReport του)
    @GetMapping("/metrics") //OK
    public ResponseEntity<List<QuestionMetricsItemDTO>> getMetricsByCandidate(
            @RequestParam Integer candidateId,
            @RequestParam String questionIds,
            @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName
    ) {
        // 1. Get the Org ID from the header
        Integer orgId = organizationService.getIdByName(headerOrgName);

        // 2. Security Check: Does the candidate belong to this org?
        if (!candidateService.existsByOrg(candidateId, orgId)) {
            return ResponseEntity.notFound().build();
        }

        List<Integer> qids = Arrays.stream(questionIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .toList();

        if (!questionService.allQuestionsBelongToOrg(qids, orgId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(
                questionScoreService.getQuestionMetricsByCandidate(candidateId, qids)
        );
    }

    @GetMapping("/metrics-by-report") //OK
    public ResponseEntity<List<QuestionMetricsItemDTO>> getMetricsByReport(
            @RequestParam Integer interviewReportId,
            @RequestParam String questionIds,
            @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName
    ) {
        // 1. Get Org ID from header
        Integer orgId = organizationService.getIdByName(headerOrgName);

        // 2. Security Check: Does this Interview Report belong to this org?
        if (!interviewReportService.existsByOrg(interviewReportId, orgId)) {
            return ResponseEntity.notFound().build();
        }

        List<Integer> qids = Arrays.stream(questionIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .toList();

        if (!questionService.allQuestionsBelongToOrg(qids, orgId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(
                questionScoreService.getQuestionMetricsByReport(interviewReportId, qids)
        );
    }

}
