package com.example.hiringProcess.Step;

import com.example.hiringProcess.Interview.InterviewService;
import com.example.hiringProcess.Organisation.OrganisationService;
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
    private final OrganisationService organizationService;
    private final InterviewService interviewService;

    @Autowired
    public StepController(StepService stepService, OrganisationService organizationService, InterviewService interviewService) {
        this.organizationService = organizationService;
        this.stepService = stepService;
        this.interviewService = interviewService;
    }

//    // (προϋπάρχοντα – αν τα χρειάζεσαι)
//    @GetMapping("/steps") //TODO check to remove
//    public List<Step> getSteps() { return stepService.getSteps(); }

    @GetMapping //OK
    public ResponseEntity<Optional<Step>> getStep(@RequestParam Integer stepId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!stepService.existsByOrg(stepId, orgId)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stepService.getStep(stepId));
    }

//    @PostMapping("/newstep") //TODO check to remove
//    public void addNewStep(@RequestBody Step step) { stepService.addNewStep(step); }

    @DeleteMapping("/{stepId}") //OK
    public ResponseEntity<Void> deleteStep(@PathVariable("stepId") Integer stepId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!stepService.existsByOrg(stepId, orgId)){
            return ResponseEntity.notFound().build();
        }
        stepService.deleteStep(stepId);
        return ResponseEntity.noContent().build();
    }

    /* ======= Skills για step (για το δεξί panel Questions) ======= */
    @GetMapping("/{stepId}/skills") //OK
    public ResponseEntity<List<StepSkillDTO>> getStepSkills(@PathVariable Integer stepId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!stepService.existsByOrg(stepId, orgId)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stepService.getSkillsForStep(stepId));
    }

    /* ======= Λίστα/δημιουργία βημάτων ανά interview (DTOs) ======= */
    @GetMapping("/interviews/{interviewId}/steps") //OK
    public ResponseEntity<List<StepResponseDTO>> listByInterview(@PathVariable Integer interviewId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!interviewService.existsByOrg(interviewId, orgId)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stepService.getStepsByInterviewSorted(interviewId));
    }

    @PostMapping("/interviews/{interviewId}/steps") //OK
    public ResponseEntity<StepResponseDTO> createAtEnd(@PathVariable Integer interviewId,
                                       @RequestBody StepCreateRequest req, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!interviewService.existsByOrg(interviewId, orgId)){
            return ResponseEntity.notFound().build();
        }
        String title = req.getTitle() == null ? "" : req.getTitle().trim();
        String desc  = req.getDescription() == null ? "" : req.getDescription().trim();
        if (title.isBlank()) throw new IllegalStateException("title required");
        return ResponseEntity.ok(stepService.createAtEnd(interviewId, title, desc));
    }

    /* ======= Ενημέρωση περιγραφής/τίτλου step ======= */
    @PutMapping("/{stepId}/description") //OK
    public ResponseEntity<Void> updateDescription(@PathVariable int stepId,
                                                  @RequestBody StepUpdateDTO dto, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!stepService.existsByOrg(stepId, orgId)){
            return ResponseEntity.notFound().build();
        }

        stepService.updateStep(stepId, dto);   // ή stepService.updateStepDescription(stepId, dto.getDescription())
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/steps/{stepId}") //TODO check name typo  //TODO check to remove
    public ResponseEntity<Void> updateStep(@PathVariable int stepId, //TODO this was name id, and not stepId
                                           @RequestBody StepUpdateDTO dto, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!stepService.existsByOrg(stepId, orgId)){
            return ResponseEntity.notFound().build();
        }
        stepService.updateStep(stepId, dto);
        return ResponseEntity.noContent().build();
    }

    /* ======= Μετακίνηση/αναδιάταξη ======= */
    @PatchMapping("/{stepId}/move") //TODO check to remove
    public void move(@PathVariable Integer stepId, @RequestParam String direction, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!stepService.existsByOrg(stepId, orgId)){
            return;
        }
        stepService.move(stepId, direction);
    }

    @PatchMapping("/interviews/{interviewId}/steps/reorder") //OK
    public ResponseEntity<?> reorder(@PathVariable int interviewId,
                                     @RequestBody StepReorderRequest body, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!interviewService.existsByOrg(interviewId, orgId)){
            return ResponseEntity.notFound().build();
        }
        try {
            stepService.reorder(interviewId, body.getStepIds());
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
