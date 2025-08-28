package com.example.hiringProcess.QuestionScore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/question-scores")
public class QuestionScoreController {

    private final QuestionScoreService questionScoreService;

    @Autowired
    public QuestionScoreController(QuestionScoreService questionScoreService) {
        this.questionScoreService = questionScoreService;
    }

    @GetMapping
    public List<QuestionScore> getAll() {
        return questionScoreService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionScore> getById(@PathVariable("id") Integer id) {
        return questionScoreService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<QuestionScore> create(@RequestBody QuestionScore questionScore) {
        QuestionScore saved = questionScoreService.create(questionScore);
        URI location = URI.create("/api/v1/question-scores/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionScore> update(
            @PathVariable("id") Integer id,
            @RequestBody QuestionScore updatedFields
    ) {
        QuestionScore updated = questionScoreService.update(id, updatedFields);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        questionScoreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
