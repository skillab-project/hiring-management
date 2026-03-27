package com.example.hiringProcess.QuestionScore;

import com.example.hiringProcess.Candidate.Candidate;
import com.example.hiringProcess.Candidate.CandidateRepository;
import com.example.hiringProcess.InterviewReport.InterviewReport; // αν έχεις άλλο package, Alt+Enter να το φέρεις σωστά
import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.Question.QuestionRepository;
import com.example.hiringProcess.SkillScore.SkillScoreRepository;
import com.example.hiringProcess.StepScore.StepScore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionScoreServiceTest {

    @Mock QuestionScoreRepository questionScoreRepository;
    @Mock QuestionRepository questionRepository;
    @Mock SkillScoreRepository skillScoreRepository;
    @Mock QuestionScoreMapper questionScoreMapper;
    @Mock CandidateRepository candidateRepository;

    @InjectMocks QuestionScoreService questionScoreService;

    // -------------------------
    // create
    // -------------------------

    @Test
    void create_forcesInsertBySettingId0_andSaves() {
        QuestionScore qs = new QuestionScore();
        qs.setId(999);

        when(questionScoreRepository.save(any(QuestionScore.class))).thenAnswer(inv -> inv.getArgument(0));

        QuestionScore saved = questionScoreService.create(qs);

        assertEquals(0, saved.getId());
        verify(questionScoreRepository).save(qs);
    }

    // -------------------------
    // update
    // -------------------------

    @Test
    void update_whenNotFound_throws() {
        when(questionScoreRepository.findById(1)).thenReturn(Optional.empty());

        QuestionScore upd = new QuestionScore();
        upd.setScore(5);

        assertThrows(IllegalStateException.class, () -> questionScoreService.update(1, upd));
    }

    @Test
    void update_whenScoreDifferent_updatesScore() {
        QuestionScore existing = new QuestionScore();
        existing.setId(1);
        existing.setScore(2);

        when(questionScoreRepository.findById(1)).thenReturn(Optional.of(existing));

        QuestionScore upd = new QuestionScore();
        upd.setScore(5);

        QuestionScore result = questionScoreService.update(1, upd);

        assertSame(existing, result);
        assertEquals(5, existing.getScore());
    }

    @Test
    void update_whenScoreSame_keepsScore() {
        QuestionScore existing = new QuestionScore();
        existing.setId(1);
        existing.setScore(3);

        when(questionScoreRepository.findById(1)).thenReturn(Optional.of(existing));

        QuestionScore upd = new QuestionScore();
        upd.setScore(3);

        questionScoreService.update(1, upd);

        assertEquals(3, existing.getScore());
    }

    // -------------------------
    // delete
    // -------------------------

    @Test
    void delete_whenNotFound_throws() {
        when(questionScoreRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> questionScoreService.delete(10));
        verify(questionScoreRepository, never()).delete(any());
    }

    @Test
    void delete_nullsRelationsThenDeletes() {
        QuestionScore existing = new QuestionScore();
        existing.setId(10);
        existing.setQuestion(new Question());
        existing.setStepScore(new StepScore()); // αν το StepScore class είναι αλλού/άλλο όνομα, προσαρμόζεις

        when(questionScoreRepository.findById(10)).thenReturn(Optional.of(existing));

        questionScoreService.delete(10);

        assertNull(existing.getQuestion());
        assertNull(existing.getStepScore());
        verify(questionScoreRepository).delete(existing);
    }

    // -------------------------
    // getQuestionsByStep
    // -------------------------

    @Test
    void getQuestionsByStep_returnsRepoResult() {
        List<Question> qs = List.of(new Question(), new Question());
        when(questionRepository.findByStepIdOrderByPositionAsc(7)).thenReturn(qs);

        List<Question> result = questionScoreService.getQuestionsByStep(7);

        assertSame(qs, result);
        verify(questionRepository).findByStepIdOrderByPositionAsc(7);
    }

    // -------------------------
    // getQuestionMetricsByReport
    // -------------------------

    @Test
    void getQuestionMetricsByReport_whenIdsNullOrEmpty_returnsEmpty() {
        assertTrue(questionScoreService.getQuestionMetricsByReport(1, null).isEmpty());
        assertTrue(questionScoreService.getQuestionMetricsByReport(1, List.of()).isEmpty());
        verifyNoInteractions(questionRepository, skillScoreRepository, questionScoreMapper);
    }

    @Test
    void getQuestionMetricsByReport_buildsTotalsRatedAvgRounded_andMapsToDto() {
        Integer reportId = 50;
        List<Integer> questionIds = List.of(1, 2, 3);

        // (qid, totalSkills)
        when(questionRepository.countSkillsForQuestions(questionIds)).thenReturn(List.of(
                new Object[]{1, 4},
                new Object[]{2, 2}
                // q3 missing => default 0
        ));

        // (qid, avg, cntRated)
        when(skillScoreRepository.aggregateByReportAndQuestionIds(reportId, questionIds)).thenReturn(List.of(
                new Object[]{1, 2.4, 3},   // avgRounded=2, rated=3
                new Object[]{2, null, 0}   // avgRounded=null, rated=0
        ));

        // mapper: επιστρέφει mock DTOs
        QuestionMetricsItemDTO dto1 = mock(QuestionMetricsItemDTO.class);
        QuestionMetricsItemDTO dto2 = mock(QuestionMetricsItemDTO.class);
        QuestionMetricsItemDTO dto3 = mock(QuestionMetricsItemDTO.class);

        when(questionScoreMapper.toDto(1, 4, 3, 2)).thenReturn(dto1);
        when(questionScoreMapper.toDto(2, 2, 0, null)).thenReturn(dto2);
        when(questionScoreMapper.toDto(3, 0, 0, null)).thenReturn(dto3);

        List<QuestionMetricsItemDTO> out = questionScoreService.getQuestionMetricsByReport(reportId, questionIds);

        assertEquals(3, out.size());
        assertSame(dto1, out.get(0));
        assertSame(dto2, out.get(1));
        assertSame(dto3, out.get(2));

        verify(questionScoreMapper).toDto(1, 4, 3, 2);
        verify(questionScoreMapper).toDto(2, 2, 0, null);
        verify(questionScoreMapper).toDto(3, 0, 0, null);
    }

    @Test
    void getQuestionMetricsByReport_whenReportIdNull_skipsSkillAggregate() {
        List<Integer> questionIds = List.of(1);


        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{1, 5});

        when(questionRepository.countSkillsForQuestions(questionIds)).thenReturn(rows);


        QuestionMetricsItemDTO dto = mock(QuestionMetricsItemDTO.class);
        when(questionScoreMapper.toDto(1, 5, 0, null)).thenReturn(dto);

        List<QuestionMetricsItemDTO> out = questionScoreService.getQuestionMetricsByReport(null, questionIds);

        assertEquals(1, out.size());
        assertSame(dto, out.get(0));

        verify(skillScoreRepository, never()).aggregateByReportAndQuestionIds(any(), any());
        verify(questionScoreMapper).toDto(1, 5, 0, null);
    }

    // -------------------------
    // getQuestionMetricsByCandidate
    // -------------------------

    @Test
    void getQuestionMetricsByCandidate_whenCandidateMissing_throws() {
        when(candidateRepository.findById(7)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> questionScoreService.getQuestionMetricsByCandidate(7, List.of(1)));
    }

    @Test
    void getQuestionMetricsByCandidate_usesCandidateReportIdAndDelegates() {
        Candidate cand = new Candidate();
        InterviewReport report = new InterviewReport();
        report.setId(99);
        cand.setInterviewReport(report);

        when(candidateRepository.findById(7)).thenReturn(Optional.of(cand));

        // κάνουμε “spy” για να ελέγξουμε ότι καλεί τη μέθοδο report
        QuestionScoreService spy = spy(questionScoreService);

        List<QuestionMetricsItemDTO> expected = List.of(mock(QuestionMetricsItemDTO.class));
        doReturn(expected).when(spy).getQuestionMetricsByReport(99, List.of(1, 2));

        List<QuestionMetricsItemDTO> out = spy.getQuestionMetricsByCandidate(7, List.of(1, 2));

        assertSame(expected, out);
        verify(spy).getQuestionMetricsByReport(99, List.of(1, 2));
    }

    @Test
    void getQuestionMetricsByCandidate_whenNoReportId_passesNullReport() {
        Candidate cand = new Candidate();
        cand.setInterviewReport(null);

        when(candidateRepository.findById(7)).thenReturn(Optional.of(cand));

        QuestionScoreService spy = spy(questionScoreService);

        List<QuestionMetricsItemDTO> expected = List.of();
        doReturn(expected).when(spy).getQuestionMetricsByReport(null, List.of(1));

        List<QuestionMetricsItemDTO> out = spy.getQuestionMetricsByCandidate(7, List.of(1));

        assertSame(expected, out);
        verify(spy).getQuestionMetricsByReport(null, List.of(1));
    }
}
