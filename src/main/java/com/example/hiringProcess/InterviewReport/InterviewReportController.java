package com.example.hiringProcess.InterviewReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/api/v1/interview-reports")
public class InterviewReportController {

    private final InterviewReportService interviewReportService;

    public InterviewReportController(InterviewReportService interviewReportService) {
        this.interviewReportService = interviewReportService;
    }

    // Επιστρέφει όλα τα InterviewReports
    @GetMapping
    public List<InterviewReport> getAll() {
        return interviewReportService.getAll();
    }

    // Επιστρέφει InterviewReport με βάση το id
    @GetMapping("/{id}")
    public ResponseEntity<InterviewReport> getById(@PathVariable("id") Integer id) {
        return interviewReportService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Δημιουργεί νέο InterviewReport
    @PostMapping
    public ResponseEntity<InterviewReport> create(@RequestBody InterviewReport report) {
        InterviewReport saved = interviewReportService.create(report);
        URI location = URI.create("/api/v1/interview-reports/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    // Κάνει update σε υπάρχον InterviewReport
    @PutMapping("/{id}")
    public ResponseEntity<InterviewReport> update(
            @PathVariable("id") Integer id,
            @RequestBody InterviewReport updatedFields) {

        InterviewReport updated = interviewReportService.update(id, updatedFields);
        return ResponseEntity.ok(updated);
    }

    // Διαγράφει InterviewReport με βάση το id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        interviewReportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
