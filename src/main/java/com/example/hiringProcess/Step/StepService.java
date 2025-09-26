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

    /* ================= READ: Steps ανά Interview (sorted) ================= */
    public List<StepResponseDTO> getStepsByInterviewSorted(int interviewId) {
        return stepRepository.findByInterviewIdOrderByPositionAsc(interviewId)
                .stream()
                .map(stepMapper::toResponseDTO)   // περιλαμβάνει id, title, description
                .toList();
    }

    /* ================= CREATE: στο τέλος της λίστας ================= */
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

        return stepMapper.toResponseDTO(stepRepository.save(s));
    }

    /** Convenience για δημιουργία με κενή περιγραφή. */
    @Transactional
    public StepResponseDTO createStep(int interviewId, String title) {
        return createAtEnd(interviewId, title, "");
    }

    /* ================= DELETE: διαγραφή & reindex ================= */
    @Transactional
    public void deleteStep(Integer stepId) {
        Step s = stepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalStateException("Step " + stepId + " not found"));

        int interviewId = s.getInterview().getId();
        int deletedPos  = s.getPosition();

        stepRepository.delete(s);

        // Reindex 0..N-1 για τα υπόλοιπα
        List<Step> rest = stepRepository.findByInterviewIdOrderByPositionAsc(interviewId);
        for (Step st : rest) {
            if (st.getPosition() > deletedPos) {
                st.setPosition(st.getPosition() - 1);
            }
        }
        stepRepository.saveAll(rest);
    }

    /* ================= UPDATE: title/description/score/μεταφορά ================= */
    @Transactional
    public void updateStep(int stepId, StepUpdateDTO dto) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalStateException("Step with id " + stepId + " does not exist"));

        // ΜΟΝΟ description
        if (dto.description() != null && !dto.description().isBlank()) {
            step.setDescription(dto.description());
        }
        stepRepository.save(step);
    }


    /* ================= Skills του step (από τις ερωτήσεις του) ================= */
    public List<StepSkillDTO> getSkillsForStep(Integer stepId) {
        stepRepository.findById(stepId).orElseThrow(() ->
                new EntityNotFoundException("Step " + stepId + " not found"));

        return questionRepository.findDistinctSkillsByStepId(stepId)
                .stream()
                .map(s -> new StepSkillDTO(stepId, s.getId(), s.getTitle()))
                .toList();
    }

    /* ================= Move (up/down) μέσα στο ίδιο interview ================= */
    @Transactional
    public void move(Integer stepId, String direction) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new IllegalStateException("Step " + stepId + " not found"));

        int interviewId = step.getInterview().getId();
        int from = step.getPosition();
        int to   = "up".equalsIgnoreCase(direction) ? from - 1 : from + 1;
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

    /* ================= Reorder (batch) με 2 φάσεις ================= */
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

        Set<Integer> current  = steps.stream().map(Step::getId).collect(Collectors.toSet());
        Set<Integer> incoming = new HashSet<>(orderedIds);
        if (!current.equals(incoming)) {
            throw new IllegalStateException("ids don't match interview steps: db=" + current + " req=" + incoming);
        }

        Map<Integer, Step> byId = steps.stream()
                .collect(Collectors.toMap(Step::getId, Function.identity()));

        // 1η φάση: προσωρινές αρνητικές θέσεις
        int tempPos = -orderedIds.size();
        for (Step s : steps) s.setPosition(tempPos++);
        stepRepository.saveAll(steps);
        stepRepository.flush();

        // 2η φάση: κανονικές θέσεις 0..N-1
        for (int i = 0; i < orderedIds.size(); i++) {
            Step s = byId.get(orderedIds.get(i));
            s.setPosition(i);
        }
        stepRepository.saveAll(steps);
    }

    /* ================= Legacy helpers (αν χρειάζονται αλλού) ================= */
    public List<Step> getSteps() { return stepRepository.findAll(); }

    public Optional<Step> getStep(Integer stepId) { return stepRepository.findById(stepId); }

    @Transactional
    public void addNewStep(Step step) {
        if (step.getInterview() != null && step.getInterview().getId() != 0) {
            int interviewId = step.getInterview().getId();
            int max = stepRepository.findMaxPositionByInterviewId(interviewId);
            step.setPosition(max + 1);
        }
        step.setScore(0);
        stepRepository.save(step);
    }
}
