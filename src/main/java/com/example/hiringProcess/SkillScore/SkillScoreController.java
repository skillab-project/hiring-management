package com.example.hiringProcess.SkillScore;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skill-scores")
public class SkillScoreController {

    private final SkillScoreService skillScoreService;

    public SkillScoreController(SkillScoreService skillScoreService) {
        this.skillScoreService = skillScoreService;
    }

    // Δημιουργεί ή ενημερώνει τη βαθμολογία για συγκεκριμένο candidate–question–skill
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SkillScoreResponseDTO> upsert(
            @RequestBody SkillScoreUpsertRequestDTO req) {

        if (req == null || req.candidateId() == 0 || req.questionId() == 0 || req.skillId() == 0) {
            return ResponseEntity.badRequest().build();
        }

        SkillScoreResponseDTO resp = skillScoreService.upsert(req);
        return ResponseEntity
                .status(resp.created() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(resp);
    }

    //Επιστρέφει λίστα βαθμολογιών ενός υποψηφίου για συγκεκριμένη ερώτηση
    @GetMapping(value = "/candidate/{candidateId}/question/{questionId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SkillScoreResponseDTO> listForCandidateQuestion(
            @PathVariable int candidateId,
            @PathVariable int questionId) {
        return skillScoreService.listForCandidateQuestion(candidateId, questionId);
    }

    // Διαγράφει βάσει tuple (candidate, question, skill)
    @DeleteMapping("/candidate/{candidateId}/question/{questionId}/skill/{skillId}")
    public ResponseEntity<Void> deleteTuple(
            @PathVariable int candidateId,
            @PathVariable int questionId,
            @PathVariable int skillId) {
        skillScoreService.deleteTuple(candidateId, questionId, skillId);
        return ResponseEntity.noContent().build();
    }

    // Επιστρέφει όλα τα skill scores
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SkillScoreResponseDTO> listAll() {
        return skillScoreService.listAll();
    }

    // Επιστρέφει όλα τα skill scores για συγκεκριμένο question (όλων των υποψηφίων)
    @GetMapping(value = "/question/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SkillScoreResponseDTO> listForQuestion(@PathVariable int questionId) {
        return skillScoreService.listForQuestion(questionId);
    }

}
