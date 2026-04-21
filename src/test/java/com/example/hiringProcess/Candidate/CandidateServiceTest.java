package com.example.hiringProcess.Candidate;

import com.example.hiringProcess.Interview.Interview;
import com.example.hiringProcess.JobAd.JobAd;
import com.example.hiringProcess.JobAd.JobAdRepository;
import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.Question.QuestionRepository;
import com.example.hiringProcess.Skill.Skill;
import com.example.hiringProcess.SkillScore.SkillScore;
import com.example.hiringProcess.SkillScore.SkillScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private CandidateMapper candidateMapper;

    @Mock
    private JobAdRepository jobAdRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private SkillScoreRepository skillScoreRepository;

    @InjectMocks
    private CandidateService candidateService;

    // === CREATE ===
    @Test
    void addNewCandidate_shouldCallSave() {
        Candidate candidate = new Candidate();

        candidateService.addNewCandidate(candidate);

        verify(candidateRepository).save(candidate);
    }

    // === DELETE ===
    @Test
    void deleteCandidate_shouldCallDeleteWhenExists() {
        when(candidateRepository.existsById(1)).thenReturn(true);

        candidateService.deleteCandidate(1);

        verify(candidateRepository).deleteById(1);
    }

    @Test
    void deleteCandidate_shouldThrowWhenNotExists() {
        when(candidateRepository.existsById(1)).thenReturn(false);

        assertThrows(IllegalStateException.class,
                () -> candidateService.deleteCandidate(1));
    }

    // === UPDATE ===
    @Test
    void updateCandidate_shouldUpdateChangedFields() {
        Candidate existing = new Candidate();
        existing.setFirstName("John");
        existing.setLastName("Doe");
        existing.setEmail("old@email.com");

        Candidate updates = new Candidate();
        updates.setFirstName("Mike"); // αλλαγή
        updates.setLastName("Doe");   // ίδια
        updates.setEmail("new@email.com"); // αλλαγή

        when(candidateRepository.findById(1))
                .thenReturn(Optional.of(existing));

        Candidate result = candidateService.updateCandidate(1, updates);

        assertEquals("Mike", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("new@email.com", result.getEmail());
    }

    @Test
    void updateCandidate_shouldThrowIfNotFound() {
        when(candidateRepository.findById(1)).thenReturn(Optional.empty());
        Candidate updates = new Candidate();

        assertThrows(IllegalStateException.class,
                () -> candidateService.updateCandidate(1, updates));
    }

    // === UPDATE COMMENTS ===
    @Test
    void updateComments_shouldChangeComments() {
        Candidate candidate = new Candidate();
        when(candidateRepository.findById(1)).thenReturn(Optional.of(candidate));

        candidateService.updateComments(1, "New comment");

        assertEquals("New comment", candidate.getComments());
    }

    @Test
    void updateComments_shouldThrowIfCandidateNotFound() {
        when(candidateRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> candidateService.updateComments(1, "New comment"));
    }

    // === UPDATE STATUS ===
    @Test
    void updateStatus_shouldCallMapper() {
        Candidate candidate = new Candidate();
        CandidateStatusDTO dto = new CandidateStatusDTO();
        when(candidateRepository.findById(1)).thenReturn(Optional.of(candidate));

        candidateService.updateStatus(1, dto);

        verify(candidateMapper).updateStatusFromDto(dto, candidate);
    }

    @Test
    void updateStatus_shouldThrowIfCandidateNotFound() {
        when(candidateRepository.findById(1)).thenReturn(Optional.empty());
        CandidateStatusDTO dto = new CandidateStatusDTO();

        assertThrows(IllegalStateException.class,
                () -> candidateService.updateStatus(1, dto));
    }

//    // === HIRE CANDIDATE ===
//    @Test
//    void hireCandidate_shouldWorkForApprovedCandidate() {
//        JobAd jobAd = new JobAd();
//        jobAd.setId(10);
//        jobAd.setStatus("Open");
//
//        Candidate candidate = new Candidate();
//        candidate.setId(1);
//        candidate.setStatus("Approved");
//        candidate.setJobAd(jobAd);
//
//        when(candidateRepository.findById(1)).thenReturn(Optional.of(candidate));
//        when(candidateRepository.countByJobAd_IdAndStatusIgnoreCase(10, "Hired")).thenReturn(1L);
//
//        var result = candidateService.hireCandidate(1, 1);
//
//        assertEquals("Hired", candidate.getStatus());
//        assertEquals("Complete", jobAd.getStatus());
//        assertEquals(1L, result.getHiredCount());
//
//        verify(candidateRepository).saveAndFlush(candidate);
//        verify(jobAdRepository).save(jobAd);
//    }
//
//    @Test
//    void hireCandidate_shouldThrowIfNotApproved() {
//        Candidate candidate = new Candidate();
//        candidate.setStatus("Pending");
//        candidate.setJobAd(new JobAd());
//        when(candidateRepository.findById(1)).thenReturn(Optional.of(candidate));
//
//        assertThrows(IllegalStateException.class,
//                () -> candidateService.hireCandidate(1, 1));
//    }

    @Test
    void hireCandidate_shouldThrowIfCandidateNotFound() {
        when(candidateRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> candidateService.hireCandidate(1, 1));
    }

    @Test
    void hireCandidate_shouldThrowIfJobAdNull() {
        Candidate candidate = new Candidate();
        candidate.setStatus("Approved");
        candidate.setJobAd(null);

        when(candidateRepository.findById(1)).thenReturn(Optional.of(candidate));

        assertThrows(IllegalStateException.class,
                () -> candidateService.hireCandidate(1, 1));
    }

//    // === CREATE CANDIDATE WITH SKELETON ===
//    @Test
//    void createCandidateWithSkeleton_shouldSaveCandidateAndSkillScores() {
//        // Δημιουργούμε JobAd και Interview
//        JobAd jobAd = new JobAd();
//        jobAd.setId(10);
//
//        Interview interview = new Interview();   // πραγματικό Interview αντικείμενο
//        jobAd.setInterview(interview);
//
//        // Δημιουργούμε ένα Candidate
//        Candidate candidate = new Candidate();
//
//        // Mock repository κλήσεις
//        when(jobAdRepository.findById(10)).thenReturn(Optional.of(jobAd));
//        when(candidateRepository.save(candidate)).thenReturn(candidate);
//
//        // Δημιουργούμε μία ερώτηση με ένα skill για να τεστάρουμε το loop
//        Question question = new Question();
//        Skill skill = new Skill();
//        Set<Skill> skills = new HashSet<>();
//        skills.add(skill);
//        question.setSkills(skills);
//
//        when(questionRepository.findByStep_Interview_Id(anyInt()))
//                .thenReturn(Collections.singletonList(question));
//
//        // Εκτέλεση μεθόδου
//        Candidate saved = candidateService.createCandidateWithSkeleton(10, candidate, 1);
//
//        // Assertions
//        assertNotNull(saved);
//        assertEquals(jobAd, saved.getJobAd());
//        assertNotNull(saved.getInterviewReport());
//
//        // Verify save calls
//        verify(candidateRepository).save(candidate);
//        verify(skillScoreRepository, times(1)).save(any(SkillScore.class));
//    }
//

    @Test
    void createCandidateWithSkeleton_shouldThrowIfJobAdNotFound() {
        Candidate candidate = new Candidate();
        when(jobAdRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> candidateService.createCandidateWithSkeleton(10, candidate, 1));
    }
}
