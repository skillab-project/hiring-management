package com.example.hiringProcess.SkillScore;

import com.example.hiringProcess.Candidate.Candidate;
import com.example.hiringProcess.Candidate.CandidateRepository;
import com.example.hiringProcess.Candidate.CandidateService;
import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.Question.QuestionRepository;
import com.example.hiringProcess.Question.QuestionService;
import com.example.hiringProcess.Skill.Skill;
import com.example.hiringProcess.Skill.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkillScoreService {

    private final SkillScoreRepository skillScoreRepository;
    private final CandidateRepository candidateRepository;
    private final QuestionRepository questionRepository;
    private final SkillRepository skillRepository;
    private final SkillScoreMapper mapper;

    private final CandidateService candidateService;
    private final QuestionService questionService;

    public SkillScoreService(SkillScoreRepository skillScoreRepository,
                             CandidateRepository candidateRepository,
                             QuestionRepository questionRepository,
                             SkillRepository skillRepository,
                             SkillScoreMapper mapper,
                             CandidateService candidateService,
                             QuestionService questionService) {
        this.skillScoreRepository = skillScoreRepository;
        this.candidateRepository = candidateRepository;
        this.questionRepository = questionRepository;
        this.skillRepository = skillRepository;
        this.mapper = mapper;
        this.candidateService = candidateService;
        this.questionService = questionService;
    }

    /** Λίστα scores για συγκεκριμένο candidate+question (για το δεξί panel) */
    public List<SkillScoreResponseDTO> listForCandidateQuestion(int candidateId, int questionId, Integer orgId) {
        if(!this.candidateService.existsByOrg(candidateId, orgId)){
            return null;
        }

        return skillScoreRepository
                .findByCandidateIdAndQuestionId(candidateId, questionId)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /** Δημιουργεί ή ενημερώνει ένα SkillScore (idempotent) */
    @Transactional
    public SkillScoreResponseDTO upsert(SkillScoreUpsertRequestDTO dto, Integer orgId) {
        // Φέρνουμε managed entities (θα ρίξει εξαίρεση αν κάτι δεν υπάρχει)
        Candidate cand   = candidateRepository.findById(dto.candidateId())
                .orElseThrow(() -> new IllegalStateException("Candidate not found: " + dto.candidateId()));
        if(!this.candidateService.existsByOrg(cand.getId(), orgId)){
            return null;
        }

        Question question = questionRepository.findById(dto.questionId())
                .orElseThrow(() -> new IllegalStateException("Question not found: " + dto.questionId()));
        Skill skill       = skillRepository.findById(dto.skillId())
                .orElseThrow(() -> new IllegalStateException("Skill not found: " + dto.skillId()));

        // Προστασία: το skill πρέπει να ανήκει στη συγκεκριμένη question
        if (question.getSkills() == null || !question.getSkills().contains(skill)) {
            throw new IllegalStateException(
                    "Skill " + dto.skillId() + " does not belong to Question " + dto.questionId());
        }

        // Βρες υπάρχον ή φτιάξε νέο
        SkillScore score = skillScoreRepository
                .findByCandidateAndQuestionAndSkill(cand, question, skill)
                .orElseGet(() -> {
                    SkillScore s = new SkillScore();
                    s.setCandidate(cand);
                    s.setQuestion(question);
                    s.setSkill(skill);
                    // αρχικές τιμές
                    s.setScore(null);
                    s.setComment("");
                    return s;
                });

        // Ενημέρωση τιμών
        score.setScore(dto.score());
        score.setComment(dto.comment());

        SkillScore saved = skillScoreRepository.save(score);
        return mapper.withCreated(mapper.toResponseDTO(saved), score.getId() == null);
    }

    /** Διαγραφή με id */
    @Transactional
    public void deleteById(long id) {
        if (skillScoreRepository.existsById(id)) {
            skillScoreRepository.deleteById(id);
        }
    }

    /** Διαγραφή για συγκεκριμένη τριάδα (candidate, question, skill) */
    @Transactional
    public void deleteTuple(int candidateId, int questionId, int skillId, Integer orgId) {
        if(!this.candidateService.existsByOrg(candidateId, orgId)){
            return;
        }

        skillScoreRepository.deleteByCandidateIdAndQuestionIdAndSkillId(candidateId, questionId, skillId);
    }

    /** Όλα τα skill scores (admin/debug) */
    public List<SkillScoreResponseDTO> listAll(Integer orgId) {
        return skillScoreRepository.findAllByOrganisationId(orgId)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /** Όλα τα skill scores για συγκεκριμένη ερώτηση (όλων των υποψηφίων) */
    public List<SkillScoreResponseDTO> listForQuestion(int questionId, Integer orgId) {
        if(!this.questionService.existsByOrg(questionId, orgId)){
            return null;
        }

        return skillScoreRepository.findByQuestionId(questionId)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }
}
