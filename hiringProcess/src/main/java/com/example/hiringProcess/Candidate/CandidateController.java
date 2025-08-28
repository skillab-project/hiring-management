package com.example.hiringProcess.Candidate;

import com.example.hiringProcess.SkillScore.SkillScore;
import com.example.hiringProcess.SkillScore.SkillScoreService;
import com.example.hiringProcess.SkillScore.SkillScoreUpsertRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.hiringProcess.SkillScore.SkillScoreResponseDTO;
import com.example.hiringProcess.SkillScore.SkillScoreUpsertRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1/candidates")
@CrossOrigin(origins = "http://localhost:3000") // dev
public class CandidateController {

    private final CandidateService candidateService;
    private final SkillScoreService skillScoreService; // 👈 προσθήκη

    @Value("${app.cv.dir:/opt/app/uploads/cv}")
    private String cvBaseDir;

    @Autowired
    public CandidateController(CandidateService candidateService,
                               SkillScoreService skillScoreService) { // 👈 προσθήκη
        this.candidateService = candidateService;
        this.skillScoreService = skillScoreService;
    }

    // -------- DETAILS (Entity) --------
    @GetMapping("/{id}")
    public Candidate getCandidate(@PathVariable Integer id) {
        return candidateService.getCandidate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));
    }

    // -------- UPDATE (Entity) --------
    @PutMapping("/{id}")
    public ResponseEntity<Candidate> updateCandidate(
            @PathVariable("id") Integer id,
            @RequestBody Candidate updatedCandidate) {
        Candidate updated = candidateService.updateCandidate(id, updatedCandidate);
        return ResponseEntity.ok(updated);
    }

    // -------- DELETE --------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Integer id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    // -------- LIST (DTO) --------
    @GetMapping
    public List<CandidateDTO> getCandidates() {
        return candidateService.getCandidateDTOs();
    }

    // -------- LIST BY JOB AD (DTO) --------
    @GetMapping("/jobad/{jobAdId}")
    public List<CandidateDTO> getCandidatesByJobAd(@PathVariable Integer jobAdId) {
        return candidateService.getCandidateDTOsByJobAd(jobAdId);
    }

    // -------- COMMENTS (write) --------
    @PatchMapping("/{id}/comments")
    public ResponseEntity<Void> saveCandidateComment(@PathVariable Integer id,
                                                     @RequestBody CandidateCommentDTO dto) {
        if (dto == null || dto.getComments() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "comments is required");
        }
        candidateService.updateComments(id, dto.getComments());
        return ResponseEntity.noContent().build();
    }

    // -------- EVALUATIONS (compat wrapper -> SkillScore upsert) --------
    /**
     * Συμβατότητα με παλιό front:
     * POST /api/v1/candidates/{id}/evaluations
     * Body: SkillEvaluationDTO { questionId, skillId, rating, comments }
     *
     * Μετατρέπουμε σε SkillScoreUpsertRequestDTO και κάνουμε upsert στη skill_score.
     */
    @PostMapping("/{id}/evaluations")
    public ResponseEntity<SkillScoreResponseDTO> saveSkillEvaluation(
            @PathVariable int id,
            @RequestBody SkillScoreUpsertRequestDTO dto) {

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body is required");
        }
        if (dto.questionId() == 0 || dto.skillId() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "questionId and skillId are required");
        }
        Integer rating = dto.score();
        if (rating != null && (rating < 0 || rating > 100)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rating must be between 0..100");
        }

        SkillScoreUpsertRequestDTO safeDto = new SkillScoreUpsertRequestDTO(
                id,
                dto.questionId(),
                dto.skillId(),
                dto.score(),
                dto.comment(),
                "system" // ή βάλε τον πραγματικό χρήστη όταν έχεις auth
        );


        // ⬇️ επιστρέφει DTO (ΟΧΙ entity)
        SkillScoreResponseDTO saved = skillScoreService.upsert(safeDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    // -------- CV DOWNLOAD --------
    @GetMapping("/{id}/cv")
    public ResponseEntity<Resource> downloadCv(@PathVariable Integer id) {
        Candidate candidate = candidateService.getCandidate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));

        String cvPath = candidate.getCvPath();
        if (cvPath == null || cvPath.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CV not available");
        }

        Path filePath = Paths.get(cvBaseDir).resolve(cvPath).normalize();
        if (!Files.exists(filePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CV file not found");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            String filename = filePath.getFileName().toString();
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid CV path");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CV download failed");
        }
    }
}
