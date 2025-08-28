package com.example.hiringProcess.SkillScore;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000","http://localhost:5173"})
@RestController
@RequestMapping("/api/v1/skill-scores")
public class SkillScoreController {

    private final SkillScoreService skillScoreService;

    public SkillScoreController(SkillScoreService skillScoreService) {
        this.skillScoreService = skillScoreService;
    }

    /** Upsert βαθμολογίας για (candidate, question, skill). */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SkillScoreResponseDTO> upsert(
            @RequestBody SkillScoreUpsertRequestDTO req) {

        // (προαιρετικό) basic validation
        if (req == null || req.candidateId() == 0 || req.questionId() == 0 || req.skillId() == 0) {
            return ResponseEntity.badRequest().build();
        }

        SkillScoreResponseDTO resp = skillScoreService.upsert(req);
        return ResponseEntity
                .status(resp.created() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(resp);
    }

    /** Λίστα βαθμολογιών ενός υποψηφίου για συγκεκριμένη ερώτηση (προφόρτωση UI). */
    @GetMapping(value = "/candidate/{candidateId}/question/{questionId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SkillScoreResponseDTO> listForCandidateQuestion(
            @PathVariable int candidateId,
            @PathVariable int questionId) {
        return skillScoreService.listForCandidateQuestion(candidateId, questionId);
    }

    /** Διαγραφή με id εγγραφής skill_score. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        skillScoreService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** Διαγραφή βάσει tuple (candidate, question, skill). */
    @DeleteMapping("/candidate/{candidateId}/question/{questionId}/skill/{skillId}")
    public ResponseEntity<Void> deleteTuple(
            @PathVariable int candidateId,
            @PathVariable int questionId,
            @PathVariable int skillId) {
        skillScoreService.deleteTuple(candidateId, questionId, skillId);
        return ResponseEntity.noContent().build();
    }
}
