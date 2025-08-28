package com.example.hiringProcess.Assessment;

import com.example.hiringProcess.Interview.InterviewRepository;
import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.Question.QuestionRepository;
import com.example.hiringProcess.SkillScore.SkillScoreRepository;
import com.example.hiringProcess.Step.Step;
import com.example.hiringProcess.Step.StepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AssessmentAggregationService {

    private final InterviewRepository interviewRepository;
    private final StepRepository stepRepository;
    private final QuestionRepository questionRepository;
    private final SkillScoreRepository skillScoreRepository;

    /**
     * Για κάθε step:
     * - totalQuestions
     * - ratedQuestions = ερωτήσεις με ΟΛΑ τα skills βαθμολογημένα
     * - averageScore   = avg(questionAvg) μόνο από fully-rated questions
     */
    @Transactional(readOnly = true)
    public List<StepAssessmentDTO> stepAssessments(int interviewId, int candidateId) {
        // validate interview
        interviewRepository.findById(interviewId)
                .orElseThrow(() -> new IllegalStateException("Interview " + interviewId + " not found"));

        // steps
        List<Step> steps = stepRepository.findByInterviewIdOrderByPositionAsc(interviewId);

        // total Q per step
        Map<Integer,Integer> totalQPerStep = new HashMap<>();
        for (Object[] row : questionRepository.countQuestionsPerStep(interviewId)) {
            int stepId = ((Number) row[0]).intValue();
            int cnt    = ((Number) row[1]).intValue();
            totalQPerStep.put(stepId, cnt);
        }

        // total skills per question
        Map<Integer,Integer> totalSkillsPerQuestion = new HashMap<>();
        for (Object[] row : questionRepository.countSkillsPerQuestion(interviewId)) {
            int qId = ((Number) row[0]).intValue();
            int cnt = ((Number) row[1]).intValue();
            totalSkillsPerQuestion.put(qId, cnt);
        }

        // question averages & rated skills for candidate
        Map<Integer,Double> avgPerQuestion    = new HashMap<>();
        Map<Integer,Integer> ratedPerQuestion = new HashMap<>();
        for (Object[] row : skillScoreRepository.questionAverages(interviewId, candidateId)) {
            int qId = ((Number) row[0]).intValue();
            Double avg = row[1] == null ? null : ((Number) row[1]).doubleValue();
            int ratedSkills = ((Number) row[2]).intValue();
            if (avg != null) avgPerQuestion.put(qId, avg);
            ratedPerQuestion.put(qId, ratedSkills);
        }

        // compose per step
        List<StepAssessmentDTO> out = new ArrayList<>(steps.size());
        for (Step s : steps) {
            List<Question> qs = questionRepository.findByStep_IdOrderByPositionAsc(s.getId());

            int totalQ = totalQPerStep.getOrDefault(s.getId(), 0);
            int fullyRatedQ = 0;
            double sumAvgs = 0.0;
            int counted = 0;

            for (Question q : qs) {
                int qId = q.getId();
                int totalSkills = totalSkillsPerQuestion.getOrDefault(qId, 0);
                int ratedSkills = ratedPerQuestion.getOrDefault(qId, 0);
                boolean fullyRated = totalSkills > 0 && ratedSkills == totalSkills;

                if (fullyRated) {
                    Double qAvg = avgPerQuestion.get(qId);
                    if (qAvg != null) {
                        fullyRatedQ++;
                        sumAvgs += qAvg;
                        counted++;
                    }
                }
            }
            Double stepAvg = counted > 0 ? (sumAvgs / counted) : null;

            out.add(new StepAssessmentDTO(
                    s.getId(),
                    s.getTitle(),
                    totalQ,
                    fullyRatedQ,
                    stepAvg
            ));
        }
        return out;
    }
}
