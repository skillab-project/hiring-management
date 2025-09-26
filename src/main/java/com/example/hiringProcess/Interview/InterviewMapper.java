package com.example.hiringProcess.Interview;

import com.example.hiringProcess.Interview.InterviewDetailsDTO;
import com.example.hiringProcess.Interview.Interview;
import com.example.hiringProcess.Step.Step;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InterviewMapper {
    public InterviewDetailsDTO toDetailsDTO(Interview interview) {
        if (interview == null) return null;

        List<InterviewDetailsDTO.StepDTO> stepDTOs = interview.getSteps().stream()
                .map(step -> new InterviewDetailsDTO.StepDTO(step.getId(), step.getTitle()))
                .toList();

        return new InterviewDetailsDTO(
                interview.getId(),
                interview.getTitle(),
                interview.getDescription(),
                stepDTOs
        );
    }
}
