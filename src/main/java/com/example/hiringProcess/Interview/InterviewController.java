package com.example.hiringProcess.Interview;

import com.example.hiringProcess.Step.StepCreateRequest;
import com.example.hiringProcess.Step.StepResponseDTO;
import com.example.hiringProcess.Step.StepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class InterviewController {

    private final InterviewService interviewService;
    private final StepService stepService;

    @Autowired
    public InterviewController(InterviewService interviewService,
                               StepService stepService) {
        this.interviewService = interviewService;
        this.stepService = stepService;
    }

    @GetMapping("/interviews")
    public List<Interview> getInterviews() {
        return interviewService.getInterviews();
    }

    @GetMapping("/jobAds/{jobAdId}/interview-details")
    public ResponseEntity<InterviewDetailsDTO> getInterviewDetailsByJobAd(@PathVariable Integer jobAdId) {
        InterviewDetailsDTO dto = interviewService.getInterviewDetailsByJobAd(jobAdId);
        return (dto != null) ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/interviews/{interviewId}")
    public ResponseEntity<Interview> getInterviewByPath(@PathVariable Integer interviewId) {
        Optional<Interview> it = interviewService.getInterview(interviewId);
        return it.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/interview")
    public Optional<Interview> getInterview(@RequestParam Integer interviewId) {
        return interviewService.getInterview(interviewId);
    }

    @PostMapping("/interviews")
    public void addInterview(@RequestBody Interview interview) {
        interviewService.addNewInterview(interview);
    }

    @PostMapping("/newinterview")
    public void addNewInterview(@RequestBody Interview interview) {
        interviewService.addNewInterview(interview);
    }

    // ✅ Δημιουργία Step και επιστροφή DTO
    @PostMapping("/interviews/{interviewId}/steps")
    public ResponseEntity<StepResponseDTO> addStepToInterview(
            @PathVariable Integer interviewId,
            @RequestBody StepCreateRequest body) {

        if (body == null || body.getTitle() == null || body.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        StepResponseDTO created = stepService.createAtEnd(
                interviewId,
                body.getTitle().trim(),
                body.getDescription() == null ? "" : body.getDescription().trim()
        );
        return ResponseEntity.ok(created);
    }



    // ✅ Save interview description
    @PutMapping("/interviews/{interviewId}/description")
    public ResponseEntity<Void> updateInterviewDescription(
            @PathVariable Integer interviewId,
            @RequestBody InterviewDescriptionDTO body) {

        if (body == null || body.description() == null) {
            return ResponseEntity.badRequest().build();
        }
        interviewService.updateDescription(interviewId, body.description());
        return ResponseEntity.ok().build();
    }
}
