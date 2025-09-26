package com.example.hiringProcess.Question;

import com.example.hiringProcess.Skill.Skill;
import com.example.hiringProcess.Skill.SkillRepository;
import com.example.hiringProcess.Step.Step;
import com.example.hiringProcess.Step.StepRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final StepRepository stepRepository;
    private final QuestionMapper questionMapper;
    private final SkillRepository skillRepository;

    public QuestionService(QuestionRepository questionRepository,
                           StepRepository stepRepository,
                           QuestionMapper questionMapper,
                           SkillRepository skillRepository) {
        this.questionRepository = questionRepository;
        this.stepRepository = stepRepository;
        this.questionMapper = questionMapper;
        this.skillRepository = skillRepository;
    }

    // ===== Legacy =====
    public List<Question> getQuestions() {
        return questionRepository.findAll();
    }

    public Optional<Question> getQuestion(Integer questionId) {
        return questionRepository.findById(questionId);
    }

    @Transactional
    public void addNewQuestion(Question question) {
        if (question.getId() != null && questionRepository.findById(question.getId()).isPresent()) {
            throw new IllegalStateException("ID already exists");
        }

        if (question.getStep() == null) {
            throw new IllegalArgumentException("Question must belong to a step");
        }

        // Step.id είναι primitive int
        final int stepId = question.getStep().getId();
        if (stepId <= 0) {
            throw new IllegalArgumentException("Question must belong to a persisted step");
        }

        long count = questionRepository.countByStep_Id(stepId);
        question.setPosition((int) count);
        questionRepository.save(question);
    }


    @Transactional
    public void deleteQuestion(Integer questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new IllegalStateException("Question with id " + questionId + " does not exist");
        }
        questionRepository.deleteById(questionId);
    }

    @Transactional
    public void updateQuestion(Integer questionId, Question updated) {
        Question existing = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalStateException("Question with id " + questionId + " does not exist"));

        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getStep() != null) existing.setStep(updated.getStep());

        if (updated.getSkills() != null) {
            existing.getSkills().clear();
            existing.getSkills().addAll(updated.getSkills());
        }
        if (updated.getQuestionScore() != null) {
            existing.getQuestionScore().clear();
            existing.getQuestionScore().addAll(updated.getQuestionScore());
        }
    }

    // ===== Νέα helpers για UI =====
    public List<QuestionLiteDTO> getQuestionsForStep(Integer stepId) {
        var list = questionRepository.findByStep_IdOrderByPositionAsc(stepId);
        return questionMapper.toLite(list);
    }

    @Transactional
    public Question createUnderStep(Integer stepId, String name, String description) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new EntityNotFoundException("Step " + stepId + " not found"));

        Question q = new Question();
        q.setStep(step);
        q.setTitle(name == null ? "" : name.trim());
        q.setDescription(description == null ? null : description.trim());

        long count = questionRepository.countByStep_Id(stepId);
        q.setPosition((int) count);

        return questionRepository.save(q);
    }

    public QuestionDetailsDTO getQuestionDetails(Integer questionId) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question " + questionId + " not found"));
        return questionMapper.toDetails(q);
    }

    @Transactional
    public void updateDescriptionAndSkills(Integer questionId, String description, List<String> skillNames) {
        var q = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question " + questionId + " not found"));

        q.setDescription(description == null ? "" : description.trim());

        if (skillNames == null) {
            q.getSkills().clear();
            return;
        }

        // Καθαρισμός input
        List<String> wanted = skillNames.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .distinct()
                .toList();

        // Υπάρχοντα skills με αυτά τα titles
        List<Skill> existing = new ArrayList<>(skillRepository.findByTitleIn(wanted));
        Set<String> existingTitles = new HashSet<>();
        for (Skill s : existing) {
            if (s != null && s.getTitle() != null) existingTitles.add(s.getTitle());
        }

        // Δημιούργησε όσα λείπουν
        for (String title : wanted) {
            if (!existingTitles.contains(title)) {
                Skill s = new Skill();
                s.setTitle(title);
                existing.add(skillRepository.save(s));
            }
        }

        q.getSkills().clear();
        q.getSkills().addAll(existing);
    }

    /** Reorder μέσα στο ίδιο step */
    @Transactional
    public void reorderInStep(Integer stepId, List<Integer> questionIdsInNewOrder) {
        if (questionIdsInNewOrder == null) return;

        List<Question> current = questionRepository.findByStep_IdOrderByPositionAsc(stepId);
        Map<Integer, Question> byId = new HashMap<>();
        for (Question q : current) byId.put(q.getId(), q);

        int pos = 0;
        for (Integer qid : questionIdsInNewOrder) {
            Question q = byId.get(qid);
            if (q != null) q.setPosition(pos++);
        }

        // Ό,τι δεν δόθηκε από client πάει στο τέλος
        for (Question q : current) {
            if (q.getPosition() == null) q.setPosition(pos++);
        }
    }

    /** Μετακίνηση σε άλλο step (με θέση) */
    @Transactional
    public void moveQuestion(Integer questionId, Integer toStepId, Integer toIndex) {
        if (toStepId == null) throw new IllegalArgumentException("toStepId is required");

        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question " + questionId + " not found"));

        Step targetStep = stepRepository.findById(toStepId)
                .orElseThrow(() -> new EntityNotFoundException("Step " + toStepId + " not found"));

        boolean stepChanged = (q.getStep() == null) || !Objects.equals(q.getStep().getId(), toStepId);
        if (stepChanged) {
            q.setStep(targetStep);
        }

        List<Question> target = new ArrayList<>(questionRepository.findByStep_IdOrderByPositionAsc(toStepId));

        // αφαίρεσε αν υπάρχει ήδη
        target.removeIf(it -> Objects.equals(it.getId(), q.getId()));
        target.add(q);

        int safeIndex = Math.max(0, Math.min(toIndex == null ? target.size() - 1 : toIndex, target.size() - 1));

        Question moved = target.remove(target.size() - 1);
        target.add(safeIndex, moved);

        for (int i = 0; i < target.size(); i++) {
            Question qi = target.get(i);
            qi.setPosition(i);
            qi.setStep(targetStep);
        }
    }
}
