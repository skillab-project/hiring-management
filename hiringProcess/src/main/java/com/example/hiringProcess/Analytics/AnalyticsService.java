package com.example.hiringProcess.Analytics;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class AnalyticsService {

    private final AnalyticsRepository repo;

    public AnalyticsService(AnalyticsRepository repo) {
        this.repo = repo;
    }

    /* ------------------------ Helpers ------------------------ */

    private double percent(long part, long total) {
        if (total == 0) return 0.0;
        return BigDecimal.valueOf(part * 100.0 / total)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double round1(double v) {
        return BigDecimal.valueOf(v)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /* -------------------- Organization scope -------------------- */

    public OrganizationStatsDto getOrganizationStats(int orgId) {
        long total    = repo.countTotalCandidatesByOrg(orgId);
        long approved = repo.countCandidatesByStatusOrg(orgId, "Approved");
        long rejected = repo.countCandidatesByStatusOrg(orgId, "Rejected");
        long hires    = repo.countHiresByOrg(orgId);

        double approvalRate  = percent(approved, total);
        double rejectionRate = percent(rejected, total);
        double hireRate      = percent(hires, total);

        List<SkillAvgDto> top5  = repo.topSkillsByOrg(orgId, 5);
        List<SkillAvgDto> weak5 = repo.weakestSkillsByOrg(orgId, 5);

        return new OrganizationStatsDto(
                approvalRate,
                rejectionRate,
                hireRate,
                hires,
                top5,
                weak5
        );
    }

    /* --------------------- Department scope --------------------- */

    public DepartmentStatsDto getDepartmentStats(int deptId) {
        long total    = repo.countTotalCandidatesByDept(deptId);
        long approved = repo.countCandidatesByStatusDept(deptId, "Approved");
        long rejected = repo.countCandidatesByStatusDept(deptId, "Rejected");

        double approvalRate  = percent(approved, total);
        double rejectionRate = percent(rejected, total);

        double avgCandPerJob = repo.avgCandidatesPerJobAdInDept(deptId);

        // score distribution 0..100 (deciles 0..9)
        List<Map<String, Object>> raw = repo.scoreDistributionByDept(deptId);
        long[] counts = new long[10];
        for (Map<String, Object> row : raw) {
            int bucket = ((Number) row.get("bucket")).intValue();
            long cnt   = ((Number) row.get("cnt")).longValue();
            if (bucket < 0) bucket = 0;
            if (bucket > 9) bucket = 9;
            counts[bucket] = cnt;
        }
        List<BucketDto> distribution = new ArrayList<>(10);
        for (int b = 0; b < 10; b++) {
            int from = b * 10;
            int to   = (b == 9) ? 100 : (b + 1) * 10;
            distribution.add(new BucketDto(from, to, counts[b]));
        }

        List<StepAvgDto>       stepDiff = repo.stepDifficultyByDept(deptId);
        List<OccupationAvgDto> occDiff  = repo.occupationDifficultyByDept(deptId);

        return new DepartmentStatsDto(
                approvalRate,
                rejectionRate,
                round1(avgCandPerJob),
                distribution,
                stepDiff,
                occDiff
        );
    }

    /* ---------------------- Occupation scope --------------------- */

    public OccupationStatsDto getOccupationStats(int deptId, int occId) {
        long total    = repo.countTotalCandidatesByDeptOcc(deptId, occId);
        long approved = repo.countCandidatesByStatusDeptOcc(deptId, occId, "Approved");
        long rejected = repo.countCandidatesByStatusDeptOcc(deptId, occId, "Rejected");

        double approvalRate  = percent(approved, total);
        double rejectionRate = percent(rejected, total);

        double candPerJobAd  = repo.avgCandidatesPerJobAdDeptOcc(deptId, occId);

        // Build histogram 0–100 από avg score 0..10
        List<Double> scores = repo.candidateAvgScoresByDeptOcc(deptId, occId);
        long[] buckets = new long[10];
        for (Double s : scores) {
            if (s == null) continue;
            double val = Math.max(0.0, Math.min(10.0, s));
            int idx = (int) Math.floor(val);   // 0..10 -> 0..9
            if (idx > 9) idx = 9;
            if (idx < 0) idx = 0;
            buckets[idx]++;
        }
        List<ScoreBucketDto> dist = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            int from = i * 10;
            int to   = (i == 9) ? 100 : (i * 10 + 9);
            dist.add(new ScoreBucketDto(from, to, buckets[i]));
        }

        return new OccupationStatsDto(
                approvalRate,
                rejectionRate,
                round1(candPerJobAd),
                dist
        );
    }

    /* ------------------------ Job Ad scope ------------------------ */

    public JobAdStatsDto getJobAdStats(int jobAdId) {
        long total    = repo.countTotalCandidatesByJobAd(jobAdId);
        long approved = repo.countCandidatesByStatusJobAd(jobAdId, "Approved");
        long rejected = repo.countCandidatesByStatusJobAd(jobAdId, "Rejected");
        long hires    = repo.countHiresByJobAd(jobAdId);

        double approvalRate = percent(approved, total);
        double rejectionRate = percent(rejected, total);
        double hireRate = percent(hires, total);

        double avgCandidateScore = round1(repo.avgCandidateScoreByJobAd(jobAdId));

        // score distribution 0..100 (deciles 0..9)
        List<Map<String, Object>> raw = repo.scoreDistributionByJobAd(jobAdId);
        long[] counts = new long[10];
        for (Map<String, Object> row : raw) {
            int bucket = ((Number) row.get("bucket")).intValue();
            long cnt   = ((Number) row.get("cnt")).longValue();
            if (bucket < 0) bucket = 0;
            if (bucket > 9) bucket = 9;
            counts[bucket] = cnt;
        }
        List<ScoreBucketDto> distribution = new ArrayList<>(10);
        for (int b = 0; b < 10; b++) {
            int from = b * 10;
            int to   = (b == 9) ? 100 : (b + 1) * 10;
            distribution.add(new ScoreBucketDto(from, to, counts[b]));
        }

        // averages / difficulty sets
        List<StepAvgDto>     stepAvg      = repo.stepDifficultyByJobAd(jobAdId);
        List<QuestionAvgDto> questionDiff = repo.questionDifficultyByJobAd(jobAdId);
        List<SkillAvgDto>    skillDiff    = repo.skillDifficultyByJobAd(jobAdId);

        return new JobAdStatsDto(
                approvalRate,
                rejectionRate,
                hireRate,
                avgCandidateScore,
                distribution,
                stepAvg,
                questionDiff,
                skillDiff
        );
    }

    // ---- Candidates summary (job ad) ----
    public CandidateSummaryDto getCandidateSummaryByJobAd(int jobAdId) {
        long approved = repo.countApprovedCandidatesByJobAd(jobAdId);
        long pending  = repo.countPendingCandidatesByJobAd(jobAdId);
        return new CandidateSummaryDto(approved, pending);
    }

    // ---- Per-candidate analytics ----
    public CandidateStatsDto getCandidateStats(int candidateId) {
        double overall = round1(repo.candidateOverallScore(candidateId));

        var stepScores      = repo.candidateStepScores(candidateId);
        var questionScores  = repo.candidateQuestionScores(candidateId);
        var skillScores     = repo.candidateSkillScores(candidateId);

        // coverage (0..100)
        int reqSkills = repo.requiredSkillCountForCandidateJobAd(candidateId);
        int covSkills = repo.coveredSkillCountForCandidate(candidateId);
        double requiredSkillCoverage = percent(covSkills, Math.max(1, reqSkills)); // avoid 0

        int totalQ   = repo.totalQuestionCountForCandidateJobAd(candidateId);
        int answered = repo.answeredQuestionCountForCandidate(candidateId);
        double questionCoverage = percent(answered, Math.max(1, totalQ));

        var strengths = repo.topSkillsForCandidate(candidateId, 3);
        var weaknesses = repo.weakSkillsForCandidate(candidateId, 3);

        return new CandidateStatsDto(
                overall,
                stepScores,
                questionScores,
                skillScores,
                round1(requiredSkillCoverage),
                round1(questionCoverage),
                strengths,
                weaknesses
        );
    }
    // Λίστα υποψηφίων για job ad (για το αριστερό box στο Candidates tab)
    public List<CandidateLiteDto> getJobAdCandidates(int jobAdId) {
        return repo.candidatesByJobAd(jobAdId);
    }

    // Approved vs Pending summary για job ad (τα κουτάκια Summary)
    public CandidateSummaryDto getCandidateSummaryForJobAd(int jobAdId) {
        long approved = repo.countCandidatesByStatusJobAd(jobAdId, "Approved");
        long pending  = repo.countCandidatesByStatusJobAd(jobAdId, "Pending");
        return new CandidateSummaryDto(approved, pending);
    }

    public StepStatsDto getStepStats(int jobAdId, int stepId) {
        // 1) Avg step score (όλων των ερωτήσεων στο step, σε όλους τους υποψήφιους)
        double avgStepScore = round1(repo.avgStepScoreForJobAdStep(jobAdId, stepId));

        // 2) Candidate step averages (για pass rate + histogram)
        List<Double> avgs = repo.candidateStepAverages(jobAdId, stepId);

        // Pass rate: % των υποψηφίων με avg >= 5.0 (50%)
        long total = avgs.size();
        long passes = avgs.stream().filter(a -> a != null && a >= 5.0).count();
        double passRate = percent(passes, total);

        // Histogram 0..100 (deciles 0..9) από candidate avg (0..10)
        long[] buckets = new long[10];
        for (Double a : avgs) {
            if (a == null) continue;
            double v = Math.max(0.0, Math.min(10.0, a));
            int idx = (int)Math.floor(v);
            if (idx < 0) idx = 0;
            if (idx > 9) idx = 9;
            buckets[idx]++;
        }
        List<ScoreBucketDto> distribution = new java.util.ArrayList<>(10);
        for (int b = 0; b < 10; b++) {
            int from = b * 10;
            int to   = (b == 9) ? 100 : (b + 1) * 10;
            distribution.add(new ScoreBucketDto(from, to, buckets[b]));
        }

        // 3) Rankings (ευκολότερα = υψηλότερα avg)
        var questionRanking = repo.questionRankingByStep(jobAdId, stepId);
        var skillRanking    = repo.skillRankingByStep(jobAdId, stepId);

        return new StepStatsDto(
                round1(passRate),
                avgStepScore,
                distribution,
                questionRanking,
                skillRanking
        );
    }

    public java.util.List<StepLiteDto> getStepsForJobAd(int jobAdId) {
        return repo.stepsForJobAd(jobAdId);
    }

    // AnalyticsService.java  (πρόσθεσε τα παρακάτω)

    public QuestionStatsDto getQuestionStats(int jobAdId, int questionId) {
        // 1) Avg question score (0..10)
        double avg = round1(repo.avgScoreForQuestionInJobAd(jobAdId, questionId));

        // 2) Per-candidate averages για pass rate + histogram
        List<Double> avgs = repo.candidateQuestionAverages(jobAdId, questionId);
        long total = avgs.size();
        long passes = avgs.stream().filter(a -> a != null && a >= 5.0).count();
        double passRate = percent(passes, total);

        // 3) Histogram 0..100 (buckets ανά 10)
        long[] buckets = new long[10];
        for (Double a : avgs) {
            if (a == null) continue;
            double v = Math.max(0.0, Math.min(10.0, a));
            int idx = (int) Math.floor(v);
            if (idx < 0) idx = 0;
            if (idx > 9) idx = 9;
            buckets[idx]++;
        }
        java.util.List<ScoreBucketDto> distribution = new java.util.ArrayList<>(10);
        for (int b = 0; b < 10; b++) {
            int from = b * 10;
            int to   = (b == 9) ? 100 : (b + 1) * 10;
            distribution.add(new ScoreBucketDto(from, to, buckets[b]));
        }

        // 4) Best/Worst skill της ερώτησης
        List<SkillAvgDto> skills = repo.skillAveragesForQuestion(jobAdId, questionId);
        SkillAvgDto best  = skills.isEmpty() ? null : skills.get(0);
        SkillAvgDto worst = skills.isEmpty() ? null : skills.get(skills.size() - 1);

        return new QuestionStatsDto(
                avg,
                round1(passRate),
                best,
                worst,
                distribution
        );
    }

    public java.util.List<QuestionLiteDto> getQuestionsForJobAdStep(int jobAdId, int stepId) {
        return repo.questionsForJobAdStep(jobAdId, stepId);
    }

    public java.util.List<SkillLiteDto> getSkillsForQuestion(int questionId) {
        return repo.skillsForQuestion(questionId);
    }

    public SkillStatsDto getSkillStats(int skillId) {
        // 1) Avg skill score (0..10)
        double avg = round1(repo.avgScoreForSkill(skillId));

        // 2) Candidate averages για pass rate + histogram
        var avgs = repo.candidateSkillAverages(skillId);
        long total = avgs.size();
        long passes = avgs.stream().filter(a -> a != null && a >= 5.0).count();
        double passRate = percent(passes, total);

        // 3) Histogram 0..100 από 0..10 (deciles)
        long[] buckets = new long[10];
        for (Double a : avgs) {
            if (a == null) continue;
            double v = Math.max(0.0, Math.min(10.0, a));
            int idx = (int) Math.floor(v);
            if (idx < 0) idx = 0;
            if (idx > 9) idx = 9;
            buckets[idx]++;
        }
        java.util.List<ScoreBucketDto> distribution = new java.util.ArrayList<>(10);
        for (int b = 0; b < 10; b++) {
            int from = b * 10;
            int to   = (b == 9) ? 100 : (b + 1) * 10;
            distribution.add(new ScoreBucketDto(from, to, buckets[b]));
        }

        return new SkillStatsDto(avg, round1(passRate), distribution);
    }



}
