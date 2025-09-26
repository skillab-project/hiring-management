package com.example.hiringProcess.StepScore;

import com.example.hiringProcess.InterviewReport.InterviewReportRepository;
import com.example.hiringProcess.Question.QuestionRepository;
import com.example.hiringProcess.SkillScore.SkillScoreRepository;
import com.example.hiringProcess.Step.Step;
import com.example.hiringProcess.Step.StepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class StepScoreService {

    private final QuestionRepository questionRepository;
    private final SkillScoreRepository skillScoreRepository;

    public StepScoreService(QuestionRepository questionRepository,SkillScoreRepository skillScoreRepository) {
        this.questionRepository = questionRepository;
        this.skillScoreRepository = skillScoreRepository;
    }
    @Autowired
    private InterviewReportRepository interviewReportRepository;

    @Autowired
    private StepRepository stepRepository;

    public Optional<Step> getStepById(Integer id) {
        return stepRepository.findById(id);
    }

    public List<Step> getAllSteps() {
        return stepRepository.findAll(); // ή με sort αν θες π.χ. κατά interviewId
    }

    public List<Step> getStepsByInterviewReportId(Integer interviewReportId) {
        var report = interviewReportRepository.findById(interviewReportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report ID: " + interviewReportId));

        return stepRepository.findByInterviewIdOrderByPositionAsc(report.getInterview().getId());
    }

    // Υπολογίζει metrics (στατιστικά) για συγκεκριμένα βήματα (steps) ενός job ad για έναν υποψήφιο
    @Transactional(readOnly = true)
    public List<StepMetricsItemDTO> getStepMetricsByCandidate(Integer candidateId, List<Integer> stepIds) {
        if (stepIds == null || stepIds.isEmpty()) return List.of();

        // 1. Εύρεση total ερωτήσεων ανά step
        Map<Integer, Integer> totalQPerStep = new HashMap<>();
        for (Object[] row : questionRepository.countQuestionsByStepIds(stepIds)) {
            Integer stepId = ((Number) row[0]).intValue();
            Integer cnt    = ((Number) row[1]).intValue();
            totalQPerStep.put(stepId, cnt);
        }

        // 2. Συλλογή όλων των ερωτήσεων ανά step
        Map<Integer, List<Integer>> questionIdsPerStep = new HashMap<>();
        Set<Integer> allQids = new HashSet<>();
        for (Object[] row : questionRepository.listQuestionIdsByStepIds(stepIds)) {
            Integer stepId = ((Number) row[0]).intValue();
            Integer qid    = ((Number) row[1]).intValue();
            questionIdsPerStep.computeIfAbsent(stepId, k -> new ArrayList<>()).add(qid);
            allQids.add(qid);
        }

        if (allQids.isEmpty()) {
            return stepIds.stream()
                    .map(sid -> new StepMetricsItemDTO(
                            sid,
                            totalQPerStep.getOrDefault(sid, 0),
                            0,
                            null))
                    .toList();
        }

        // 3. total skills ανά question
        Map<Integer, Integer> totalSkillsByQ = new HashMap<>();
        for (Object[] row : questionRepository.countSkillsForQuestions(allQids)) {
            Integer qid = (Integer) row[0];
            Number n    = (Number) row[1];
            int cnt     = n == null ? 0 : n.intValue();
            totalSkillsByQ.put(qid, cnt);
        }

        // 4. rated skills & avgScore ανά question (με βάση candidateId)
        Map<Integer, Integer> ratedSkillsByQ = new HashMap<>();
        Map<Integer, Double>  avgScoreByQ    = new HashMap<>();
        if (candidateId != null) {
            var rows = skillScoreRepository.aggregateByCandidateAndQuestionIdsRaw(candidateId, allQids);
            for (Object[] r : rows) {
                Integer qid = (Integer) r[0];
                int rated    = ((Number) r[1]).intValue();
                Double avg   = r[2] == null ? null : ((Number) r[2]).doubleValue();
                ratedSkillsByQ.put(qid, rated);
                if (avg != null) avgScoreByQ.put(qid, avg);
            }
        }

        // 5. Σύνθεση ανά step
        List<StepMetricsItemDTO> out = new ArrayList<>(stepIds.size());
        for (Integer stepId : stepIds) {
            int totalQ = totalQPerStep.getOrDefault(stepId, 0);
            List<Integer> qids = questionIdsPerStep.getOrDefault(stepId, List.of());

            int fullyRated = 0;
            double sum = 0.0;
            int cnt = 0;

            for (Integer qid : qids) {
                int totalSkills = totalSkillsByQ.getOrDefault(qid, 0);
                int ratedSkills = ratedSkillsByQ.getOrDefault(qid, 0);
                boolean fully = totalSkills > 0 && ratedSkills == totalSkills;
                if (fully) {
                    Double qAvg = avgScoreByQ.get(qid);
                    if (qAvg != null) {
                        fullyRated++;
                        sum += qAvg;
                        cnt++;
                    }
                }
            }

            Integer avgRounded = (cnt > 0) ? (int) Math.round(sum / cnt) : null;
            out.add(new StepMetricsItemDTO(stepId, totalQ, fullyRated, avgRounded));
        }

        return out;
    }
}
