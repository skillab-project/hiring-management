package com.example.hiringProcess.Step;

import com.example.hiringProcess.Interview.Interview;
import com.example.hiringProcess.Interview.InterviewRepository;
import com.example.hiringProcess.Question.QuestionRepository;
import com.example.hiringProcess.Skill.Skill;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StepServiceTest {

    @Mock StepRepository stepRepository;
    @Mock QuestionRepository questionRepository;
    @Mock InterviewRepository interviewRepository;
    @Mock StepMapper stepMapper;

    @InjectMocks StepService stepService;

    // -------------------------
    // getStepsByInterviewSorted
    // -------------------------
    @Test
    void getStepsByInterviewSorted_mapsAndReturnsDtos() {
        Step s1 = new Step(); s1.setId(1);
        Step s2 = new Step(); s2.setId(2);
        when(stepRepository.findByInterviewIdOrderByPositionAsc(9)).thenReturn(List.of(s1, s2));

        StepResponseDTO d1 = mock(StepResponseDTO.class);
        StepResponseDTO d2 = mock(StepResponseDTO.class);
        when(stepMapper.toResponseDTO(s1)).thenReturn(d1);
        when(stepMapper.toResponseDTO(s2)).thenReturn(d2);

        List<StepResponseDTO> out = stepService.getStepsByInterviewSorted(9);

        assertEquals(List.of(d1, d2), out);
        verify(stepRepository).findByInterviewIdOrderByPositionAsc(9);
        verify(stepMapper).toResponseDTO(s1);
        verify(stepMapper).toResponseDTO(s2);
    }

    // -------------------------
    // createAtEnd / createStep
    // -------------------------
    @Test
    void createAtEnd_whenInterviewMissing_throws() {
        when(interviewRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> stepService.createAtEnd(7, "t", "d"));
    }

    @Test
    void createAtEnd_setsFieldsAndSaves_atMaxPlusOne() {
        Interview interview = new Interview();
        interview.setId(7);
        when(interviewRepository.findById(7)).thenReturn(Optional.of(interview));

        when(stepRepository.findMaxPositionByInterviewId(7)).thenReturn(3);

        // capture the Step passed to save()
        ArgumentCaptor<Step> captor = ArgumentCaptor.forClass(Step.class);

        Step saved = new Step();
        saved.setId(99);
        when(stepRepository.save(any(Step.class))).thenReturn(saved);

        StepResponseDTO dto = mock(StepResponseDTO.class);
        when(stepMapper.toResponseDTO(saved)).thenReturn(dto);

        StepResponseDTO out = stepService.createAtEnd(7, "Title", null);

        assertSame(dto, out);

        verify(stepRepository).save(captor.capture());
        Step toSave = captor.getValue();

        assertEquals("Title", toSave.getTitle());
        assertEquals("", toSave.getDescription()); // null -> ""
        assertSame(interview, toSave.getInterview());
        assertEquals(4, toSave.getPosition());     // max 3 -> 4
        assertEquals(0, toSave.getScore());

        verify(stepMapper).toResponseDTO(saved);
    }

    @Test
    void createStep_callsCreateAtEndWithEmptyDescription() {
        Interview interview = new Interview();
        interview.setId(1);
        when(interviewRepository.findById(1)).thenReturn(Optional.of(interview));
        when(stepRepository.findMaxPositionByInterviewId(1)).thenReturn(0);

        Step saved = new Step();
        when(stepRepository.save(any(Step.class))).thenReturn(saved);

        StepResponseDTO dto = mock(StepResponseDTO.class);
        when(stepMapper.toResponseDTO(saved)).thenReturn(dto);

        StepResponseDTO out = stepService.createStep(1, "A");

        assertSame(dto, out);

        ArgumentCaptor<Step> captor = ArgumentCaptor.forClass(Step.class);
        verify(stepRepository).save(captor.capture());
        assertEquals("", captor.getValue().getDescription());
    }

    // -------------------------
    // deleteStep (reindex)
    // -------------------------
    @Test
    void deleteStep_whenStepMissing_throws() {
        when(stepRepository.findById(5)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> stepService.deleteStep(5));
    }

    @Test
    void deleteStep_deletesAndReindexesPositionsAboveDeleted() {
        Interview interview = new Interview(); interview.setId(10);

        Step deleted = new Step();
        deleted.setId(1);
        deleted.setInterview(interview);
        deleted.setPosition(1);

        when(stepRepository.findById(1)).thenReturn(Optional.of(deleted));

        Step s0 = new Step(); s0.setId(2); s0.setInterview(interview); s0.setPosition(0);
        Step s2 = new Step(); s2.setId(3); s2.setInterview(interview); s2.setPosition(2);
        Step s3 = new Step(); s3.setId(4); s3.setInterview(interview); s3.setPosition(3);

        when(stepRepository.findByInterviewIdOrderByPositionAsc(10)).thenReturn(List.of(s0, s2, s3));

        stepService.deleteStep(1);

        verify(stepRepository).delete(deleted);

        // positions > deletedPos(1) decrement by 1
        assertEquals(0, s0.getPosition());
        assertEquals(1, s2.getPosition());
        assertEquals(2, s3.getPosition());

        verify(stepRepository).saveAll(List.of(s0, s2, s3));
    }

    // -------------------------
    // updateStep
    // -------------------------
    @Test
    void updateStep_whenMissing_throws() {
        when(stepRepository.findById(1)).thenReturn(Optional.empty());
        StepUpdateDTO dto = mock(StepUpdateDTO.class);
        assertThrows(IllegalStateException.class, () -> stepService.updateStep(1, dto));
    }

    @Test
    void updateStep_updatesDescriptionOnlyWhenNotBlank() {
        Step step = new Step();
        step.setId(1);
        step.setDescription("old");
        when(stepRepository.findById(1)).thenReturn(Optional.of(step));

        StepUpdateDTO dto = mock(StepUpdateDTO.class);
        when(dto.description()).thenReturn("  new  ");

        stepService.updateStep(1, dto);

        assertEquals("  new  ", step.getDescription());
        verify(stepRepository).save(step);
    }

    @Test
    void updateStep_blankDescription_doesNothingButStillSaves() {
        Step step = new Step();
        step.setId(1);
        step.setDescription("old");
        when(stepRepository.findById(1)).thenReturn(Optional.of(step));

        StepUpdateDTO dto = mock(StepUpdateDTO.class);
        when(dto.description()).thenReturn("   "); // blank

        stepService.updateStep(1, dto);

        assertEquals("old", step.getDescription());
        verify(stepRepository).save(step);
    }

    // -------------------------
    // getSkillsForStep
    // -------------------------
    @Test
    void getSkillsForStep_whenStepMissing_throwsEntityNotFound() {
        when(stepRepository.findById(5)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> stepService.getSkillsForStep(5));
    }

    @Test
    void getSkillsForStep_mapsSkillsToDTOs() {
        when(stepRepository.findById(5)).thenReturn(Optional.of(new Step()));

        Skill s1 = new Skill(); s1.setId(1); s1.setTitle("Java");
        Skill s2 = new Skill(); s2.setId(2); s2.setTitle("SQL");

        when(questionRepository.findDistinctSkillsByStepId(5)).thenReturn(List.of(s1, s2));

        List<StepSkillDTO> out = stepService.getSkillsForStep(5);

        assertEquals(2, out.size());
        assertEquals(5, out.get(0).stepId());
        assertEquals(1, out.get(0).skillId());
        assertEquals("Java", out.get(0).skillName());

        assertEquals(5, out.get(1).stepId());
        assertEquals(2, out.get(1).skillId());
        assertEquals("SQL", out.get(1).skillName());
    }

    // -------------------------
    // move (up/down)
    // -------------------------
    @Test
    void move_whenStepMissing_throws() {
        when(stepRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> stepService.move(1, "up"));
    }

    @Test
    void move_whenToIsNegative_returnsWithoutSaving() {
        Interview interview = new Interview(); interview.setId(10);
        Step step = new Step(); step.setId(1); step.setInterview(interview); step.setPosition(0);
        when(stepRepository.findById(1)).thenReturn(Optional.of(step));

        stepService.move(1, "up");

        verify(stepRepository, never()).save(any());
    }

    @Test
    void move_whenOtherNotFound_returnsWithoutSaving() {
        Interview interview = new Interview(); interview.setId(10);
        Step step = new Step(); step.setId(1); step.setInterview(interview); step.setPosition(1);
        when(stepRepository.findById(1)).thenReturn(Optional.of(step));

        when(stepRepository.findByInterviewIdAndPosition(10, 0)).thenReturn(Optional.empty());

        stepService.move(1, "up");

        verify(stepRepository, never()).save(any());
    }

    @Test
    void move_swapsPositionsAndSavesBoth() {
        Interview interview = new Interview(); interview.setId(10);

        Step step = new Step(); step.setId(1); step.setInterview(interview); step.setPosition(1);
        Step other = new Step(); other.setId(2); other.setInterview(interview); other.setPosition(0);

        when(stepRepository.findById(1)).thenReturn(Optional.of(step));
        when(stepRepository.findByInterviewIdAndPosition(10, 0)).thenReturn(Optional.of(other));

        stepService.move(1, "up");

        assertEquals(0, step.getPosition());
        assertEquals(1, other.getPosition());

        verify(stepRepository).save(step);
        verify(stepRepository).save(other);
    }

    // -------------------------
    // reorder validations + happy path
    // -------------------------
    @Test
    void reorder_whenIdsNullOrEmpty_throws() {
        assertThrows(IllegalStateException.class, () -> stepService.reorder(1, null));
        assertThrows(IllegalStateException.class, () -> stepService.reorder(1, List.of()));
    }

    @Test
    void reorder_whenNoSteps_throws() {
        when(stepRepository.findByInterviewIdOrderByPositionAsc(1)).thenReturn(List.of());
        assertThrows(IllegalStateException.class, () -> stepService.reorder(1, List.of(1)));
    }

    @Test
    void reorder_whenSizeMismatch_throws() {
        Step a = new Step(); a.setId(1);
        when(stepRepository.findByInterviewIdOrderByPositionAsc(1)).thenReturn(List.of(a));

        assertThrows(IllegalStateException.class, () -> stepService.reorder(1, List.of(1, 2)));
    }

    @Test
    void reorder_whenIdsDontMatch_throws() {
        Step a = new Step(); a.setId(1);
        Step b = new Step(); b.setId(2);
        when(stepRepository.findByInterviewIdOrderByPositionAsc(1)).thenReturn(List.of(a, b));

        assertThrows(IllegalStateException.class, () -> stepService.reorder(1, List.of(1, 3)));
    }

    @Test
    void reorder_happyPath_setsTemporaryNegativeThenFinalPositions() {
        Step a = new Step(); a.setId(1); a.setPosition(0);
        Step b = new Step(); b.setId(2); b.setPosition(1);
        Step c = new Step(); c.setId(3); c.setPosition(2);

        List<Step> steps = new ArrayList<>(List.of(a, b, c));
        when(stepRepository.findByInterviewIdOrderByPositionAsc(9)).thenReturn(steps);

        // reorder ids
        List<Integer> ordered = List.of(3, 1, 2);

        stepService.reorder(9, ordered);

        // Verify 2-phase: saveAll + flush + saveAll
        InOrder inOrder = inOrder(stepRepository);
        inOrder.verify(stepRepository).saveAll(steps);
        inOrder.verify(stepRepository).flush();
        inOrder.verify(stepRepository).saveAll(steps);

        // final positions should match ordered list: 3->0,1->1,2->2
        assertEquals(1, a.getPosition());
        assertEquals(2, b.getPosition());
        assertEquals(0, c.getPosition());
    }

    // -------------------------
    // legacy getSteps/getStep/addNewStep
    // -------------------------
    @Test
    void getSteps_delegates() {
        List<Step> list = List.of(new Step());
        when(stepRepository.findAll()).thenReturn(list);
        assertSame(list, stepService.getSteps());
    }

    @Test
    void getStep_delegates() {
        Step s = new Step();
        when(stepRepository.findById(1)).thenReturn(Optional.of(s));
        assertTrue(stepService.getStep(1).isPresent());
    }

    @Test
    void addNewStep_whenInterviewPresent_setsPositionMaxPlusOne_andScoreZero_andSaves() {
        Interview interview = new Interview();
        interview.setId(10);

        Step step = new Step();
        step.setInterview(interview);

        when(stepRepository.findMaxPositionByInterviewId(10)).thenReturn(4);

        stepService.addNewStep(step);

        assertEquals(5, step.getPosition());
        assertEquals(0, step.getScore());
        verify(stepRepository).save(step);
    }

    @Test
    void addNewStep_whenNoInterview_setsScoreZero_andSaves() {
        Step step = new Step();
        step.setInterview(null);

        stepService.addNewStep(step);

        assertEquals(0, step.getScore());
        verify(stepRepository).save(step);
    }
}
