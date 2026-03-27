package com.example.hiringProcess.Skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock SkillRepository skillRepository;

    @InjectMocks SkillService skillService;

    @Test
    void getSkills_returnsAllFromRepo() {
        List<Skill> skills = List.of(new Skill(), new Skill());
        when(skillRepository.findAll()).thenReturn(skills);

        List<Skill> result = skillService.getSkills();

        assertSame(skills, result);
        verify(skillRepository).findAll();
    }

    @Test
    void getSkill_returnsOptionalFromRepo() {
        Skill s = new Skill();
        when(skillRepository.findById(7)).thenReturn(Optional.of(s));

        Optional<Skill> result = skillService.getSkill(7);

        assertTrue(result.isPresent());
        assertSame(s, result.get());
        verify(skillRepository).findById(7);
    }

    @Test
    void addNewSkill_savesSkill() {
        Skill s = new Skill();

        skillService.addNewSkill(s);

        verify(skillRepository).save(s);
    }

    @Test
    void deleteSkill_whenNotExists_throws() {
        when(skillRepository.existsById(10)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> skillService.deleteSkill(10));
        verify(skillRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteSkill_whenExists_deletes() {
        when(skillRepository.existsById(10)).thenReturn(true);

        skillService.deleteSkill(10);

        verify(skillRepository).deleteById(10);
    }

    @Test
    void updateSkill_whenNotFound_throws() {
        when(skillRepository.findById(1)).thenReturn(Optional.empty());

        Skill updated = new Skill();
        updated.setTitle("x");

        assertThrows(IllegalStateException.class, () -> skillService.updateSkill(1, updated));
    }

    @Test
    void updateSkill_updatesOnlyNonNullFields_butAlwaysSetsScore() {
        // existing
        Skill existing = new Skill();
        existing.setTitle("old");
        existing.setScore(1.0);
        existing.setEscoId("oldEsco");

        when(skillRepository.findById(1)).thenReturn(Optional.of(existing));

        // updated
        Skill updated = new Skill();
        updated.setTitle("new");
        updated.setScore(7.5);
        updated.setEscoId(null); // δεν πρέπει να αλλάξει
        updated.setQuestions(null); // δεν πρέπει να αλλάξει

        skillService.updateSkill(1, updated);

        assertEquals("new", existing.getTitle());
        assertEquals(7.5, existing.getScore());
        assertEquals("oldEsco", existing.getEscoId());
    }

    @Test
    void updateSkill_updatesEscoIdAndQuestions_whenProvided() {
        Skill existing = new Skill();
        existing.setTitle("old");
        existing.setScore(1.0);
        existing.setEscoId("oldEsco");

        when(skillRepository.findById(1)).thenReturn(Optional.of(existing));

        Skill updated = new Skill();
        updated.setTitle(null);          // δεν αλλάζει
        updated.setScore(0.0);           // αλλάζει πάντα
        updated.setEscoId("newEsco");    // αλλάζει
        updated.setQuestions(Set.of());  // αλλάζει (full replace)

        skillService.updateSkill(1, updated);

        assertEquals("old", existing.getTitle());
        assertEquals(0.0, existing.getScore());
        assertEquals("newEsco", existing.getEscoId());
        assertNotNull(existing.getQuestions());
        assertTrue(existing.getQuestions().isEmpty());
    }
}
