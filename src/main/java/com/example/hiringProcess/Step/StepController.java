package com.example.hiringProcess.Step;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:3000","http://localhost:5173"})
@RestController
@RequestMapping(path = "api/v1/step")
public class StepController {

    private final StepService stepService;

    @Autowired
    public StepController(StepService stepService) {
        this.stepService = stepService;
    }

    // (προϋπάρχοντα – αν τα χρειάζεσαι)
    @GetMapping("/steps")
    public List<Step> getSteps() { return stepService.getSteps(); }

    @GetMapping("/step")
    public Optional<Step> getStep(@RequestParam Integer stepId) { return stepService.getStep(stepId); }

    @PostMapping("/newstep")
    public void addNewStep(@RequestBody Step step) { stepService.addNewStep(step); }

    @DeleteMapping("/{stepId}")
    public ResponseEntity<Void> deleteStep(@PathVariable("stepId") Integer stepId) {
        stepService.deleteStep(stepId);
        return ResponseEntity.noContent().build();
    }

    /* ======= Skills για step (για το δεξί panel Questions) ======= */
    @GetMapping("/{stepId}/skills")
    public List<StepSkillDTO> getStepSkills(@PathVariable Integer stepId) {
        return stepService.getSkillsForStep(stepId);
    }

    /* ======= Λίστα/δημιουργία βημάτων ανά interview (DTOs) ======= */
    @GetMapping("/interviews/{interviewId}/steps")
    public List<StepResponseDTO> listByInterview(@PathVariable Integer interviewId) {
        return stepService.getStepsByInterviewSorted(interviewId);
    }

    @PostMapping("/interviews/{interviewId}/steps")
    public StepResponseDTO createAtEnd(@PathVariable Integer interviewId,
                                       @RequestBody StepCreateRequest req) {
        String title = req.getTitle() == null ? "" : req.getTitle().trim();
        String desc  = req.getDescription() == null ? "" : req.getDescription().trim();
        if (title.isBlank()) throw new IllegalStateException("title required");
        return stepService.createAtEnd(interviewId, title, desc);
    }

    /* ======= Ενημέρωση περιγραφής/τίτλου step ======= */
    @PutMapping("/{stepId}/description")
    public ResponseEntity<Void> updateDescription(@PathVariable int stepId,
                                                  @RequestBody StepUpdateDTO dto) {
        stepService.updateStep(stepId, dto);   // ή stepService.updateStepDescription(stepId, dto.getDescription())
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/steps/{id}")
    public ResponseEntity<Void> updateStep(@PathVariable int id,
                                           @RequestBody StepUpdateDTO dto) {
        stepService.updateStep(id, dto);
        return ResponseEntity.noContent().build();
    }

    /* ======= Μετακίνηση/αναδιάταξη ======= */
    @PatchMapping("/{stepId}/move")
    public void move(@PathVariable Integer stepId, @RequestParam String direction) {
        stepService.move(stepId, direction);
    }

    @PatchMapping("/interviews/{interviewId}/steps/reorder")
    public ResponseEntity<?> reorder(@PathVariable int interviewId,
                                     @RequestBody StepReorderRequest body) {
        try {
            stepService.reorder(interviewId, body.getStepIds());
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
