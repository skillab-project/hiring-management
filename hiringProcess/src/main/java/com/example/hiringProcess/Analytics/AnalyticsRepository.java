package com.example.hiringProcess.Analytics;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.SQLException;


@Repository
public class AnalyticsRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public AnalyticsRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /* ======================  ORGANIZATION  ====================== */

    public long countTotalCandidatesByOrg(int orgId) {
        String sql = """
            SELECT COUNT(c.id)
            FROM candidate c
            JOIN job_ad ja ON c.job_ad_id = ja.id
            JOIN jobad_department jd ON ja.id = jd.jobad_id
            JOIN department d ON jd.department_id = d.id
            WHERE d.organisation_id = :orgId
            """;
        Long n = jdbc.queryForObject(sql, Map.of("orgId", orgId), Long.class);
        return n == null ? 0L : n;
    }

    public long countCandidatesByStatusOrg(int orgId, String status) {
        String sql = """
            SELECT COUNT(c.id)
            FROM candidate c
            JOIN job_ad ja ON c.job_ad_id = ja.id
            JOIN jobad_department jd ON ja.id = jd.jobad_id
            JOIN department d ON jd.department_id = d.id
            WHERE d.organisation_id = :orgId AND c.status = :status
            """;
        Long n = jdbc.queryForObject(sql, Map.of("orgId", orgId, "status", status), Long.class);
        return n == null ? 0L : n;
    }

    /** Hire = Approved candidate on a Complete job ad (προσαρμόζεται αν θες άλλο business rule) */
    public long countHiresByOrg(int orgId) {
        String sql = """
            SELECT COUNT(c.id)
            FROM candidate c
            JOIN job_ad ja ON c.job_ad_id = ja.id
            JOIN jobad_department jd ON ja.id = jd.jobad_id
            JOIN department d ON jd.department_id = d.id
            WHERE d.organisation_id = :orgId
              AND c.status = 'Approved'
              AND ja.status = 'Complete'
            """;
        Long n = jdbc.queryForObject(sql, Map.of("orgId", orgId), Long.class);
        return n == null ? 0L : n;
    }

    /* === Mappers === */
    private static final RowMapper<SkillAvgDto> SKILL_AVG_MAPPER =
            (rs, rowNum) -> new SkillAvgDto(rs.getString("skill"), rs.getDouble("avg_score"));

    private static final RowMapper<QuestionAvgDto> QUESTION_AVG_MAPPER =
            (rs, rowNum) -> new QuestionAvgDto(rs.getString("question"), rs.getDouble("avg_score"));

    public List<SkillAvgDto> topSkillsByOrg(int orgId, int limit) {
        String sql = """
            SELECT s.title AS skill, AVG(qs.score) AS avg_score
            FROM candidate c
            JOIN job_ad ja               ON c.job_ad_id = ja.id
            JOIN jobad_department jd     ON ja.id = jd.jobad_id
            JOIN department d            ON jd.department_id = d.id
            JOIN interview_report ir     ON ir.id = c.interview_report_id
            JOIN step_results sr         ON sr.interview_report_id = ir.id
            JOIN question_score qs       ON qs.step_results_id = sr.id
            JOIN question q              ON q.id = qs.question_id
            JOIN question_skill qsk      ON qsk.question_id = q.id
            JOIN skill s                 ON s.id = qsk.skill_id
            WHERE d.organisation_id = :orgId
            GROUP BY s.title
            HAVING COUNT(qs.score) > 0
            ORDER BY avg_score DESC
            LIMIT :limit
            """;
        return jdbc.query(sql, Map.of("orgId", orgId, "limit", limit), SKILL_AVG_MAPPER);
    }

    public List<SkillAvgDto> weakestSkillsByOrg(int orgId, int limit) {
        String sql = """
            SELECT s.title AS skill, AVG(qs.score) AS avg_score
            FROM candidate c
            JOIN job_ad ja               ON c.job_ad_id = ja.id
            JOIN jobad_department jd     ON ja.id = jd.jobad_id
            JOIN department d            ON jd.department_id = d.id
            JOIN interview_report ir     ON ir.id = c.interview_report_id
            JOIN step_results sr         ON sr.interview_report_id = ir.id
            JOIN question_score qs       ON qs.step_results_id = sr.id
            JOIN question q              ON q.id = qs.question_id
            JOIN question_skill qsk      ON qsk.question_id = q.id
            JOIN skill s                 ON s.id = qsk.skill_id
            WHERE d.organisation_id = :orgId
            GROUP BY s.title
            HAVING COUNT(qs.score) > 0
            ORDER BY avg_score ASC
            LIMIT :limit
            """;
        return jdbc.query(sql, Map.of("orgId", orgId, "limit", limit), SKILL_AVG_MAPPER);
    }

    /* ======================  DEPARTMENT  ====================== */

    public long countTotalCandidatesByDept(int deptId) {
        String sql = """
        SELECT COUNT(c.id)
        FROM candidate c
        JOIN job_ad ja ON c.job_ad_id = ja.id
        JOIN jobad_department jd ON ja.id = jd.jobad_id
        WHERE jd.department_id = :deptId
        """;
        Long n = jdbc.queryForObject(sql, Map.of("deptId", deptId), Long.class);
        return n == null ? 0L : n;
    }

    public long countCandidatesByStatusDept(int deptId, String status) {
        String sql = """
        SELECT COUNT(c.id)
        FROM candidate c
        JOIN job_ad ja ON c.job_ad_id = ja.id
        JOIN jobad_department jd ON ja.id = jd.jobad_id
        WHERE jd.department_id = :deptId AND c.status = :status
        """;
        Long n = jdbc.queryForObject(sql, Map.of("deptId", deptId, "status", status), Long.class);
        return n == null ? 0L : n;
    }

    public Double avgCandidatesPerJobAdInDept(int deptId) {
        String sql = """
        SELECT AVG(cnt) FROM (
            SELECT COUNT(c.id) AS cnt
            FROM job_ad ja
            JOIN jobad_department jd ON ja.id = jd.jobad_id
            LEFT JOIN candidate c ON c.job_ad_id = ja.id
            WHERE jd.department_id = :deptId
            GROUP BY ja.id
        ) sub
        """;
        Double v = jdbc.queryForObject(sql, Map.of("deptId", deptId), Double.class);
        return v == null ? 0.0 : v;
    }

    public List<Map<String, Object>> scoreDistributionByDept(int deptId) {
        String sql = """
        SELECT bucket, COUNT(*) AS cnt
        FROM (
            SELECT LEAST(9, FLOOR(AVG(qs.score))) AS bucket
            FROM candidate c
            JOIN job_ad ja            ON c.job_ad_id = ja.id
            JOIN jobad_department jd  ON ja.id = jd.jobad_id
            JOIN interview_report ir  ON ir.id = c.interview_report_id
            JOIN step_results sr      ON sr.interview_report_id = ir.id
            JOIN question_score qs    ON qs.step_results_id = sr.id
            WHERE jd.department_id = :deptId
            GROUP BY c.id
        ) t
        GROUP BY bucket
        ORDER BY bucket
        """;
        return jdbc.queryForList(sql, Map.of("deptId", deptId));
    }

    private static final RowMapper<StepAvgDto> STEP_AVG_MAPPER =
            (rs, i) -> new StepAvgDto(rs.getString("step"), rs.getDouble("avg_score"));

    public List<StepAvgDto> stepDifficultyByDept(int deptId) {
        String sql = """
        SELECT st.title AS step, AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN job_ad ja            ON c.job_ad_id = ja.id
        JOIN jobad_department jd  ON ja.id = jd.jobad_id
        JOIN interview_report ir  ON ir.id = c.interview_report_id
        JOIN step_results sr      ON sr.interview_report_id = ir.id
        JOIN step st              ON st.id = sr.step_id
        JOIN question_score qs    ON qs.step_results_id = sr.id
        WHERE jd.department_id = :deptId
        GROUP BY st.title
        HAVING COUNT(qs.score) > 0
        ORDER BY st.title
        """;
        return jdbc.query(sql, Map.of("deptId", deptId), STEP_AVG_MAPPER);
    }

    private static final RowMapper<OccupationAvgDto> OCC_AVG_MAPPER =
            (rs, i) -> new OccupationAvgDto(rs.getString("occupation"), rs.getDouble("avg_score"));

    public List<OccupationAvgDto> occupationDifficultyByDept(int deptId) {
        String sql = """
        SELECT oc.title AS occupation, AVG(cavg.avg_score) AS avg_score
        FROM (
            SELECT c.id AS cand_id, c.job_ad_id, AVG(qs.score) AS avg_score
            FROM candidate c
            JOIN job_ad ja            ON c.job_ad_id = ja.id
            JOIN jobad_department jd  ON ja.id = jd.jobad_id
            JOIN interview_report ir  ON ir.id = c.interview_report_id
            JOIN step_results sr      ON sr.interview_report_id = ir.id
            JOIN question_score qs    ON qs.step_results_id = sr.id
            WHERE jd.department_id = :deptId
            GROUP BY c.id, c.job_ad_id
        ) cavg
        JOIN job_ad ja      ON ja.id = cavg.job_ad_id
        JOIN occupation oc  ON oc.id = ja.occupation_id
        GROUP BY oc.title
        ORDER BY avg_score ASC
        """;
        return jdbc.query(sql, Map.of("deptId", deptId), OCC_AVG_MAPPER);
    }

    /* ======================  OCCUPATION (within Dept)  ====================== */

    public long countTotalCandidatesByDeptOcc(int deptId, int occId) {
        String sql = """
        SELECT COUNT(c.id)
        FROM candidate c
        JOIN job_ad ja           ON c.job_ad_id = ja.id
        JOIN jobad_department jd ON ja.id = jd.jobad_id
        WHERE jd.department_id = :deptId AND ja.occupation_id = :occId
        """;
        Long n = jdbc.queryForObject(sql, Map.of("deptId", deptId, "occId", occId), Long.class);
        return n == null ? 0L : n;
    }

    public long countCandidatesByStatusDeptOcc(int deptId, int occId, String status) {
        String sql = """
        SELECT COUNT(c.id)
        FROM candidate c
        JOIN job_ad ja           ON c.job_ad_id = ja.id
        JOIN jobad_department jd ON ja.id = jd.jobad_id
        WHERE jd.department_id = :deptId AND ja.occupation_id = :occId AND c.status = :status
        """;
        Long n = jdbc.queryForObject(sql, Map.of("deptId", deptId, "occId", occId, "status", status), Long.class);
        return n == null ? 0L : n;
    }

    public Double avgCandidatesPerJobAdDeptOcc(int deptId, int occId) {
        String sql = """
        SELECT AVG(cnt) 
        FROM (
            SELECT ja.id, COUNT(c.id) AS cnt
            FROM job_ad ja
            JOIN jobad_department jd ON ja.id = jd.jobad_id
            LEFT JOIN candidate c     ON c.job_ad_id = ja.id
            WHERE jd.department_id = :deptId AND ja.occupation_id = :occId
            GROUP BY ja.id
        ) t
        """;
        Double v = jdbc.queryForObject(sql, Map.of("deptId", deptId, "occId", occId), Double.class);
        return v == null ? 0.0 : v;
    }

    /** Avg score ανά υποψήφιο (0..10) στο συγκεκριμένο dept+occupation */
    public List<Double> candidateAvgScoresByDeptOcc(int deptId, int occId) {
        String sql = """
        SELECT COALESCE(AVG(qs.score), 0) AS avg_score
        FROM candidate c
        JOIN job_ad ja           ON c.job_ad_id = ja.id
        JOIN jobad_department jd ON ja.id = jd.jobad_id
        LEFT JOIN interview_report ir ON ir.id = c.interview_report_id
        LEFT JOIN step_results sr     ON sr.interview_report_id = ir.id
        LEFT JOIN question_score qs   ON qs.step_results_id = sr.id
        WHERE jd.department_id = :deptId AND ja.occupation_id = :occId
        GROUP BY c.id
        """;
        return jdbc.query(sql, Map.of("deptId", deptId, "occId", occId),
                (rs, i) -> rs.getDouble("avg_score"));
    }

    /* ======================  JOB AD  ====================== */

    public long countTotalCandidatesByJobAd(int jobAdId) {
        String sql = """
        SELECT COUNT(c.id)
        FROM candidate c
        WHERE c.job_ad_id = :jobAdId
        """;
        Long n = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId), Long.class);
        return n == null ? 0L : n;
    }

    public long countCandidatesByStatusJobAd(int jobAdId, String status) {
        String sql = """
        SELECT COUNT(c.id)
        FROM candidate c
        WHERE c.job_ad_id = :jobAdId AND c.status = :status
        """;
        Long n = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId, "status", status), Long.class);
        return n == null ? 0L : n;
    }

    /** Προσλήψεις για συγκεκριμένη αγγελία (δείτε και τον κανόνα στο Org). */
    public long countHiresByJobAd(int jobAdId) {
        String sql = """
        SELECT COUNT(c.id)
        FROM candidate c
        JOIN job_ad ja ON c.job_ad_id = ja.id
        WHERE ja.id = :jobAdId
          AND c.status = 'Approved'
          AND ja.status = 'Complete'
        """;
        Long n = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId), Long.class);
        return n == null ? 0L : n;
    }

    /** Μέση συνολική βαθμολογία υποψηφίων της αγγελίας (μέση των μέσων). */
    public Double avgCandidateScoreByJobAd(int jobAdId) {
        String sql = """
        SELECT AVG(cavg.avg_score) AS overall_avg
        FROM (
            SELECT c.id AS cand_id, AVG(qs.score) AS avg_score
            FROM candidate c
            JOIN job_ad ja            ON c.job_ad_id = ja.id
            LEFT JOIN interview_report ir ON ir.id = c.interview_report_id
            LEFT JOIN step_results sr     ON sr.interview_report_id = ir.id
            LEFT JOIN question_score qs   ON qs.step_results_id = sr.id
            WHERE ja.id = :jobAdId
            GROUP BY c.id
        ) cavg
        """;
        Double v = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId), Double.class);
        return v == null ? 0.0 : v;
    }

    /** Κατανομή σκορ 0..100 (deciles 0..9) για την αγγελία. */
    public List<Map<String, Object>> scoreDistributionByJobAd(int jobAdId) {
        String sql = """
        SELECT bucket, COUNT(*) AS cnt
        FROM (
            SELECT LEAST(9, FLOOR(AVG(qs.score))) AS bucket
            FROM candidate c
            JOIN job_ad ja            ON c.job_ad_id = ja.id
            JOIN interview_report ir  ON ir.id = c.interview_report_id
            JOIN step_results sr      ON sr.interview_report_id = ir.id
            JOIN question_score qs    ON qs.step_results_id = sr.id
            WHERE ja.id = :jobAdId
            GROUP BY c.id
        ) t
        GROUP BY bucket
        ORDER BY bucket
        """;
        return jdbc.queryForList(sql, Map.of("jobAdId", jobAdId));
    }

    /** Δυσκολία ανά step (avg score per step) για την αγγελία. */
    public List<StepAvgDto> stepDifficultyByJobAd(int jobAdId) {
        String sql = """
        SELECT st.title AS step, AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN job_ad ja            ON c.job_ad_id = ja.id
        JOIN interview_report ir  ON ir.id = c.interview_report_id
        JOIN step_results sr      ON sr.interview_report_id = ir.id
        JOIN step st              ON st.id = sr.step_id
        JOIN question_score qs    ON qs.step_results_id = sr.id
        WHERE ja.id = :jobAdId
        GROUP BY st.title
        HAVING COUNT(qs.score) > 0
        ORDER BY st.title
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId), STEP_AVG_MAPPER);
    }

    /** Δυσκολία Ερωτήσεων (χαμηλότερο avg = δυσκολότερη). */
    public List<QuestionAvgDto> questionDifficultyByJobAd(int jobAdId) {
        String sql = """
        SELECT q.title AS question, AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN job_ad ja            ON c.job_ad_id = ja.id
        JOIN interview_report ir  ON ir.id = c.interview_report_id
        JOIN step_results sr      ON sr.interview_report_id = ir.id
        JOIN question_score qs    ON qs.step_results_id = sr.id
        JOIN question q           ON q.id = qs.question_id
        WHERE ja.id = :jobAdId
        GROUP BY q.title
        HAVING COUNT(qs.score) > 0
        ORDER BY avg_score ASC
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId), QUESTION_AVG_MAPPER);
    }

    /** Δυσκολία Δεξιοτήτων (χαμηλότερο avg = δυσκολότερη). */
    public List<SkillAvgDto> skillDifficultyByJobAd(int jobAdId) {
        String sql = """
        SELECT s.title AS skill, AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN job_ad ja            ON c.job_ad_id = ja.id
        JOIN interview_report ir  ON ir.id = c.interview_report_id
        JOIN step_results sr      ON sr.interview_report_id = ir.id
        JOIN question_score qs    ON qs.step_results_id = sr.id
        JOIN question q           ON q.id = qs.question_id
        JOIN question_skill qsk   ON qsk.question_id = q.id
        JOIN skill s              ON s.id = qsk.skill_id
        WHERE ja.id = :jobAdId
        GROUP BY s.title
        HAVING COUNT(qs.score) > 0
        ORDER BY avg_score ASC
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId), SKILL_AVG_MAPPER);
    }

    // ======================  CANDIDATES (jobAd level summary)  ======================

    public long countApprovedCandidatesByJobAd(int jobAdId) {
        String sql = """
        SELECT COUNT(c.id)
        FROM candidate c
        WHERE c.job_ad_id = :jobAdId AND c.status = 'Approved'
        """;
        Long n = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId), Long.class);
        return n == null ? 0L : n;
    }

    public long countPendingCandidatesByJobAd(int jobAdId) {
        String sql = """
        SELECT COUNT(c.id)
        FROM candidate c
        WHERE c.job_ad_id = :jobAdId AND c.status = 'Pending'
        """;
        Long n = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId), Long.class);
        return n == null ? 0L : n;
    }

// ======================  CANDIDATE (per candidate analytics)  ======================

    public Double candidateOverallScore(int candidateId) {
        String sql = """
        SELECT COALESCE(AVG(qs.score), 0) AS overall
        FROM candidate c
        LEFT JOIN interview_report ir ON ir.id = c.interview_report_id
        LEFT JOIN step_results sr     ON sr.interview_report_id = ir.id
        LEFT JOIN question_score qs   ON qs.step_results_id = sr.id
        WHERE c.id = :candId
        """;
        Double v = jdbc.queryForObject(sql, Map.of("candId", candidateId), Double.class);
        return v == null ? 0.0 : v;
    }

    private static final RowMapper<QuestionScoreDto> QUESTION_SCORE_MAPPER =
            (rs, i) -> new QuestionScoreDto(rs.getString("question"), rs.getDouble("score"));

    public List<QuestionScoreDto> candidateQuestionScores(int candidateId) {
        String sql = """
        SELECT q.title AS question, AVG(qs.score) AS score
        FROM candidate c
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        JOIN question q          ON q.id = qs.question_id
        WHERE c.id = :candId
        GROUP BY q.title
        HAVING COUNT(qs.score) > 0
        ORDER BY q.title
        """;
        return jdbc.query(sql, Map.of("candId", candidateId), QUESTION_SCORE_MAPPER);
    }

    public List<StepAvgDto> candidateStepScores(int candidateId) {
        String sql = """
        SELECT st.title AS step, AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN step st             ON st.id = sr.step_id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        WHERE c.id = :candId
        GROUP BY st.title
        HAVING COUNT(qs.score) > 0
        ORDER BY st.title
        """;
        return jdbc.query(sql, Map.of("candId", candidateId),
                (rs, i) -> new StepAvgDto(rs.getString("step"), rs.getDouble("avg_score")));
    }

    public List<SkillAvgDto> candidateSkillScores(int candidateId) {
        String sql = """
        SELECT s.title AS skill, AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        JOIN question q          ON q.id = qs.question_id
        JOIN question_skill qsk  ON qsk.question_id = q.id
        JOIN skill s             ON s.id = qsk.skill_id
        WHERE c.id = :candId
        GROUP BY s.title
        HAVING COUNT(qs.score) > 0
        ORDER BY s.title
        """;
        return jdbc.query(sql, Map.of("candId", candidateId),
                (rs, i) -> new SkillAvgDto(rs.getString("skill"), rs.getDouble("avg_score")));
    }

    /** σύνολο απαιτούμενων δεξιοτήτων (για την αγγελία του υποψηφίου) */
    public Integer requiredSkillCountForCandidateJobAd(int candidateId) {
        String sql = """
        SELECT COUNT(DISTINCT qsk.skill_id)
        FROM job_ad ja
        JOIN candidate c0       ON c0.job_ad_id = ja.id
        JOIN candidate c        ON c.job_ad_id = ja.id
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        JOIN question q          ON q.id = qs.question_id
        JOIN question_skill qsk  ON qsk.question_id = q.id
        WHERE c0.id = :candId     -- βρες την αγγελία του συγκεκριμένου υποψηφίου
        """;
        Integer v = jdbc.queryForObject(sql, Map.of("candId", candidateId), Integer.class);
        return v == null ? 0 : v;
    }

    /** πόσες απαιτούμενες δεξιότητες έχει "καλυμμένες" ο υποψήφιος (του έχουν βαθμολογηθεί) */
    public Integer coveredSkillCountForCandidate(int candidateId) {
        String sql = """
        SELECT COUNT(DISTINCT qsk.skill_id)
        FROM candidate c
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        JOIN question q          ON q.id = qs.question_id
        JOIN question_skill qsk  ON qsk.question_id = q.id
        WHERE c.id = :candId
        """;
        Integer v = jdbc.queryForObject(sql, Map.of("candId", candidateId), Integer.class);
        return v == null ? 0 : v;
    }

    /** σύνολο ερωτήσεων της αγγελίας (όπως προκύπτει από όσα έχουν βαθμολογηθεί σε αυτό το job ad) */
    public Integer totalQuestionCountForCandidateJobAd(int candidateId) {
        String sql = """
        SELECT COUNT(DISTINCT qs.question_id) AS total_q
        FROM candidate c0
        JOIN job_ad ja ON ja.id = c0.job_ad_id
        JOIN candidate c  ON c.job_ad_id = ja.id
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        WHERE c0.id = :candId
        """;
        Integer v = jdbc.queryForObject(sql, Map.of("candId", candidateId), Integer.class);
        return v == null ? 0 : v;
    }

    /** ερωτήσεις που έχει απαντήσει/βαθμολογηθεί ο συγκεκριμένος υποψήφιος */
    public Integer answeredQuestionCountForCandidate(int candidateId) {
        String sql = """
        SELECT COUNT(DISTINCT qs.question_id) AS ans_q
        FROM candidate c
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        WHERE c.id = :candId
        """;
        Integer v = jdbc.queryForObject(sql, Map.of("candId", candidateId), Integer.class);
        return v == null ? 0 : v;
    }

    public List<SkillAvgDto> topSkillsForCandidate(int candidateId, int limit) {
        String sql = """
        SELECT s.title AS skill, AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        JOIN question q          ON q.id = qs.question_id
        JOIN question_skill qsk  ON qsk.question_id = q.id
        JOIN skill s             ON s.id = qsk.skill_id
        WHERE c.id = :candId
        GROUP BY s.title
        HAVING COUNT(qs.score) > 0
        ORDER BY avg_score DESC
        LIMIT :limit
        """;
        return jdbc.query(sql, Map.of("candId", candidateId, "limit", limit),
                (rs, i) -> new SkillAvgDto(rs.getString("skill"), rs.getDouble("avg_score")));
    }

    public List<SkillAvgDto> weakSkillsForCandidate(int candidateId, int limit) {
        String sql = """
        SELECT s.title AS skill, AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        JOIN question q          ON q.id = qs.question_id
        JOIN question_skill qsk  ON qsk.question_id = q.id
        JOIN skill s             ON s.id = qsk.skill_id
        WHERE c.id = :candId
        GROUP BY s.title
        HAVING COUNT(qs.score) > 0
        ORDER BY avg_score ASC
        LIMIT :limit
        """;
        return jdbc.query(sql, Map.of("candId", candidateId, "limit", limit),
                (rs, i) -> new SkillAvgDto(rs.getString("skill"), rs.getDouble("avg_score")));
    }

    // ΛΙΣΤΑ ΥΠΟΨΗΦΙΩΝ ΓΙΑ JOB AD (id, fullName, status)
    public List<CandidateLiteDto> candidatesByJobAd(int jobAdId) {
        String sql = """
        SELECT *
        FROM candidate c
        WHERE c.job_ad_id = :jobAdId
        ORDER BY c.id
        """;

        return jdbc.query(sql, Map.of("jobAdId", jobAdId), (rs, i) -> {
            int id = rs.getInt("id");
            String status = getStringSafe(rs, "status");

            String fullName = firstNonBlank(
                    getStringSafe(rs, "full_name"),
                    getStringSafe(rs, "fullname"),
                    getStringSafe(rs, "name"),
                    joinNames(getStringSafe(rs, "first_name"), getStringSafe(rs, "last_name")),
                    joinNames(getStringSafe(rs, "firstName"), getStringSafe(rs, "lastName"))
            );

            if (fullName == null || fullName.isBlank()) {
                fullName = "Candidate " + id;
            }
            return new CandidateLiteDto(id, fullName, status);
        });
    }

    private static String getStringSafe(ResultSet rs, String col) {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }
    private static String firstNonBlank(String... vals) {
        if (vals == null) return null;
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }
    private static String joinNames(String a, String b) {
        String s = ((a == null ? "" : a.trim()) + " " + (b == null ? "" : b.trim())).trim();
        return s.isBlank() ? null : s;
    }

    // ======================  STEP analytics (per Job Ad & Step)  ======================

    /** Μέσος όρος όλων των scores του συγκεκριμένου step, για το συγκεκριμένο job ad. */
    public Double avgStepScoreForJobAdStep(int jobAdId, int stepId) {
        String sql = """
        SELECT AVG(qs.score) AS avg_step
        FROM candidate c
        JOIN job_ad ja           ON ja.id = c.job_ad_id
        JOIN interview_report ir  ON ir.id = c.interview_report_id
        JOIN step_results sr      ON sr.interview_report_id = ir.id
        JOIN question_score qs    ON qs.step_results_id = sr.id
        WHERE ja.id = :jobAdId AND sr.step_id = :stepId
        """;
        Double v = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId, "stepId", stepId), Double.class);
        return v == null ? 0.0 : v;
    }

    /** Μέσος όρος (για το συγκεκριμένο step) ανά υποψήφιο του job ad – για pass rate & histogram. */
    public List<Double> candidateStepAverages(int jobAdId, int stepId) {
        String sql = """
        SELECT AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN job_ad ja           ON ja.id = c.job_ad_id
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        WHERE ja.id = :jobAdId AND sr.step_id = :stepId
        GROUP BY c.id
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "stepId", stepId),
                (rs, i) -> rs.getDouble("avg_score"));
    }

    /** Κατάταξη ερωτήσεων του step (ευκολότερη -> δυσκολότερη). */
    public List<QuestionAvgDto> questionRankingByStep(int jobAdId, int stepId) {
        String sql = """
        SELECT q.title AS question, AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN job_ad ja           ON ja.id = c.job_ad_id
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        JOIN question q          ON q.id = qs.question_id
        WHERE ja.id = :jobAdId AND sr.step_id = :stepId
        GROUP BY q.title
        HAVING COUNT(qs.score) > 0
        ORDER BY avg_score DESC
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "stepId", stepId),
                (rs, i) -> new QuestionAvgDto(rs.getString("question"), rs.getDouble("avg_score")));
    }

    /** Κατάταξη δεξιοτήτων του step (ευκολότερη -> δυσκολότερη). */
    public List<SkillAvgDto> skillRankingByStep(int jobAdId, int stepId) {
        String sql = """
        SELECT s.title AS skill, AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN job_ad ja           ON ja.id = c.job_ad_id
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        JOIN question q          ON q.id = qs.question_id
        JOIN question_skill qsk  ON qsk.question_id = q.id
        JOIN skill s             ON s.id = qsk.skill_id
        WHERE ja.id = :jobAdId AND sr.step_id = :stepId
        GROUP BY s.title
        HAVING COUNT(qs.score) > 0
        ORDER BY avg_score DESC
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "stepId", stepId),
                (rs, i) -> new SkillAvgDto(rs.getString("skill"), rs.getDouble("avg_score")));
    }

    // ======================  STEPS (listing per Job Ad)  ======================
    public java.util.List<StepLiteDto> stepsForJobAd(int jobAdId) {
        String sql = """
        SELECT DISTINCT st.id, st.title
        FROM candidate c
        JOIN job_ad ja           ON ja.id = c.job_ad_id
        JOIN interview_report ir  ON ir.id = c.interview_report_id
        JOIN step_results sr      ON sr.interview_report_id = ir.id
        JOIN step st              ON st.id = sr.step_id
        WHERE ja.id = :jobAdId
        ORDER BY st.title
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId),
                (rs,i) -> new StepLiteDto(rs.getInt("id"), rs.getString("title")));
    }

    // AnalyticsRepository.java  (πρόσθεσε τα παρακάτω)

    /** Avg score της συγκεκριμένης ερώτησης στο context ενός job ad (0..10). */
    public Double avgScoreForQuestionInJobAd(int jobAdId, int questionId) {
        String sql = """
            SELECT AVG(qs.score) AS avg_q
            FROM candidate c
            JOIN job_ad ja           ON ja.id = c.job_ad_id
            JOIN interview_report ir ON ir.id = c.interview_report_id
            JOIN step_results sr     ON sr.interview_report_id = ir.id
            JOIN question_score qs   ON qs.step_results_id = sr.id
            WHERE ja.id = :jobAdId AND qs.question_id = :questionId
            """;
        Double v = jdbc.queryForObject(sql,
                Map.of("jobAdId", jobAdId, "questionId", questionId),
                Double.class);
        return v == null ? 0.0 : v;
    }

    /** Μέσος όρος της ερώτησης ανά υποψήφιο (για pass rate & histogram). */
    public List<Double> candidateQuestionAverages(int jobAdId, int questionId) {
        String sql = """
            SELECT AVG(qs.score) AS avg_score
            FROM candidate c
            JOIN job_ad ja           ON ja.id = c.job_ad_id
            JOIN interview_report ir ON ir.id = c.interview_report_id
            JOIN step_results sr     ON sr.interview_report_id = ir.id
            JOIN question_score qs   ON qs.step_results_id = sr.id
            WHERE ja.id = :jobAdId AND qs.question_id = :questionId
            GROUP BY c.id
            """;
        return jdbc.query(sql,
                Map.of("jobAdId", jobAdId, "questionId", questionId),
                (rs, i) -> rs.getDouble("avg_score"));
    }

    /** Μέσος όρος ανά skill για τη συγκεκριμένη ερώτηση μέσα στο job ad. */
    public List<SkillAvgDto> skillAveragesForQuestion(int jobAdId, int questionId) {
        String sql = """
            SELECT s.title AS skill, AVG(qs.score) AS avg_score
            FROM candidate c
            JOIN job_ad ja           ON ja.id = c.job_ad_id
            JOIN interview_report ir ON ir.id = c.interview_report_id
            JOIN step_results sr     ON sr.interview_report_id = ir.id
            JOIN question_score qs   ON qs.step_results_id = sr.id
            JOIN question_skill qsk  ON qsk.question_id = qs.question_id
            JOIN skill s             ON s.id = qsk.skill_id
            WHERE ja.id = :jobAdId AND qs.question_id = :questionId
            GROUP BY s.title
            HAVING COUNT(qs.score) > 0
            ORDER BY avg_score DESC
            """;
        return jdbc.query(sql,
                Map.of("jobAdId", jobAdId, "questionId", questionId),
                (rs, i) -> new SkillAvgDto(rs.getString("skill"), rs.getDouble("avg_score")));
    }

    // Ερωτήσεις που βαθμολογήθηκαν για το συγκεκριμένο JobAd+Step
    public java.util.List<QuestionLiteDto> questionsForJobAdStep(int jobAdId, int stepId) {
        String sql = """
        SELECT DISTINCT q.id, q.title
        FROM candidate c
        JOIN job_ad ja            ON ja.id = c.job_ad_id
        JOIN interview_report ir  ON ir.id = c.interview_report_id
        JOIN step_results sr      ON sr.interview_report_id = ir.id
        JOIN question_score qs    ON qs.step_results_id = sr.id
        JOIN question q           ON q.id = qs.question_id
        WHERE ja.id = :jobAdId AND sr.step_id = :stepId
        ORDER BY q.title
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "stepId", stepId),
                (rs, i) -> new QuestionLiteDto(rs.getInt("id"), rs.getString("title")));
    }

    // Λίστα δεξιοτήτων που είναι συνδεδεμένες με μια ερώτηση
    public java.util.List<SkillLiteDto> skillsForQuestion(int questionId) {
        String sql = """
        SELECT s.id, s.title
        FROM question_skill qsk
        JOIN skill s ON s.id = qsk.skill_id
        WHERE qsk.question_id = :qid
        ORDER BY s.title
        """;
        return jdbc.query(sql, Map.of("qid", questionId),
                (rs, i) -> new SkillLiteDto(rs.getInt("id"), rs.getString("title")));
    }

    // Μέσος όρος σκορ της δεξιότητας σε όλο το dataset (όλα τα question scores που την αφορούν)
    public Double avgScoreForSkill(int skillId) {
        String sql = """
        SELECT AVG(qs.score) AS avg_skill
        FROM question_score qs
        JOIN question q       ON q.id = qs.question_id
        JOIN question_skill k ON k.question_id = q.id
        WHERE k.skill_id = :skillId
        """;
        Double v = jdbc.queryForObject(sql, Map.of("skillId", skillId), Double.class);
        return v == null ? 0.0 : v;
    }

    // Μέσος όρος δεξιότητας ανά υποψήφιο (για pass rate & histogram)
    public java.util.List<Double> candidateSkillAverages(int skillId) {
        String sql = """
        SELECT AVG(qs.score) AS avg_score
        FROM candidate c
        JOIN interview_report ir ON ir.id = c.interview_report_id
        JOIN step_results sr     ON sr.interview_report_id = ir.id
        JOIN question_score qs   ON qs.step_results_id = sr.id
        JOIN question q          ON q.id = qs.question_id
        JOIN question_skill k    ON k.question_id = q.id
        WHERE k.skill_id = :skillId
        GROUP BY c.id
        """;
        return jdbc.query(sql, Map.of("skillId", skillId),
                (rs, i) -> rs.getDouble("avg_score"));
    }



}
