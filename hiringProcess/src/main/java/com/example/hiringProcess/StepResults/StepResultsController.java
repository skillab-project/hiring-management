package com.example.hiringProcess.StepResults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/step-results")
public class StepResultsController {

    private final StepResultsService stepResultsService;

    @Autowired
    public StepResultsController(StepResultsService stepResultsService) {
        this.stepResultsService = stepResultsService;
    }

    @GetMapping
    public List<StepResults> getAllStepResults() {
        return stepResultsService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StepResults> getStepResult(@PathVariable("id") Integer id) {
        return stepResultsService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StepResults> createStepResult(@RequestBody StepResults stepResults) {
        StepResults saved = stepResultsService.create(stepResults);
        URI location = URI.create("/api/v1/step-results/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StepResults> updateStepResult(
            @PathVariable("id") Integer id,
            @RequestBody StepResults updatedFields) {

        StepResults updated = stepResultsService.update(id, updatedFields);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStepResult(@PathVariable("id") Integer id) {
        stepResultsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
