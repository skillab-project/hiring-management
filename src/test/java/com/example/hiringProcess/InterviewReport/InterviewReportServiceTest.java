package com.example.hiringProcess.InterviewReport;

import com.example.hiringProcess.Candidate.Candidate;
import com.example.hiringProcess.Interview.Interview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InterviewReportServiceTest {

    @Mock
    private InterviewReportRepository interviewReportRepository;

    @InjectMocks
    private InterviewReportService interviewReportService;

    @Test
    void getAll_shouldReturnAllReports() {
        InterviewReport report1 = new InterviewReport();
        InterviewReport report2 = new InterviewReport();
        when(interviewReportRepository.findAll()).thenReturn(List.of(report1, report2));

        List<InterviewReport> result = interviewReportService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(report1));
        assertTrue(result.contains(report2));
    }

    @Test
    void getById_shouldReturnReportIfExists() {
        InterviewReport report = new InterviewReport();
        when(interviewReportRepository.findById(1)).thenReturn(Optional.of(report));

        Optional<InterviewReport> result = interviewReportService.getById(1);

        assertTrue(result.isPresent());
        assertSame(report, result.get());
    }

    @Test
    void create_shouldSetIdZeroAndSave() {
        InterviewReport report = new InterviewReport();
        when(interviewReportRepository.save(report)).thenReturn(report);

        InterviewReport result = interviewReportService.create(report);

        assertEquals(0, report.getId()); // Βεβαιωνόμαστε ότι το ID έχει γίνει 0
        assertSame(report, result);
        verify(interviewReportRepository).save(report);
    }

    @Test
    void delete_shouldSetCandidateAndInterviewToNullAndDelete() {
        InterviewReport report = new InterviewReport();
        Candidate candidate = new Candidate();  // σωστός τύπος
        Interview interview = new Interview();  // σωστός τύπος
        report.setCandidate(candidate);
        report.setInterview(interview);

        when(interviewReportRepository.findById(1)).thenReturn(Optional.of(report));

        interviewReportService.delete(1);

        assertNull(report.getCandidate());
        assertNull(report.getInterview());
        verify(interviewReportRepository).delete(report);
    }


    @Test
    void delete_shouldThrowIfReportNotFound() {
        when(interviewReportRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            interviewReportService.delete(1);
        });

        assertEquals("InterviewReport with id 1 does not exist", exception.getMessage());
    }
}
