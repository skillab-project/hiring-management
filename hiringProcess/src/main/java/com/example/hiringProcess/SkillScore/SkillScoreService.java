package com.example.hiringProcess.SkillScore;

import com.example.hiringProcess.Candidate.CandidateRepository;
import com.example.hiringProcess.Question.QuestionRepository;
import com.example.hiringProcess.Skill.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class SkillScoreService {

    private final SkillScoreRepository skillScoreRepository;
    private final CandidateRepository candidateRepository;
    private final QuestionRepository questionRepository;
    private final SkillRepository skillRepository;
    private final SkillScoreMapper mapper;

    public SkillScoreService(SkillScoreRepository skillScoreRepository,
                             CandidateRepository candidateRepository,
                             QuestionRepository questionRepository,
                             SkillRepository skillRepository,
                             SkillScoreMapper mapper) {
        this.skillScoreRepository = skillScoreRepository;
        this.candidateRepository = candidateRepository;
        this.questionRepository = questionRepository;
        this.skillRepository = skillRepository;
        this.mapper = mapper;
    }

    /* ========= LIST για συγκεκριμένο candidate+question ========= */
    public List<SkillScoreResponseDTO> listForCandidateQuestion(int candidateId, int questionId) {
        return skillScoreRepository
                .findByCandidateIdAndQuestionId(candidateId, questionId)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /* ========= UPSERT ========= */
    @Transactional
    public SkillScoreResponseDTO upsert(SkillScoreUpsertRequestDTO dto) {
        var existingOpt = skillScoreRepository
                .findByCandidateIdAndQuestionIdAndSkillId(dto.candidateId(), dto.questionId(), dto.skillId());

        if (existingOpt.isPresent()) {
            SkillScore existing = existingOpt.get();
            existing.setScore(dto.score());
            existing.setComment(dto.comment());
            existing.setRatedBy(dto.ratedBy());
            existing.setRatedAt(Instant.now());
            SkillScore saved = skillScoreRepository.save(existing);
            return mapper.withCreated(mapper.toResponseDTO(saved), false);
        }

        SkillScore entity = mapper.toNewEntity(dto);
        entity.setCandidate(candidateRepository.getReferenceById(dto.candidateId()));
        entity.setQuestion(questionRepository.getReferenceById(dto.questionId()));
        entity.setSkill(skillRepository.getReferenceById(dto.skillId()));
        SkillScore saved = skillScoreRepository.save(entity);
        return mapper.withCreated(mapper.toResponseDTO(saved), true);
    }

    /* ========= DELETE by id ========= */
    @Transactional
    public void deleteById(long id) {
        if (skillScoreRepository.existsById(id)) {
            skillScoreRepository.deleteById(id);
        }
    }

    /* ========= DELETE tuple (candidate, question, skill) ========= */
    @Transactional
    public void deleteTuple(int candidateId, int questionId, int skillId) {
        skillScoreRepository.deleteByCandidateIdAndQuestionIdAndSkillId(candidateId, questionId, skillId);
    }
}
