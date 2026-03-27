package com.example.hiringProcess.SkillScore;

import com.example.hiringProcess.Organisation.OrganisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizations/{orgId}/skill-scores")
public class SkillScoreController {

    private final SkillScoreService skillScoreService;
    private final OrganisationService organizationService;

    public SkillScoreController(SkillScoreService skillScoreService, OrganisationService organizationService) {
        this.organizationService = organizationService;
        this.skillScoreService = skillScoreService;
    }

    // Δημιουργεί ή ενημερώνει τη βαθμολογία για συγκεκριμένο candidate–question–skill
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SkillScoreResponseDTO> upsert( @RequestBody SkillScoreUpsertRequestDTO req, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if (req == null || req.candidateId() == 0 || req.questionId() == 0 || req.skillId() == 0) {
            return ResponseEntity.badRequest().build();
        }

        SkillScoreResponseDTO resp = skillScoreService.upsert(req, orgId);
        return ResponseEntity
                .status(resp.created() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(resp);
    }

    //Επιστρέφει λίστα βαθμολογιών ενός υποψηφίου για συγκεκριμένη ερώτηση
    @GetMapping(value = "/candidate/{candidateId}/question/{questionId}", //OK
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SkillScoreResponseDTO> listForCandidateQuestion(
             @PathVariable int candidateId,
            @PathVariable int questionId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);
        return skillScoreService.listForCandidateQuestion(candidateId, questionId, orgId);
    }

    // Διαγράφει βάσει tuple (candidate, question, skill)
    @DeleteMapping("/candidate/{candidateId}/question/{questionId}/skill/{skillId}") //TODO check USE
    public ResponseEntity<Void> deleteTuple(
            @PathVariable int candidateId,
            @PathVariable int questionId,
            @PathVariable int skillId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);
        skillScoreService.deleteTuple(candidateId, questionId, skillId, orgId);
        return ResponseEntity.noContent().build();
    }

    // Επιστρέφει όλα τα skill scores
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) //OK
    public List<SkillScoreResponseDTO> listAll(@RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);
        return skillScoreService.listAll(orgId);
    }

    // Επιστρέφει όλα τα skill scores για συγκεκριμένο question (όλων των υποψηφίων)
    @GetMapping(value = "/question/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE) //TODO check USE
    public List<SkillScoreResponseDTO> listForQuestion( @PathVariable int questionId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);
        return skillScoreService.listForQuestion(questionId, orgId);
    }

}
