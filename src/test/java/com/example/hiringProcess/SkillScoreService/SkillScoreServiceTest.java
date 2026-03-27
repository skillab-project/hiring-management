package com.example.hiringProcess.SkillScore;

import com.example.hiringProcess.Candidate.Candidate;
import com.example.hiringProcess.Candidate.CandidateRepository;
import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.Question.QuestionRepository;
import com.example.hiringProcess.Skill.Skill;
import com.example.hiringProcess.Skill.SkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillScoreServiceTest {

    @Mock SkillScoreRepository skillScoreRepository;
    @Mock CandidateRepository candidateRepository;
    @Mock QuestionRepository questionRepository;
    @Mock SkillRepository skillRepository;
    @Mock SkillScoreMapper mapper;

    @InjectMocks SkillScoreService skillScoreService;

    // -------------------------
    // listForCandidateQuestion
    // -------------------------

    @Test
    void listForCandidateQuestion_mapsEachEntityToDto() {
        SkillScore s1 = new SkillScore();
        SkillScore s2 = new SkillScore();
        when(skillScoreRepository.findByCandidateIdAndQuestionId(1, 2)).thenReturn(List.of(s1, s2));

        SkillScoreResponseDTO d1 = mock(SkillScoreResponseDTO.class);
        SkillScoreResponseDTO d2 = mock(SkillScoreResponseDTO.class);
        when(mapper.toResponseDTO(s1)).thenReturn(d1);
        when(mapper.toResponseDTO(s2)).thenReturn(d2);

        List<SkillScoreResponseDTO> out = skillScoreService.listForCandidateQuestion(1, 2);

        assertEquals(2, out.size());
        assertSame(d1, out.get(0));
        assertSame(d2, out.get(1));
        verify(mapper).toResponseDTO(s1);
        verify(mapper).toResponseDTO(s2);
    }

    // -------------------------
    // upsert - not found cases
    // -------------------------

    @Test
    void upsert_whenCandidateMissing_throws() {
        SkillScoreUpsertRequestDTO dto = new SkillScoreUpsertRequestDTO(1, 2, 3, 5, "ok");

        when(candidateRepository.findById(1)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> skillScoreService.upsert(dto));
        assertTrue(ex.getMessage().contains("Candidate not found"));
    }

    @Test
    void upsert_whenQuestionMissing_throws() {
        SkillScoreUpsertRequestDTO dto = new SkillScoreUpsertRequestDTO(1, 2, 3, 5, "ok");

        when(candidateRepository.findById(1)).thenReturn(Optional.of(new Candidate()));
        when(questionRepository.findById(2)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> skillScoreService.upsert(dto));
        assertTrue(ex.getMessage().contains("Question not found"));
    }

    @Test
    void upsert_whenSkillMissing_throws() {
        SkillScoreUpsertRequestDTO dto = new SkillScoreUpsertRequestDTO(1, 2, 3, 5, "ok");

        when(candidateRepository.findById(1)).thenReturn(Optional.of(new Candidate()));

        Question q = new Question();
        q.setSkills(new HashSet<>());
        when(questionRepository.findById(2)).thenReturn(Optional.of(q));

        when(skillRepository.findById(3)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> skillScoreService.upsert(dto));
        assertTrue(ex.getMessage().contains("Skill not found"));
    }

    // -------------------------
    // upsert - skill does not belong
    // -------------------------

    @Test
    void upsert_whenQuestionSkillsNull_throws() {
        SkillScoreUpsertRequestDTO dto = new SkillScoreUpsertRequestDTO(1, 2, 3, 5, "ok");

        when(candidateRepository.findById(1)).thenReturn(Optional.of(new Candidate()));

        Question q = new Question();
        q.setSkills(null); // <- important
        when(questionRepository.findById(2)).thenReturn(Optional.of(q));

        Skill skill = new Skill();
        skill.setId(3);
        when(skillRepository.findById(3)).thenReturn(Optional.of(skill));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> skillScoreService.upsert(dto));
        assertTrue(ex.getMessage().contains("does not belong"));
    }

    @Test
    void upsert_whenSkillNotInQuestionSkills_throws() {
        SkillScoreUpsertRequestDTO dto = new SkillScoreUpsertRequestDTO(1, 2, 3, 5, "ok");

        when(candidateRepository.findById(1)).thenReturn(Optional.of(new Candidate()));

        Question q = new Question();
        q.setSkills(new HashSet<>()); // empty
        when(questionRepository.findById(2)).thenReturn(Optional.of(q));

        Skill skill = new Skill();
        skill.setId(3);
        when(skillRepository.findById(3)).thenReturn(Optional.of(skill));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> skillScoreService.upsert(dto));
        assertTrue(ex.getMessage().contains("does not belong"));
    }

    // -------------------------
    // upsert - create new
    // -------------------------

    @Test
    void upsert_whenNoExisting_createsNewAndSavesAndMaps() {
        SkillScoreUpsertRequestDTO dto = new SkillScoreUpsertRequestDTO(1, 2, 3, 7, "good");

        Candidate cand = new Candidate();
        Question q = new Question();
        Skill skill = new Skill(); skill.setId(3);

        // question contains skill
        Set<Skill> skills = new HashSet<>();
        skills.add(skill);
        q.setSkills(skills);

        when(candidateRepository.findById(1)).thenReturn(Optional.of(cand));
        when(questionRepository.findById(2)).thenReturn(Optional.of(q));
        when(skillRepository.findById(3)).thenReturn(Optional.of(skill));

        when(skillScoreRepository.findByCandidateAndQuestionAndSkill(cand, q, skill))
                .thenReturn(Optional.empty());

        // save returns same entity (id stays null unless you set it)
        when(skillScoreRepository.save(any(SkillScore.class))).thenAnswer(inv -> inv.getArgument(0));

        SkillScoreResponseDTO mapped = mock(SkillScoreResponseDTO.class);
        when(mapper.toResponseDTO(any(SkillScore.class))).thenReturn(mapped);

        SkillScoreResponseDTO finalDto = mock(SkillScoreResponseDTO.class);
        when(mapper.withCreated(eq(mapped), anyBoolean())).thenReturn(finalDto);

        SkillScoreResponseDTO out = skillScoreService.upsert(dto);

        assertSame(finalDto, out);

        // capture saved entity and check updated values
        var captor = org.mockito.ArgumentCaptor.forClass(SkillScore.class);
        verify(skillScoreRepository).save(captor.capture());
        SkillScore saved = captor.getValue();

        assertSame(cand, saved.getCandidate());
        assertSame(q, saved.getQuestion());
        assertSame(skill, saved.getSkill());
        assertEquals(7, saved.getScore());
        assertEquals("good", saved.getComment());

        verify(mapper).toResponseDTO(any(SkillScore.class));
        verify(mapper).withCreated(eq(mapped), anyBoolean());
    }

    // -------------------------
    // upsert - update existing
    // -------------------------

    @Test
    void upsert_whenExisting_updatesAndSaves() {
        SkillScoreUpsertRequestDTO dto = new SkillScoreUpsertRequestDTO(1, 2, 3, 9, "updated");

        Candidate cand = new Candidate();
        Question q = new Question();
        Skill skill = new Skill(); skill.setId(3);

        q.setSkills(new HashSet<>(Set.of(skill)));

        when(candidateRepository.findById(1)).thenReturn(Optional.of(cand));
        when(questionRepository.findById(2)).thenReturn(Optional.of(q));
        when(skillRepository.findById(3)).thenReturn(Optional.of(skill));

        SkillScore existing = new SkillScore();
        existing.setCandidate(cand);
        existing.setQuestion(q);
        existing.setSkill(skill);
        existing.setScore(1);
        existing.setComment("old");

        when(skillScoreRepository.findByCandidateAndQuestionAndSkill(cand, q, skill))
                .thenReturn(Optional.of(existing));

        when(skillScoreRepository.save(existing)).thenReturn(existing);

        SkillScoreResponseDTO mapped = mock(SkillScoreResponseDTO.class);
        when(mapper.toResponseDTO(existing)).thenReturn(mapped);

        SkillScoreResponseDTO finalDto = mock(SkillScoreResponseDTO.class);
        when(mapper.withCreated(eq(mapped), anyBoolean())).thenReturn(finalDto);

        SkillScoreResponseDTO out = skillScoreService.upsert(dto);

        assertSame(finalDto, out);
        assertEquals(9, existing.getScore());
        assertEquals("updated", existing.getComment());

        verify(skillScoreRepository).save(existing);
        verify(mapper).withCreated(eq(mapped), anyBoolean());
    }

    // -------------------------
    // deleteById
    // -------------------------

    @Test
    void deleteById_whenExists_deletes() {
        when(skillScoreRepository.existsById(5L)).thenReturn(true);

        skillScoreService.deleteById(5L);

        verify(skillScoreRepository).deleteById(5L);
    }

    @Test
    void deleteById_whenNotExists_doesNothing() {
        when(skillScoreRepository.existsById(5L)).thenReturn(false);

        skillScoreService.deleteById(5L);

        verify(skillScoreRepository, never()).deleteById(anyLong());
    }

    // -------------------------
    // deleteTuple
    // -------------------------

    @Test
    void deleteTuple_callsRepositoryDelete() {
        skillScoreService.deleteTuple(1, 2, 3);

        verify(skillScoreRepository).deleteByCandidateIdAndQuestionIdAndSkillId(1, 2, 3);
    }

    // -------------------------
    // listAll / listForQuestion
    // -------------------------

    @Test
    void listAll_mapsEntities() {
        SkillScore s1 = new SkillScore();
        when(skillScoreRepository.findAll()).thenReturn(List.of(s1));

        SkillScoreResponseDTO dto = mock(SkillScoreResponseDTO.class);
        when(mapper.toResponseDTO(s1)).thenReturn(dto);

        List<SkillScoreResponseDTO> out = skillScoreService.listAll();

        assertEquals(1, out.size());
        assertSame(dto, out.get(0));
        verify(mapper).toResponseDTO(s1);
    }

    @Test
    void listForQuestion_mapsEntities() {
        SkillScore s1 = new SkillScore();
        SkillScore s2 = new SkillScore();
        when(skillScoreRepository.findByQuestionId(9)).thenReturn(List.of(s1, s2));

        SkillScoreResponseDTO d1 = mock(SkillScoreResponseDTO.class);
        SkillScoreResponseDTO d2 = mock(SkillScoreResponseDTO.class);
        when(mapper.toResponseDTO(s1)).thenReturn(d1);
        when(mapper.toResponseDTO(s2)).thenReturn(d2);

        List<SkillScoreResponseDTO> out = skillScoreService.listForQuestion(9);

        assertEquals(2, out.size());
        assertSame(d1, out.get(0));
        assertSame(d2, out.get(1));
        verify(mapper).toResponseDTO(s1);
        verify(mapper).toResponseDTO(s2);
    }
}
