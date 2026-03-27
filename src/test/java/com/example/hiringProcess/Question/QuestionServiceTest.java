package com.example.hiringProcess.Question;

import com.example.hiringProcess.QuestionScore.QuestionScore;
import com.example.hiringProcess.Skill.Skill;
import com.example.hiringProcess.Skill.SkillRepository;
import com.example.hiringProcess.Step.Step;
import com.example.hiringProcess.Step.StepRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock QuestionRepository questionRepository;
    @Mock StepRepository stepRepository;
    @Mock QuestionMapper questionMapper;
    @Mock SkillRepository skillRepository;

    @InjectMocks QuestionService questionService;

    // -------------------------
    // addNewQuestion
    // -------------------------

    @Test
    void addNewQuestion_whenIdAlreadyExists_throws() {
        Question q = new Question();
        q.setId(10);

        when(questionRepository.findById(10)).thenReturn(Optional.of(new Question()));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> questionService.addNewQuestion(q));

        assertTrue(ex.getMessage().toLowerCase().contains("id"));
        verify(questionRepository).findById(10);
        verify(questionRepository, never()).save(any());
    }

    @Test
    void addNewQuestion_whenStepNull_throws() {
        Question q = new Question();
        q.setId(null);
        q.setStep(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> questionService.addNewQuestion(q));

        assertTrue(ex.getMessage().toLowerCase().contains("step"));
        verify(questionRepository, never()).save(any());
    }

    @Test
    void addNewQuestion_whenStepNotPersisted_throws() {
        Step step = new Step();
        step.setId(0); // <=0 => not persisted (σύμφωνα με service)

        Question q = new Question();
        q.setStep(step);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> questionService.addNewQuestion(q));

        assertTrue(ex.getMessage().toLowerCase().contains("persisted"));
        verify(questionRepository, never()).save(any());
    }

    @Test
    void addNewQuestion_setsPositionBasedOnCountAndSaves() {
        Step step = new Step();
        step.setId(5);

        Question q = new Question();
        q.setId(null);
        q.setStep(step);

        when(questionRepository.countByStep_Id(5)).thenReturn(3L);

        questionService.addNewQuestion(q);

        assertEquals(3, q.getPosition());
        verify(questionRepository).save(q);
    }

    // -------------------------
    // deleteQuestion
    // -------------------------

    @Test
    void deleteQuestion_whenNotExists_throws() {
        when(questionRepository.existsById(9)).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> questionService.deleteQuestion(9));

        assertTrue(ex.getMessage().contains("9"));
        verify(questionRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteQuestion_whenExists_deletes() {
        when(questionRepository.existsById(9)).thenReturn(true);

        questionService.deleteQuestion(9);

        verify(questionRepository).deleteById(9);
    }

    // -------------------------
    // updateQuestion
    // -------------------------

    @Test
    void updateQuestion_whenNotFound_throws() {
        when(questionRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> questionService.updateQuestion(1, new Question()));

        verify(questionRepository).findById(1);
    }

    @Test
    void updateQuestion_updatesFieldsAndReplacesCollections() {
        // existing
        Question existing = new Question();
        existing.setId(1);
        existing.setTitle("old");
        existing.setDescription("old desc");

        Step oldStep = new Step();
        oldStep.setId(2);
        existing.setStep(oldStep);

        // ⚠️ ΣΙΓΟΥΡΑ mutable collections
        if (existing.getSkills() == null) existing.setSkills(new HashSet<>());
        if (existing.getQuestionScore() == null) existing.setQuestionScore(new ArrayList<>());

        // updated
        Question updated = new Question();
        updated.setTitle("new");
        updated.setDescription("new desc");

        Step newStep = new Step();
        newStep.setId(3);
        updated.setStep(newStep);

        Skill s1 = new Skill(); s1.setTitle("Java");
        Skill s2 = new Skill(); s2.setTitle("SQL");

        // ⚠️ ΣΙΓΟΥΡΑ non-null
        Set<Skill> newSkills = new HashSet<>();
        newSkills.add(s1);
        newSkills.add(s2);
        updated.setSkills(newSkills);

        when(questionRepository.findById(1)).thenReturn(Optional.of(existing));

        // Act
        questionService.updateQuestion(1, updated);

        // Assert
        assertEquals("new", existing.getTitle());
        assertEquals("new desc", existing.getDescription());
        assertSame(newStep, existing.getStep());

        assertNotNull(existing.getSkills());
        assertEquals(2, existing.getSkills().size());
        assertTrue(existing.getSkills().contains(s1));
        assertTrue(existing.getSkills().contains(s2));
    }


    // -------------------------
    // getQuestionsForStep
    // -------------------------

    @Test
    void getQuestionsForStep_usesMapper() {
        List<Question> list = List.of(new Question(), new Question());
        when(questionRepository.findByStep_IdOrderByPositionAsc(4)).thenReturn(list);

        List<QuestionLiteDTO> mapped = List.of(mock(QuestionLiteDTO.class));
        when(questionMapper.toLite(list)).thenReturn(mapped);

        List<QuestionLiteDTO> result = questionService.getQuestionsForStep(4);

        assertSame(mapped, result);
        verify(questionRepository).findByStep_IdOrderByPositionAsc(4);
        verify(questionMapper).toLite(list);
    }

    // -------------------------
    // createUnderStep
    // -------------------------

    @Test
    void createUnderStep_whenStepNotFound_throwsEntityNotFound() {
        when(stepRepository.findById(5)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> questionService.createUnderStep(5, "x", "y"));

        verify(questionRepository, never()).save(any());
    }

    @Test
    void createUnderStep_trimsAndSetsPositionAndSaves() {
        Step step = new Step();
        step.setId(5);

        when(stepRepository.findById(5)).thenReturn(Optional.of(step));
        when(questionRepository.countByStep_Id(5)).thenReturn(2L);
        when(questionRepository.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

        Question saved = questionService.createUnderStep(5, "  Name  ", "  Desc  ");

        assertSame(step, saved.getStep());
        assertEquals("Name", saved.getTitle());
        assertEquals("Desc", saved.getDescription());
        assertEquals(2, saved.getPosition());

        verify(questionRepository).save(any(Question.class));
    }

    // -------------------------
    // getQuestionDetails
    // -------------------------

    @Test
    void getQuestionDetails_whenNotFound_throwsEntityNotFound() {
        when(questionRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> questionService.getQuestionDetails(1));

        verify(questionMapper, never()).toDetails(any());
    }

    @Test
    void getQuestionDetails_mapsWithMapper() {
        Question q = new Question();
        q.setId(1);

        when(questionRepository.findById(1)).thenReturn(Optional.of(q));

        QuestionDetailsDTO dto = mock(QuestionDetailsDTO.class);
        when(questionMapper.toDetails(q)).thenReturn(dto);

        QuestionDetailsDTO result = questionService.getQuestionDetails(1);

        assertSame(dto, result);
        verify(questionMapper).toDetails(q);
    }

    // -------------------------
    // updateDescriptionAndSkills
    // -------------------------

    @Test
    void updateDescriptionAndSkills_whenSkillNamesNull_clearsSkillsAndReturns() {
        Question q = new Question();
        q.setId(1);
        q.setSkills(new HashSet<>());

        Skill existingSkill = new Skill();
        existingSkill.setTitle("Java");
        q.getSkills().add(existingSkill);

        when(questionRepository.findById(1)).thenReturn(Optional.of(q));

        questionService.updateDescriptionAndSkills(1, "  hello  ", null);

        assertEquals("hello", q.getDescription());
        assertTrue(q.getSkills().isEmpty());
        verify(skillRepository, never()).findByTitleIn(any());
        verify(skillRepository, never()).save(any());
    }

    @Test
    void updateDescriptionAndSkills_trimsDistinctsFindsExistingAndCreatesMissing() {
        Question q = new Question();
        q.setId(1);
        q.setSkills(new HashSet<>());

        when(questionRepository.findById(1)).thenReturn(Optional.of(q));

        // input contains blanks + duplicates + spaces
        List<String> skillNames = Arrays.asList(" Java ", "SQL", "  ", "Java", null, "Spring");

        // existing skills in DB: Java already exists
        Skill java = new Skill();
        java.setTitle("Java");

        when(skillRepository.findByTitleIn(List.of("Java", "SQL", "Spring"))).thenReturn(List.of(java));

        // new ones created: SQL and Spring
        when(skillRepository.save(any(Skill.class))).thenAnswer(inv -> inv.getArgument(0));

        questionService.updateDescriptionAndSkills(1, "  desc  ", skillNames);

        assertEquals("desc", q.getDescription());
        assertEquals(3, q.getSkills().size());

        Set<String> titles = new HashSet<>();
        for (Skill s : q.getSkills()) titles.add(s.getTitle());

        assertTrue(titles.contains("Java"));
        assertTrue(titles.contains("SQL"));
        assertTrue(titles.contains("Spring"));

        // save called for missing ones (2 φορές)
        verify(skillRepository, times(2)).save(any(Skill.class));
    }

    // -------------------------
    // reorderInStep
    // -------------------------

    @Test
    void reorderInStep_whenNullList_doesNothing() {
        questionService.reorderInStep(1, null);
        verifyNoInteractions(questionRepository);
    }

    @Test
    void reorderInStep_setsPositionsForGivenIds_andOthersGoToEnd() {
        Question q1 = new Question(); q1.setId(1); q1.setPosition(null);
        Question q2 = new Question(); q2.setId(2); q2.setPosition(null);
        Question q3 = new Question(); q3.setId(3); q3.setPosition(null);

        when(questionRepository.findByStep_IdOrderByPositionAsc(10)).thenReturn(List.of(q1, q2, q3));

        // new order gives only 2 and 1; 3 is missing => should go at end
        questionService.reorderInStep(10, List.of(2, 1));

        assertEquals(1, q1.getPosition()); // after q2
        assertEquals(0, q2.getPosition());
        assertEquals(2, q3.getPosition()); // goes to end
    }

    // -------------------------
    // moveQuestion
    // -------------------------

    @Test
    void moveQuestion_whenToStepIdNull_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> questionService.moveQuestion(1, null, 0));
    }

    @Test
    void moveQuestion_movesAndReordersPositions() {
        // Question to move
        Step fromStep = new Step(); fromStep.setId(1);
        Step toStep = new Step(); toStep.setId(2);

        Question moving = new Question();
        moving.setId(100);
        moving.setStep(fromStep);

        when(questionRepository.findById(100)).thenReturn(Optional.of(moving));
        when(stepRepository.findById(2)).thenReturn(Optional.of(toStep));

        // target step current list: qA, qB
        Question qA = new Question(); qA.setId(1); qA.setStep(toStep); qA.setPosition(0);
        Question qB = new Question(); qB.setId(2); qB.setStep(toStep); qB.setPosition(1);

        when(questionRepository.findByStep_IdOrderByPositionAsc(2)).thenReturn(List.of(qA, qB));

        // move to index 1 (middle)
        questionService.moveQuestion(100, 2, 1);

        // after move: expected order [qA, moving, qB] positions 0,1,2
        assertSame(toStep, moving.getStep());
        assertEquals(1, moving.getPosition());

        assertEquals(0, qA.getPosition());
        assertEquals(2, qB.getPosition());

        // also ensured each question in target has step=toStep
        assertSame(toStep, qA.getStep());
        assertSame(toStep, qB.getStep());
    }
}
