package com.example.hiringProcess.QuestionScore;

import com.example.hiringProcess.Question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionScoreRepository extends JpaRepository<QuestionScore, Integer> {
}
