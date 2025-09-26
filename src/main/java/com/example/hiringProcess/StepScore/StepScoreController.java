package com.example.hiringProcess.StepScore;

import com.example.hiringProcess.Step.Step;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/v1/step-scores")

public class StepScoreController {

    private final StepScoreService service;

    public StepScoreController(StepScoreService service) {
        this.service = service;
    }

    // Επιστρέφει ένα συγκεκριμένο Step με βάση το id
    @GetMapping("/{id}")
    public ResponseEntity<Step> getStepById(@PathVariable("id") Integer id) {
        return service.getStepById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Επιστρέφει όλα τα Steps
    @GetMapping
    public ResponseEntity<List<Step>> getAllSteps() {
        return ResponseEntity.ok(service.getAllSteps());
    }

    // Επιστρέφει όλα τα Steps που ανήκουν σε συγκεκριμένο InterviewReport
    @GetMapping("/by-report")
    public ResponseEntity<List<Step>> getStepsByInterviewReport(
            @RequestParam Integer interviewReportId
    ) {
        List<Step> steps = service.getStepsByInterviewReportId(interviewReportId);
        return ResponseEntity.ok(steps);
    }

    // Επιστρέφει τα metrics (στατιστικά/αποτελέσματα) για έναν υποψήφιο
    @GetMapping("/metrics")
    public ResponseEntity<List<StepMetricsItemDTO>> getMetricsByCandidate(
            @RequestParam Integer candidateId,
            @RequestParam String stepIds
    ) {
        List<Integer> sids = Arrays.stream(stepIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .toList();

        return ResponseEntity.ok(service.getStepMetricsByCandidate(candidateId, sids));
    }
}
