package com.example.hiringProcess.Question;

import com.example.hiringProcess.Organisation.OrganisationService;
import com.example.hiringProcess.Step.StepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping
public class QuestionController {

    private final QuestionService questionService;
    private final OrganisationService organizationService;
    private final StepService stepService;

    public QuestionController(QuestionService questionService, OrganisationService organizationService, StepService stepService) {
        this.organizationService = organizationService;
        this.questionService = questionService;
        this.stepService = stepService;
    }

    // ===== LEGACY =====
//    @GetMapping(path = "/questions") //OK
//    public List<Question> getQuestions() {
//        return questionService.getQuestions();
//    }

    @GetMapping(path = "/question") //OK
    public Optional<Question> getQuestion( @RequestParam Integer questionId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return questionService.getQuestion(questionId, orgId);
    }

//    @PostMapping(path = "/newQuestion") //TODO check USE
//    public void addNewQuestion(@RequestBody Question question) {
//        questionService.addNewQuestion(question);
//    }

    @DeleteMapping(path = "/question") //OK
    public void deleteQuestionLegacy( @RequestParam Integer questionId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        questionService.deleteQuestion(questionId, orgId);
    }

    @PutMapping(path = "/question/{questionId}") //OK
    public void updateQuestion( @PathVariable Integer questionId, @RequestBody Question body, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        questionService.updateQuestion(questionId, body, orgId);
    }

    // ===== ΝΕΑ ENDPOINTS ΓΙΑ ΤΟ UI =====

    /** Αριστερό panel: ερωτήσεις ανά step (ταξινομημένες). */
    @GetMapping("/api/v1/step/{stepId}/questions")  //TODO check USE
    public ResponseEntity<List<QuestionLiteDTO>> getQuestionsForStep(@PathVariable Integer stepId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!stepService.existsByOrg(stepId, orgId)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(questionService.getQuestionsForStep(stepId));
    }

    /** Create question κάτω από συγκεκριμένο step. */
    @PostMapping("/api/v1/step/{stepId}/questions")  //TODO check USE
    public ResponseEntity<QuestionLiteDTO> createQuestionUnderStep(@PathVariable Integer stepId,
                                                                   @RequestBody QuestionCreateRequest req, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!stepService.existsByOrg(stepId, orgId)){
            return ResponseEntity.notFound().build();
        }

        if (req == null || req.getName() == null || req.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        var created = questionService.createUnderStep(
                stepId,
                req.getName().trim(),
                req.getDescription() == null ? null : req.getDescription().trim()
        );
        var dto = new QuestionLiteDTO(created.getId(), created.getTitle());
        return ResponseEntity.created(URI.create("/api/v1/question/" + created.getId())).body(dto);
    }

    /** Δεξί panel: description + skills της επιλεγμένης ερώτησης. */
    @GetMapping("/api/v1/question/{questionId}/details")  //TODO check USE
    public ResponseEntity<QuestionDetailsDTO> getQuestionDetails(@PathVariable Integer questionId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!questionService.existsByOrg(questionId, orgId)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(questionService.getQuestionDetails(questionId));
    }

    /** Update description + skills */
    @PutMapping("/api/v1/question/{questionId}")  //TODO check USE
    public ResponseEntity<Void> updateQuestionDescAndSkills(@PathVariable Integer questionId,
                                                            @RequestBody QuestionUpdateRequest body, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!questionService.existsByOrg(questionId, orgId)){
            return ResponseEntity.notFound().build();
        }
        questionService.updateDescriptionAndSkills(
                questionId,
                body.getDescription(),
                body.getSkillNames()
        );
        return ResponseEntity.noContent().build();
    }

    /** Reorder ερωτήσεων ΜΕΣΑ στο ίδιο step */
    @PatchMapping("/api/v1/step/{stepId}/questions/reorder")  //TODO check USE
    public ResponseEntity<Void> reorderQuestionsInStep(@PathVariable Integer stepId,
                                                       @RequestBody QuestionReorderRequest body, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!stepService.existsByOrg(stepId, orgId)){
            return ResponseEntity.notFound().build();
        }
        questionService.reorderInStep(stepId, body.getQuestionIds());
        return ResponseEntity.noContent().build();
    }

    /** Μετακίνηση ερώτησης σε άλλο step (και θέση) */
    @PatchMapping("/api/v1/question/{questionId}/move")  //TODO check USE
    public ResponseEntity<Void> moveQuestion(@PathVariable Integer questionId,
                                             @RequestBody QuestionMoveRequest body, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!questionService.existsByOrg(questionId, orgId)){
            return ResponseEntity.notFound().build();
        }
        questionService.moveQuestion(questionId, body.getToStepId(), body.getToIndex());
        return ResponseEntity.noContent().build();
    }

    /** DELETE endpoint που χρησιμοποιεί το UI (StepsTree) */
    @DeleteMapping("/api/v1/question/{questionId}")  //TODO check USE
    public ResponseEntity<Void> deleteQuestionV1(@PathVariable Integer questionId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if(!stepService.existsByOrg(questionId, orgId)){
            return ResponseEntity.notFound().build();
        }
        questionService.deleteQuestion(questionId, orgId);
        return ResponseEntity.noContent().build();
    }
}
