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

    // Επιστρέφει όλα τα InterviewReports
    public List<InterviewReport> getAll() {
        return interviewReportRepository.findAll();
    }

    // Επιστρέφει InterviewReport με βάση το id
    public Optional<InterviewReport> getById(Integer id) {
        return interviewReportRepository.findById(id);
    }

    // Δημιουργεί νέο InterviewReport
    public InterviewReport create(InterviewReport report) {
        report.setId(0); // Force insert (να μην γίνει update)
        return interviewReportRepository.save(report);
    }

    // Ενημερώνει υπάρχον InterviewReport
    @Transactional
    public InterviewReport update(Integer id, InterviewReport updatedFields) {
        InterviewReport existing = interviewReportRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("InterviewReport with id " + id + " does not exist"));

        return existing;
    }

    // Διαγράφει InterviewReport και αποσυνδέει Candidate/Interview
    @Transactional
    public void delete(Integer id) {
        InterviewReport existing = interviewReportRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("InterviewReport with id " + id + " does not exist"));

        existing.setCandidate(null);
        existing.setInterview(null);

        interviewReportRepository.delete(existing);
    }
}
