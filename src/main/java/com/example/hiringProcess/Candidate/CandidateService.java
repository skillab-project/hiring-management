package com.example.hiringProcess.Candidate;

import com.example.hiringProcess.InterviewReport.InterviewReport;
import com.example.hiringProcess.JobAd.JobAd;
import com.example.hiringProcess.JobAd.JobAdRepository;
import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.Question.QuestionRepository;
import com.example.hiringProcess.Skill.Skill;
import com.example.hiringProcess.SkillScore.SkillScore;
import com.example.hiringProcess.SkillScore.SkillScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;
    private final JobAdRepository jobAdRepository;
    private final QuestionRepository questionRepository;
    private final SkillScoreRepository skillScoreRepository;

    public CandidateService(CandidateRepository candidateRepository,
                            CandidateMapper candidateMapper,
                            JobAdRepository jobAdRepository,
                            QuestionRepository questionRepository,
                            SkillScoreRepository skillScoreRepository) {
        this.candidateRepository = candidateRepository;
        this.candidateMapper = candidateMapper;
        this.jobAdRepository = jobAdRepository;
        this.questionRepository = questionRepository;
        this.skillScoreRepository = skillScoreRepository;
    }

    // === READs ===
    public Optional<Candidate> getCandidate(Integer candidateId) {
        return candidateRepository.findById(candidateId);
    }

    @Transactional(readOnly = true)
    public List<CandidateDTO> getCandidateDTOs() {
        return candidateRepository.findAllListDtos(); // projection
    }

    @Transactional(readOnly = true)
    public List<CandidateDTO> getCandidateDTOsByJobAd(Integer jobAdId) {
        return candidateRepository.findListDtosByJobAdId(jobAdId); // projection
    }

    // === CREATE/UPDATE/DELETE ===
    public void addNewCandidate(Candidate candidate) {
        candidateRepository.save(candidate);
    }

    public void deleteCandidate(Integer candidateId) {
        boolean exists = candidateRepository.existsById(candidateId);
        if (!exists) throw new IllegalStateException("candidate with id " + candidateId + " does not exist");
        candidateRepository.deleteById(candidateId);
    }

    @Transactional
    public Candidate updateCandidate(Integer candidateId, Candidate updatedFields) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalStateException("Candidate with id " + candidateId + " does not exist"));

        if (updatedFields.getFirstName() != null && !updatedFields.getFirstName().isEmpty()
                && !Objects.equals(candidate.getFirstName(), updatedFields.getFirstName())) {
            candidate.setFirstName(updatedFields.getFirstName());
        }
        if (updatedFields.getLastName() != null && !updatedFields.getLastName().isEmpty()
                && !Objects.equals(candidate.getLastName(), updatedFields.getLastName())) {
            candidate.setLastName(updatedFields.getLastName());
        }
        if (updatedFields.getEmail() != null && !updatedFields.getEmail().isEmpty()
                && !Objects.equals(candidate.getEmail(), updatedFields.getEmail())) {
            candidate.setEmail(updatedFields.getEmail());
        }
        if (updatedFields.getCvPath() != null && !updatedFields.getCvPath().isEmpty()
                && !Objects.equals(candidate.getCvPath(), updatedFields.getCvPath())) {
            candidate.setCvPath(updatedFields.getCvPath());
        }
        if (updatedFields.getStatus() != null
                && !Objects.equals(candidate.getStatus(), updatedFields.getStatus())) {
            candidate.setStatus(updatedFields.getStatus());
        }
        if (updatedFields.getComments() != null && !updatedFields.getComments().isEmpty()
                && !Objects.equals(candidate.getComments(), updatedFields.getComments())) {
            candidate.setComments(updatedFields.getComments());
        }
        if (updatedFields.getCvOriginalName() != null && !updatedFields.getCvOriginalName().isEmpty()
                && !Objects.equals(candidate.getCvOriginalName(), updatedFields.getCvOriginalName())) {
            candidate.setCvOriginalName(updatedFields.getCvOriginalName());
        }
        return candidate; // managed
    }

    @Transactional
    public void updateComments(Integer candidateId, String comments) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalStateException("Candidate with id " + candidateId + " does not exist"));
        candidate.setComments(comments);
    }

    @Transactional
    public void updateStatus(Integer candidateId, CandidateStatusDTO dto) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalStateException("Candidate with id " + candidateId + " does not exist"));
        if (dto != null) candidateMapper.updateStatusFromDto(dto, candidate);
    }

    @Transactional
    public CandidateAndJobAdStatusDTO hireCandidate(Integer candidateId) {
        Candidate cand = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalStateException("Candidate not found"));

        JobAd job = cand.getJobAd();
        if (job == null) throw new IllegalStateException("Candidate not linked to a JobAd");

        if (!"Approved".equalsIgnoreCase(cand.getStatus())) {
            throw new IllegalStateException("Only Approved candidates can be hired");
        }

        cand.setStatus("Hired");
        candidateRepository.saveAndFlush(cand);

        job.setStatus("Complete");
        jobAdRepository.save(job);

        long hiredCount = candidateRepository.countByJobAd_IdAndStatusIgnoreCase(job.getId(), "Hired");

        return new CandidateAndJobAdStatusDTO(
                cand.getId(), cand.getStatus(), job.getId(), job.getStatus(), hiredCount
        );
    }

    @Transactional(readOnly = true)
    public List<CandidateFinalScoreDTO> getCandidateFinalScoresForJobAd(Integer jobAdId) {
        return candidateRepository.findFinalScoresByJobAd(jobAdId);
    }

    // === Δημιουργία candidate + skeleton SkillScores ===
    @Transactional
    public Candidate createCandidateWithSkeleton(Integer jobAdId, Candidate body) {
        JobAd jobAd = jobAdRepository.findById(jobAdId)
                .orElseThrow(() -> new IllegalStateException("JobAd not found"));

        if (body.getStatus() == null || body.getStatus().isBlank()) body.setStatus("Pending");
        if (body.getComments() == null) body.setComments("");

        body.setJobAd(jobAd);

        InterviewReport ir = new InterviewReport();
        ir.setInterview(jobAd.getInterview());
        body.setInterviewReport(ir);

        Candidate saved = candidateRepository.save(body);

        Integer interviewId = jobAd.getInterview().getId();
        List<Question> questions = questionRepository.findByStep_Interview_Id(interviewId);

        for (Question q : questions) {
            Set<Skill> skills = q.getSkills();
            if (skills == null) continue;
            for (Skill sk : skills) {
                SkillScore s = new SkillScore();
                s.setCandidate(saved);
                s.setQuestion(q);
                s.setSkill(sk);
                s.setScore(null);
                s.setComment("");
                skillScoreRepository.save(s);
            }
        }
        return saved;
    }
}
