package com.example.hiringProcess.Candidate;

import com.example.hiringProcess.SkillScore.SkillScoreResponseDTO;
import com.example.hiringProcess.SkillScore.SkillScoreService;
import com.example.hiringProcess.SkillScore.SkillScoreUpsertRequestDTO;
import com.example.hiringProcess.JobAd.JobAdRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/candidates")
@CrossOrigin(origins = {"http://localhost:3000"})
public class CandidateController {

    private final CandidateService candidateService;
    private final SkillScoreService skillScoreService;
    private final JobAdRepository jobAdRepository;

    public CandidateController(CandidateService candidateService,
                               SkillScoreService skillScoreService,
                               JobAdRepository jobAdRepository) {
        this.candidateService = candidateService;
        this.skillScoreService = skillScoreService;
        this.jobAdRepository = jobAdRepository;
    }

    // Επιστροφή συγκεκριμένου υποψηφίου
    @GetMapping("/{id}")
    public Candidate getCandidate(@PathVariable Integer id) {
        return candidateService.getCandidate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));
    }

    // Ενημέρωση στοιχείων υποψηφίου
    @PutMapping("/{id}")
    public ResponseEntity<Candidate> updateCandidate(
            @PathVariable("id") Integer id,
            @RequestBody Candidate updatedCandidate) {
        Candidate updated = candidateService.updateCandidate(id, updatedCandidate);
        return ResponseEntity.ok(updated);
    }

    // Διαγραφή υποψηφίου
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Integer id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    // Λίστα όλων των υποψηφίων
    @GetMapping
    public List<CandidateDTO> getCandidates() {
        return candidateService.getCandidateDTOs();
    }

    // Λίστα υποψηφίων για συγκεκριμένο Job Ad
    @GetMapping("/jobad/{jobAdId}")
    public List<CandidateDTO> getCandidatesByJobAd(@PathVariable Integer jobAdId) {
        return candidateService.getCandidateDTOsByJobAd(jobAdId);
    }

    // Αποθήκευση/ενημέρωση αξιολόγησης δεξιοτήτων για υποψήφιο
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

        // “Ασφαλές” DTO: το candidateId έρχεται από το path, όχι από το body
        SkillScoreUpsertRequestDTO safeDto = new SkillScoreUpsertRequestDTO(
                id,
                dto.questionId(),
                dto.skillId(),
                dto.score(),
                dto.comment()
        );

        SkillScoreResponseDTO saved = skillScoreService.upsert(safeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Λήψη CV υποψηφίου (fallback σε SampleCV αν λείπει)
    @GetMapping("/{id}/cv")
    public ResponseEntity<Resource> downloadCv(@PathVariable("id") int id) throws Exception {
        var cand = candidateService.getCandidate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate " + id + " not found"));

        String cvPath = cand.getCvPath();
        Resource res = resolveResource(cvPath);

        if (res == null || !res.exists() || !res.isReadable()) {
            res = new ClassPathResource("cv/SampleCV.pdf");
        }

        String original = cand.getCvOriginalName();
        String fileName;
        if (original != null && !original.isBlank()) {
            fileName = original;
        } else {
            String first = Optional.ofNullable(cand.getFirstName()).orElse("Candidate");
            String last  = Optional.ofNullable(cand.getLastName()).orElse(String.valueOf(id));
            fileName = sanitize(first + "_" + last) + "_CV.pdf";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(fileName, StandardCharsets.UTF_8)
                        .build()
        );

        return ResponseEntity.ok().headers(headers).body(res);
    }

    // Ενημέρωση status υποψηφίου
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateCandidateStatus(
            @PathVariable Integer id,
            @RequestBody CandidateStatusDTO dto) {
        if (dto == null || dto.getStatus() == null || dto.getStatus().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }
        candidateService.updateStatus(id, dto);
        return ResponseEntity.noContent().build();
    }

    // Δημιουργία νέου υποψηφίου
    @PostMapping
    public ResponseEntity<Candidate> createCandidate(
            @RequestParam("jobAdId") Integer jobAdId,
            @RequestBody Candidate body) {

        if (body == null || body.getFirstName() == null || body.getFirstName().isBlank()
                || body.getLastName() == null || body.getLastName().isBlank()
                || body.getEmail() == null || body.getEmail().isBlank()
                || jobAdId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "firstName, lastName, email και jobAdId είναι υποχρεωτικά");
        }

        Candidate saved = candidateService.createCandidateWithSkeleton(jobAdId, body);
        return ResponseEntity
                .created(URI.create("/api/v1/candidates/" + saved.getId()))
                .body(saved);
    }


    // Επιστρέφει τα τελικά σκορ υποψηφίων για συγκεκριμένο Job Ad (φθίνουσα)
    @GetMapping("/jobad/{jobAdId}/final-scores")
    public List<CandidateFinalScoreDTO> getFinalScoresForJobAd(@PathVariable Integer jobAdId) {
        return candidateService.getCandidateFinalScoresForJobAd(jobAdId);
    }

    // Αλλαγή status σε Hire υποψηφίου και επιστροφή ενημερωμένου status
    @PostMapping("/{id}/hire")
    public ResponseEntity<?> hireCandidate(@PathVariable Integer id) {
        try {
            CandidateAndJobAdStatusDTO dto = candidateService.hireCandidate(id);
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException e) {
            String msg = e.getMessage();
            if ("Only Approved candidates can be hired".equals(msg)) {
                return ResponseEntity.badRequest().body(msg);        // 400
            }
            if ("JobAd already complete".equals(msg)) {
                return ResponseEntity.status(409).body(msg);         // 409
            }
            return ResponseEntity.status(422).body(msg);             // 422 fallback
        }
    }

    // Upload CV (PDF) στο uploads/cv και επιστροφή path + originalName
    @PostMapping(value = "/upload-cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadCv(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file");
            }
            if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF allowed");
            }

            Path base = Paths.get("uploads", "cv").normalize();
            Files.createDirectories(base);

            String ts = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS").format(LocalDateTime.now());
            String original = file.getOriginalFilename();
            String safe = (original == null ? "cv.pdf" : original.replaceAll("[^A-Za-z0-9._-]", "_"));
            if (!safe.toLowerCase().endsWith(".pdf")) safe = safe + ".pdf";

            Path out = base.resolve(ts + "_" + safe).normalize();
            if (!out.startsWith(base)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad path");

            Files.copy(file.getInputStream(), out);

            String rel = base.resolve(out.getFileName()).toString().replace('\\', '/');

            return Map.of(
                    "path", rel,
                    "originalName", (original != null && !original.isBlank()) ? original : "cv.pdf"
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed");
        }
    }

    // Ενημερώνει μόνο το πεδίο "comments" ενός υποψηφίου
    @PatchMapping("/{id}/comments")
    public ResponseEntity<Void> updateCandidateComments(
            @PathVariable Integer id,
            @RequestBody CandidateCommentDTO dto) {

        if (dto == null || dto.getComments() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "comments are required");
        }
        candidateService.updateComments(id, dto.getComments());
        return ResponseEntity.noContent().build();
    }

    // Helper: επίλυση resource από classpath ή uploads/cv
    private Resource resolveResource(String path) {
        if (path == null || path.isBlank()) return null;
        try {
            if (path.startsWith("classpath:")) {
                return new ClassPathResource(path.substring("classpath:".length()));
            }
            Path base = Paths.get("uploads", "cv").normalize();
            Path p = Paths.get(path).normalize();
            if (p.isAbsolute()) return null;
            if (!p.startsWith("uploads")) {
                p = base.resolve(p).normalize();
            }
            if (!p.startsWith(base)) return null;
            if (Files.exists(p) && Files.isReadable(p)) {
                return new FileSystemResource(p);
            }
        } catch (Exception ignored) { }
        return null;
    }

    // Helper: καθαρισμός filename
    private String sanitize(String s) {
        return s == null ? "candidate" : s.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}

