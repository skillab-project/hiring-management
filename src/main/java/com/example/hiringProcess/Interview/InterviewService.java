package com.example.hiringProcess.Interview;

import com.example.hiringProcess.JobAd.JobAd;
import com.example.hiringProcess.JobAd.JobAdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobAdRepository jobAdRepository;
    private final InterviewMapper interviewMapper; // <-- inject mapper

    @Autowired
    public InterviewService(InterviewRepository interviewRepository,
                            JobAdRepository jobAdRepository,
                            InterviewMapper interviewMapper) {
        this.interviewRepository = interviewRepository;
        this.jobAdRepository = jobAdRepository;
        this.interviewMapper = interviewMapper;
    }

    public List<Interview> getInterviews() {
        return interviewRepository.findAll();
    }

    public Optional<Interview> getInterview(Integer interviewId) {
        return interviewRepository.findById(interviewId);
    }

    public void addNewInterview(Interview interview) {
        interviewRepository.save(interview);
    }

    @Transactional
    public void updateInterview(Integer interviewId, Interview updatedInterview) {
        Interview existing = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new IllegalStateException("Interview with id " + interviewId + " does not exist"));

        // Primitive/simple fields
        if (updatedInterview.getTitle() != null) {
            existing.setTitle(updatedInterview.getTitle());
        }
        if (updatedInterview.getDescription() != null) {
            existing.setDescription(updatedInterview.getDescription());
        }

        // Steps: αντικαθιστούμε πλήρως μόνο αν δόθηκαν (το setSteps φροντίζει το bidirectional link)
        if (updatedInterview.getSteps() != null) {
            existing.setSteps(updatedInterview.getSteps());
        }

        // InterviewReport: αντικαθιστούμε μόνο αν δόθηκε νέο
        if (updatedInterview.getInterviewReports() != null) {
            existing.setInterviewReports(updatedInterview.getInterviewReports());
        }

        // Σημείωση: το jobAd δεν το αλλάζουμε εδώ. Αν θες να το αλλάξεις,
        // μπορείς να προσθέσεις παρόμοια λογική με conditional set.
        // Δεν χρειάζεται explicit save επειδή είσαι σε @Transactional context.
    }

    public void deleteInterview(Integer interviewId) {
        boolean exists = interviewRepository.existsById(interviewId);

        if (!exists) {
            throw new IllegalStateException("Interview with id " + interviewId + " does not exist");
        }
        interviewRepository.deleteById(interviewId);
    }

    public InterviewDetailsDTO getInterviewDetailsByJobAd(Integer jobAdId) {
        JobAd jobAd = jobAdRepository.findById(jobAdId)
                .orElseThrow(() -> new RuntimeException("JobAd not found with id: " + jobAdId));

        var interview = jobAd.getInterview();
        if (interview == null) {
            throw new RuntimeException("No interview found for JobAd with id: " + jobAdId);
        }

        return interviewMapper.toDetailsDTO(interview); // <-- instance call
    }

    @Transactional
    public void updateDescription(Integer interviewId, String description) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new IllegalStateException("Interview " + interviewId + " not found"));
        interview.setDescription(description);
    }
}
