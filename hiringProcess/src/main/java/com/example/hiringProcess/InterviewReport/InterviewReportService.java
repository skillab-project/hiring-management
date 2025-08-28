package com.example.hiringProcess.InterviewReport;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InterviewReportService {

    private final InterviewReportRepository interviewReportRepository;

    @Autowired
    public InterviewReportService(InterviewReportRepository interviewReportRepository) {
        this.interviewReportRepository = interviewReportRepository;
    }

    public List<InterviewReport> getAll() {
        return interviewReportRepository.findAll();
    }

    public Optional<InterviewReport> getById(Integer id) {
        return interviewReportRepository.findById(id);
    }

    public InterviewReport create(InterviewReport report) {
        report.setId(0); // Force insert
        return interviewReportRepository.save(report);
    }

    @Transactional
    public InterviewReport update(Integer id, InterviewReport updatedFields) {
        InterviewReport existing = interviewReportRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("InterviewReport with id " + id + " does not exist"));

        // Εδώ μπορείς να προσθέσεις updates αν προστεθούν editable fields
        return existing;
    }

    @Transactional
    public void delete(Integer id) {
        InterviewReport existing = interviewReportRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("InterviewReport with id " + id + " does not exist"));

        existing.setCandidate(null);
        existing.setInterview(null);

        interviewReportRepository.delete(existing);
    }
}
