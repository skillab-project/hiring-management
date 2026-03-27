package com.example.hiringProcess.Interview;

import com.example.hiringProcess.JobAd.JobAd;
import com.example.hiringProcess.JobAd.JobAdRepository;
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
class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private JobAdRepository jobAdRepository;

    @Mock
    private InterviewMapper interviewMapper;

    @InjectMocks
    private InterviewService interviewService;

    // ===== CREATE =====
    @Test
    void addNewInterview_shouldCallSave() {
        Interview interview = new Interview();

        interviewService.addNewInterview(interview);

        verify(interviewRepository).save(interview);
    }

    // ===== READ =====
    @Test
    void getInterviews_shouldReturnAll() {
        Interview interview = new Interview();
        when(interviewRepository.findAll()).thenReturn(List.of(interview));

        List<Interview> result = interviewService.getInterviews();

        assertEquals(1, result.size());
        assertSame(interview, result.get(0));
    }

    @Test
    void getInterview_shouldReturnOptional() {
        Interview interview = new Interview();
        when(interviewRepository.findById(1)).thenReturn(Optional.of(interview));

        Optional<Interview> result = interviewService.getInterview(1);

        assertTrue(result.isPresent());
        assertSame(interview, result.get());
    }

    @Test
    void getInterviewDetailsByJobAd_shouldReturnDTO() {
        // Mock JobAd και Interview
        JobAd jobAd = mock(JobAd.class);
        Interview interview = mock(Interview.class);

        // Dummy List για steps
        List<InterviewDetailsDTO.StepDTO> steps = List.of();

        // Δημιουργία DTO με constructor (required 4 arguments)
        InterviewDetailsDTO dto = new InterviewDetailsDTO(1, "Title", "Description", steps);

        when(jobAdRepository.findById(10)).thenReturn(Optional.of(jobAd));
        when(jobAd.getInterview()).thenReturn(interview);
        when(interviewMapper.toDetailsDTO(interview)).thenReturn(dto);

        InterviewDetailsDTO result = interviewService.getInterviewDetailsByJobAd(10);

        assertSame(dto, result);  // Βεβαιωνόμαστε ότι επιστρέφεται ακριβώς το ίδιο αντικείμενο
    }


    @Test
    void getInterviewDetailsByJobAd_shouldThrowIfNoJobAd() {
        when(jobAdRepository.findById(99)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class,
                () -> interviewService.getInterviewDetailsByJobAd(99));

        assertTrue(ex.getMessage().contains("JobAd not found"));
    }

    @Test
    void getInterviewDetailsByJobAd_shouldThrowIfNoInterview() {
        JobAd jobAd = mock(JobAd.class);
        when(jobAdRepository.findById(10)).thenReturn(Optional.of(jobAd));
        when(jobAd.getInterview()).thenReturn(null);

        Exception ex = assertThrows(RuntimeException.class,
                () -> interviewService.getInterviewDetailsByJobAd(10));

        assertTrue(ex.getMessage().contains("No interview found"));
    }

    // ===== UPDATE =====
    @Test
    void updateInterview_shouldUpdateFields() {
        Interview existing = new Interview();
        existing.setTitle("OldTitle");
        existing.setDescription("OldDesc");

        Interview updated = new Interview();
        updated.setTitle("NewTitle");
        updated.setDescription("NewDesc");

        when(interviewRepository.findById(1)).thenReturn(Optional.of(existing));

        interviewService.updateInterview(1, updated);

        assertEquals("NewTitle", existing.getTitle());
        assertEquals("NewDesc", existing.getDescription());
    }

    @Test
    void updateInterview_shouldThrowIfNotFound() {
        when(interviewRepository.findById(99)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalStateException.class,
                () -> interviewService.updateInterview(99, new Interview()));

        assertTrue(ex.getMessage().contains("does not exist"));
    }

    @Test
    void updateDescription_shouldSetNewDescription() {
        Interview interview = new Interview();
        when(interviewRepository.findById(1)).thenReturn(Optional.of(interview));

        interviewService.updateDescription(1, "NewDesc");

        assertEquals("NewDesc", interview.getDescription());
    }

    @Test
    void updateDescription_shouldThrowIfNotFound() {
        when(interviewRepository.findById(99)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalStateException.class,
                () -> interviewService.updateDescription(99, "desc"));

        assertTrue(ex.getMessage().contains("not found"));
    }

    // ===== DELETE =====
    @Test
    void deleteInterview_shouldCallRepository() {
        when(interviewRepository.existsById(1)).thenReturn(true);

        interviewService.deleteInterview(1);

        verify(interviewRepository).deleteById(1);
    }

    @Test
    void deleteInterview_shouldThrowIfNotFound() {
        when(interviewRepository.existsById(99)).thenReturn(false);

        Exception ex = assertThrows(IllegalStateException.class,
                () -> interviewService.deleteInterview(99));

        assertTrue(ex.getMessage().contains("does not exist"));
    }
}
