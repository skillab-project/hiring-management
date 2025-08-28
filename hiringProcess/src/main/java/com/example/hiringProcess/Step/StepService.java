package com.example.hiringProcess.Step;

import com.example.hiringProcess.Interview.Interview;
import com.example.hiringProcess.Interview.InterviewRepository;
import com.example.hiringProcess.Question.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StepService {

    private final StepRepository stepRepository;
    private final QuestionRepository questionRepository;
    private final InterviewRepository interviewRepository;
    private final StepMapper stepMapper;

    /* =========================================================
     * Read (DTO) – Steps ανά Interview, ταξινομημένα
     * ========================================================= */
    public List<StepResponseDTO> getStepsByInterviewSorted(int interviewId) {
        return stepRepository.findByInterviewIdOrderByPositionAsc(interviewId)
                .stream()
                .map(stepMapper::toResponseDTO)
                .toList();
    }

    /* =========================================================
     * Create – στο τέλος της λίστας (επιστρέφει DTO)
     * ========================================================= */
    @Transactional
    public StepResponseDTO createAtEnd(int interviewId, String title, String description) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new IllegalStateException("Interview " + interviewId + " not found"));

        int max = stepRepository.findMaxPositionByInterviewId(interviewId);

        Step s = new Step();
        s.setTitle(title);
        s.setDescription(description == null ? "" : description);
        s.setInterview(interview);
        s.setPosition(max + 1);   // στο τέλος
        s.setScore(0);            // default

        Step saved = stepRepository.save(s);
        return stepMapper.toResponseDTO(saved);
    }

    /** Convenience για δημιουργία με κενή περιγραφή. */
    @Transactional
    public StepResponseDTO createStep(int interviewId, String title) {
        return createAtEnd(interviewId, title, "");
    }

    /* =========================================================
     * Delete – και «συμπίεση» (reindex) των υπολοίπων
     * ========================================================= */
    @Transactional
    public void deleteStep(Integer stepId) {
        Step s = stepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalStateException("Step " + stepId + " not found"));

        int interviewId = s.getInterview().getId();
        int deletedPos = s.getPosition();

        // Διαγραφή (με orphanRemoval για ερωτήσεις)
        stepRepository.delete(s);

        // Reindex 0..N-1
        List<Step> rest = stepRepository.findByInterviewIdOrderByPositionAsc(interviewId);
        for (Step st : rest) {
            if (st.getPosition() > deletedPos) {
                st.setPosition(st.getPosition() - 1);
            }
        }
        stepRepository.saveAll(rest);
    }

    /* =========================================================
     * Update – τίτλος/περιγραφή/score/μεταφορά σε άλλο interview
     * ========================================================= */
    @Transactional
    public void updateStep(Integer stepId, StepUpdateDTO dto) {
        Step existing = stepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalStateException("Step with id " + stepId + " does not exist"));

        if (dto.getTitle() != null)       existing.setTitle(dto.getTitle());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getScore() != null)       existing.setScore(dto.getScore());

        // Μεταφορά σε άλλο interview → τοποθέτηση στο τέλος εκείνης της λίστας
        if (dto.getInterviewId() != null) {
            int newInterviewId = dto.getInterviewId();
            int currentInterviewId = existing.getInterview() != null ? existing.getInterview().getId() : -1;
            if (newInterviewId != currentInterviewId) {
                Interview newInterview = interviewRepository.findById(newInterviewId)
                        .orElseThrow(() -> new IllegalStateException("Interview " + newInterviewId + " not found"));
                existing.setInterview(newInterview);
                int max = stepRepository.findMaxPositionByInterviewId(newInterviewId);
                existing.setPosition(max + 1);
            }
        }

        stepRepository.save(existing);
    }

    /* =========================================================
     * Skills του step (από τις ερωτήσεις του)
     * ========================================================= */
    public List<StepSkillDTO> getSkillsForStep(Integer stepId) {
        // Validate ότι υπάρχει το step
        stepRepository.findById(stepId).orElseThrow(() ->
                new EntityNotFoundException("Step " + stepId + " not found"));

        return questionRepository.findDistinctSkillsByStepId(stepId)
                .stream()
                .map(s -> new StepSkillDTO(stepId, s.getId(), s.getTitle()))
                .toList();
    }

    /* =========================================================
     * Μετακίνηση ενός step (up/down) μέσα στο ίδιο interview
     * ========================================================= */
    @Transactional
    public void move(Integer stepId, String direction) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalStateException("Step " + stepId + " not found"));

        int interviewId = step.getInterview().getId();
        int from = step.getPosition();
        int to = "up".equalsIgnoreCase(direction) ? from - 1 : from + 1;
        if (to < 0) return;

        Optional<Step> otherOpt = stepRepository.findByInterviewIdAndPosition(interviewId, to);
        if (otherOpt.isEmpty()) return;

        Step other = otherOpt.get();
        if (Objects.equals(other.getId(), step.getId())) return;

        step.setPosition(to);
        other.setPosition(from);
        stepRepository.save(step);
        stepRepository.save(other);
    }

    /* =========================================================
     * Batch reorder (0..N-1) με 2 φάσεις για αποφυγή unique conflicts
     * ========================================================= */
    @Transactional
    public void reorder(int interviewId, List<Integer> orderedIds) {
        if (orderedIds == null || orderedIds.isEmpty()) {
            throw new IllegalStateException("stepIds is empty");
        }

        List<Step> steps = stepRepository.findByInterviewIdOrderByPositionAsc(interviewId);
        if (steps.isEmpty()) {
            throw new IllegalStateException("no steps for interview " + interviewId);
        }
        if (steps.size() != orderedIds.size()) {
            throw new IllegalStateException("mismatch size: db=" + steps.size() + " req=" + orderedIds.size());
        }

        Set<Integer> current = steps.stream().map(Step::getId).collect(Collectors.toSet());
        Set<Integer> incoming = new HashSet<>(orderedIds);
        if (!current.equals(incoming)) {
            throw new IllegalStateException("ids don't match interview steps: db=" + current + " req=" + incoming);
        }

        Map<Integer, Step> byId = steps.stream()
                .collect(Collectors.toMap(Step::getId, Function.identity()));

        // 1η φάση: προσωρινές θέσεις (αρνητικές) για να αποφύγουμε μοναδικούς περιορισμούς
        int tempPos = -orderedIds.size();
        for (Step s : steps) {
            s.setPosition(tempPos++);
        }
        stepRepository.saveAll(steps);
        stepRepository.flush();

        // 2η φάση: κανονικές θέσεις 0..N-1
        for (int i = 0; i < orderedIds.size(); i++) {
            Step s = byId.get(orderedIds.get(i));
            s.setPosition(i);
        }
        stepRepository.saveAll(steps);
    }

    /* =========================================================
     * Legacy helpers (για συμβατότητα με υπάρχον front όπου χρειάζεται)
     * ========================================================= */
    public List<Step> getSteps() {
        return stepRepository.findAll();
    }

    public Optional<Step> getStep(Integer stepId) {
        return stepRepository.findById(stepId);
    }

    @Transactional
    public void addNewStep(Step step) {
        if (step.getInterview() != null && step.getInterview().getId() != 0) {
            int interviewId = step.getInterview().getId();
            int max = stepRepository.findMaxPositionByInterviewId(interviewId);
            step.setPosition(max + 1);
        }
        step.setScore(0); // default
        stepRepository.save(step);
    }
}
