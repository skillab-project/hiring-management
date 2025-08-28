package com.example.hiringProcess.Assessment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/assessment")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentAggregationService assessmentAggregationService;

    @GetMapping("/interviews/{interviewId}/candidates/{candidateId}/steps")
    public List<StepAssessmentDTO> stepAssessments(
            @PathVariable int interviewId,
            @PathVariable int candidateId
    ) {
        return assessmentAggregationService.stepAssessments(interviewId, candidateId);
    }
}
