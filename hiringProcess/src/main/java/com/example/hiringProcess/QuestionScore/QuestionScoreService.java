package com.example.hiringProcess.QuestionScore;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class QuestionScoreService {

    private final QuestionScoreRepository questionScoreRepository;

    @Autowired
    public QuestionScoreService(QuestionScoreRepository questionScoreRepository) {
        this.questionScoreRepository = questionScoreRepository;
    }

    public List<QuestionScore> getAll() {
        return questionScoreRepository.findAll();
    }

    public Optional<QuestionScore> getById(Integer id) {
        return questionScoreRepository.findById(id);
    }

    public QuestionScore create(QuestionScore questionScore) {
        questionScore.setId(0); // force insert
        return questionScoreRepository.save(questionScore);
    }

    @Transactional
    public QuestionScore update(Integer id, QuestionScore updatedFields) {
        QuestionScore existing = questionScoreRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("QuestionScore with id " + id + " does not exist"));

        if (!Objects.equals(existing.getScore(), updatedFields.getScore())) {
            existing.setScore(updatedFields.getScore());
        }

        return existing;
    }

    @Transactional
    public void delete(Integer id) {
        QuestionScore existing = questionScoreRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("QuestionScore with id " + id + " does not exist"));

        // Optional: remove associations if needed
        existing.setQuestion(null);
        existing.setStepResults(null);

        questionScoreRepository.delete(existing);
    }
}
