package com.example.hiringProcess.StepScore;

import com.example.hiringProcess.InterviewReport.InterviewReport;
import com.example.hiringProcess.InterviewReport.InterviewReportRepository;
import com.example.hiringProcess.Question.QuestionRepository;
import com.example.hiringProcess.SkillScore.SkillScoreRepository;
import com.example.hiringProcess.Step.Step;
import com.example.hiringProcess.Step.StepRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StepScoreServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private SkillScoreRepository skillScoreRepository;

    @Mock
    private InterviewReportRepository interviewReportRepository;

    @Mock
    private StepRepository stepRepository;

    @InjectMocks
    private StepScoreService stepScoreService;

    @Test
    void getStepById_shouldReturnStepIfExists() {
        Step step = new Step();
        when(stepRepository.findById(1)).thenReturn(Optional.of(step));

        Optional<Step> result = stepScoreService.getStepById(1);

        assertTrue(result.isPresent());
        assertSame(step, result.get());
    }

    @Test
    void getAllSteps_shouldReturnAllSteps() {
        Step step1 = new Step();
        Step step2 = new Step();
        when(stepRepository.findAll()).thenReturn(List.of(step1, step2));

        List<Step> result = stepScoreService.getAllSteps();

        assertEquals(2, result.size());
        assertTrue(result.contains(step1));
        assertTrue(result.contains(step2));
    }

    @Test
    void getStepsByInterviewReportId_shouldReturnStepsForReport() {
        InterviewReport report = mock(InterviewReport.class);
        var interview = mock(com.example.hiringProcess.Interview.Interview.class);
        when(report.getInterview()).thenReturn(interview);
        when(interview.getId()).thenReturn(100);
        when(interviewReportRepository.findById(1)).thenReturn(Optional.of(report));

        Step step1 = new Step();
        Step step2 = new Step();
        when(stepRepository.findByInterviewIdOrderByPositionAsc(100)).thenReturn(List.of(step1, step2));

        List<Step> result = stepScoreService.getStepsByInterviewReportId(1);

        assertEquals(2, result.size());
        assertTrue(result.contains(step1));
        assertTrue(result.contains(step2));
    }

    @Test
    void getStepsByInterviewReportId_shouldThrowIfReportNotFound() {
        when(interviewReportRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                stepScoreService.getStepsByInterviewReportId(1)
        );

        assertEquals("Invalid report ID: 1", exception.getMessage());
    }

    @Test
    void getStepMetricsByCandidate_shouldReturnEmptyListIfStepIdsEmpty() {
        List<StepMetricsItemDTO> result = stepScoreService.getStepMetricsByCandidate(1, Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void getStepMetricsByCandidate_shouldReturnMetricsProperly() {
        List<Integer> stepIds = List.of(1, 2);

        when(questionRepository.countQuestionsByStepIds(stepIds))
                .thenReturn(List.of(new Object[]{1, 2}, new Object[]{2, 1}));

        when(questionRepository.listQuestionIdsByStepIds(stepIds))
                .thenReturn(List.of(
                        new Object[]{1, 10},
                        new Object[]{1, 11},
                        new Object[]{2, 20}
                ));

        when(questionRepository.countSkillsForQuestions(Set.of(10, 11, 20)))
                .thenReturn(List.of(
                        new Object[]{10, 2},
                        new Object[]{11, 1},
                        new Object[]{20, 1}
                ));

        when(skillScoreRepository.aggregateByCandidateAndQuestionIdsRaw(eq(1), anySet()))
                .thenReturn(List.of(
                        new Object[]{10, 2, 4.0},
                        new Object[]{11, 1, 3.0},
                        new Object[]{20, 1, 5.0}
                ));

        List<StepMetricsItemDTO> result = stepScoreService.getStepMetricsByCandidate(1, stepIds);

        assertEquals(2, result.size());

        StepMetricsItemDTO step1Metrics = result.stream().filter(s -> s.getStepId() == 1).findFirst().orElseThrow();
        assertEquals(2, step1Metrics.getTotalQuestions());
        assertEquals(2, step1Metrics.getRatedQuestions());
        assertEquals(4, step1Metrics.getAverageScore());

        StepMetricsItemDTO step2Metrics = result.stream().filter(s -> s.getStepId() == 2).findFirst().orElseThrow();
        assertEquals(1, step2Metrics.getTotalQuestions());
        assertEquals(1, step2Metrics.getRatedQuestions());
        assertEquals(5, step2Metrics.getAverageScore());
    }

}
