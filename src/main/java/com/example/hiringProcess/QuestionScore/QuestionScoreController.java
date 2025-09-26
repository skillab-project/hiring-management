package com.example.hiringProcess.QuestionScore;

import com.example.hiringProcess.Question.Question;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/v1/question-scores")
public class QuestionScoreController {

    private final QuestionScoreService questionScoreService;

    public QuestionScoreController(QuestionScoreService questionScoreService) {
        this.questionScoreService = questionScoreService;
    }

    // Επιστρέφει όλα τα QuestionScores
    @GetMapping
    public List<QuestionScore> getAll() {
        return questionScoreService.getAll();
    }

    // Επιστρέφει ένα QuestionScore με βάση το id
    @GetMapping("/{id}")
    public ResponseEntity<QuestionScore> getById(@PathVariable("id") Integer id) {
        return questionScoreService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Επιστρέφει μια λίστα QuestionScore που αντιστοιχούν σε ένα step
    @GetMapping("/by-step")
    public ResponseEntity<List<Question>> getQuestionsByStep(
            @RequestParam Integer stepId
    ) {
        return ResponseEntity.ok(questionScoreService.getQuestionsByStep(stepId));
    }

    // Επιστρέφει metrics για ερωτήσεις συγκεκριμένου candidate (μέσω του interviewReport του)
    @GetMapping("/metrics")
    public ResponseEntity<List<QuestionMetricsItemDTO>> getMetricsByCandidate(
            @RequestParam Integer candidateId,
            @RequestParam String questionIds
    ) {
        List<Integer> qids = Arrays.stream(questionIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .toList();

        return ResponseEntity.ok(
                questionScoreService.getQuestionMetricsByCandidate(candidateId, qids)
        );
    }
    @GetMapping("/metrics-by-report")
    public ResponseEntity<List<QuestionMetricsItemDTO>> getMetricsByReport(
            @RequestParam Integer interviewReportId,
            @RequestParam String questionIds
    ) {
        List<Integer> qids = Arrays.stream(questionIds.split(","))
                .map(String::trim).filter(s -> !s.isEmpty())
                .map(Integer::valueOf).toList();

        return ResponseEntity.ok(
                questionScoreService.getQuestionMetricsByReport(interviewReportId, qids)
        );
    }

}
