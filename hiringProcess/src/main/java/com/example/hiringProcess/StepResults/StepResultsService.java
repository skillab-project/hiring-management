package com.example.hiringProcess.StepResults;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StepResultsService {

    private final StepResultsRepository stepResultsRepository;

    @Autowired
    public StepResultsService(StepResultsRepository stepResultsRepository) {
        this.stepResultsRepository = stepResultsRepository;
    }

    public List<StepResults> getAll() {
        return stepResultsRepository.findAll();
    }

    public Optional<StepResults> getById(Integer id) {
        return stepResultsRepository.findById(id);
    }

    public StepResults create(StepResults stepResults) {
        stepResults.setId(0); // force insert
        return stepResultsRepository.save(stepResults);
    }

    @Transactional
    public StepResults update(Integer id, StepResults updatedFields) {
        StepResults existing = stepResultsRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("StepResults with id " + id + " does not exist"));

        // (Αν προσθέσεις πεδία στο entity, κάνε εδώ update)
        return existing;
    }

    @Transactional
    public void delete(Integer id) {
        StepResults existing = stepResultsRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("StepResults with id " + id + " does not exist"));

        // Αποσύνδεσε τις σχέσεις αν χρειαστεί
        existing.setStep(null);
        existing.setInterviewReport(null);

        stepResultsRepository.delete(existing);
    }
}
