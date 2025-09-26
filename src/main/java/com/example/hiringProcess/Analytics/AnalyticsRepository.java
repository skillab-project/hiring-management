package com.example.hiringProcess.Analytics;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    // Μετρά ΟΛΟΥΣ τους υποψήφιους του οργανισμού orgId.
    public long countTotalCandidatesByOrg(int orgId) {
        String sql = """
    SELECT COUNT(c.id)
    FROM candidate c
    JOIN job_ad ja           ON ja.id = c.job_ad_id
    JOIN jobad_department jd ON jd.jobad_id = ja.id
    JOIN department d        ON d.id = jd.department_id
    WHERE d.organisation_id = :orgId
  """;
        Long n = jdbc.queryForObject(sql, Map.of("orgId", orgId), Long.class);
        return n == null ? 0L : n;
    }

    // Μετρά ΟΛΟΥΣ τους υποψήφιους ενός οργανισμού με συγκεκριμένο status
    // (όποιο status του δώσoυμε στο όρισμα status)
    public long countCandidatesByStatusOrg(int orgId, String status) {
        String sql = """
    SELECT COUNT(c.id)
    FROM candidate c
    JOIN job_ad ja           ON ja.id = c.job_ad_id
    JOIN jobad_department jd ON jd.jobad_id = ja.id
    JOIN department d        ON d.id = jd.department_id
    WHERE d.organisation_id = :orgId
      AND LOWER(TRIM(c.status)) = LOWER(:status)
  """;
        Long n = jdbc.queryForObject(sql, Map.of("orgId", orgId, "status", status), Long.class);
        return n == null ? 0L : n;
    }

    // Top (ίσο βάρος ανά candidate)
    public List<SkillAvgDto> topSkillsByOrg(int orgId, int limit) {
        String sql = """
    WITH scale AS (
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 1.0 ELSE 10.0 END AS sf100
    ),
    cand_skill AS (  -- μέσος ανά (candidate, skill)
      SELECT ss.candidate_id, ss.skill_id,
             AVG(ss.score) * (SELECT sf100 FROM scale) AS avg_per_candidate
      FROM skill_score ss
      JOIN candidate c         ON c.id = ss.candidate_id
      JOIN job_ad ja           ON ja.id = c.job_ad_id
      JOIN jobad_department jd ON jd.jobad_id = ja.id
      JOIN department d        ON d.id = jd.department_id
      WHERE d.organisation_id = :orgId
      GROUP BY ss.candidate_id, ss.skill_id
    )
    SELECT s.title AS skill, AVG(cs.avg_per_candidate) AS avg_score
    FROM cand_skill cs
    JOIN skill s ON s.id = cs.skill_id
    GROUP BY s.id, s.title
    ORDER BY avg_score DESC
    LIMIT :limit
    """;
        return jdbc.query(sql, Map.of("orgId", orgId, "limit", limit), AnalyticsMapper.SKILL_AVG);
    }

    // Weakest (ίσο βάρος ανά candidate)
    public List<SkillAvgDto> weakestSkillsByOrg(int orgId, int limit) {
        String sql = """
    WITH scale AS (
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 1.0 ELSE 10.0 END AS sf100
    ),
    cand_skill AS (  -- μέσος ανά (candidate, skill)
      SELECT ss.candidate_id, ss.skill_id,
             AVG(ss.score) * (SELECT sf100 FROM scale) AS avg_per_candidate
      FROM skill_score ss
      JOIN candidate c         ON c.id = ss.candidate_id
      JOIN job_ad ja           ON ja.id = c.job_ad_id
      JOIN jobad_department jd ON jd.jobad_id = ja.id
      JOIN department d        ON d.id = jd.department_id
      WHERE d.organisation_id = :orgId
      GROUP BY ss.candidate_id, ss.skill_id
    )
    SELECT s.title AS skill, AVG(cs.avg_per_candidate) AS avg_score
    FROM cand_skill cs
    JOIN skill s ON s.id = cs.skill_id
    GROUP BY s.id, s.title
    ORDER BY avg_score ASC
    LIMIT :limit
    """;
        return jdbc.query(sql, Map.of("orgId", orgId, "limit", limit), AnalyticsMapper.SKILL_AVG);
    }

    // Μέσοι όροι υποψηφίων (skills only) για ΟΛΟ τον οργανισμό, σε κλίμακα 0–10
    public List<Double> candidateAvgScoresByOrg(int orgId) {
        String sql = """
    WITH scale AS (
      -- Αν τα skill scores είναι 0..100 -> 0.1 για να πέσουμε σε 0..10, αλλιώς 1.0
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 0.1 ELSE 1.0 END AS f10
    )
    SELECT AVG(ss.score) * (SELECT f10 FROM scale) AS avg10
    FROM candidate c
    JOIN job_ad ja             ON ja.id = c.job_ad_id
    JOIN jobad_department jd   ON jd.jobad_id = ja.id
    JOIN department d          ON d.id = jd.department_id
    JOIN skill_score ss        ON ss.candidate_id = c.id
    WHERE d.organisation_id = :orgId
    GROUP BY c.id
    """;
        return jdbc.query(sql, Map.of("orgId", orgId), (rs, i) -> rs.getDouble("avg10"));
    }

    // Μέσος αριθμός υποψηφίων ανά Job Ad για έναν οργανισμό
    public double avgCandidatesPerJobAdOrg(int orgId) {
        String sql = """
    SELECT COALESCE(AVG(cnt), 0.0)
    FROM (
      SELECT ja.id, COUNT(c.id) AS cnt
      FROM job_ad ja
      JOIN jobad_department jd ON jd.jobad_id = ja.id
      JOIN department d        ON d.id = jd.department_id
      LEFT JOIN candidate c    ON c.job_ad_id = ja.id
      WHERE d.organisation_id = :orgId
      GROUP BY ja.id
    ) t
  """;
        Double v = jdbc.queryForObject(sql, Map.of("orgId", orgId), Double.class);
        return v == null ? 0.0 : v;
    }

    /* ======================  DEPARTMENT  ====================== */

    // Μετρά ΟΛΟΥΣ τους υποψήφιους του department deptId.
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

    // Μετρά ΟΛΟΥΣ τους υποψήφιους ενός department με συγκεκριμένο status (όποιο status του δώσoυμε στο όρισμα status)
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

    // Υπολογίζει τον μέσο αριθμό υποψηφίων ανά αγγελία εργασίας (job_ad) για το συγκεκριμένο department.
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

    // Επιστρέφει histogram κατανομής βαθμών (buckets 0–9) ανά department (0–100 ranges)
    public List<Double> candidateAvgScoresByDept(int deptId) {
        String sql = """
    WITH scale AS (
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 0.1 ELSE 1.0 END AS factor
    )
    SELECT AVG(ss.score) * (SELECT factor FROM scale) AS avg10
    FROM candidate c
    JOIN job_ad ja           ON ja.id = c.job_ad_id
    JOIN jobad_department jd ON jd.jobad_id = ja.id
    JOIN skill_score ss      ON ss.candidate_id = c.id
    WHERE jd.department_id = :deptId
    GROUP BY c.id
    """;
        return jdbc.query(sql, Map.of("deptId", deptId),
                (rs, i) -> rs.getDouble("avg10"));
    }


    // Υπολογίζει για το συγκεκριμένο department τον μέσο όρο σκορ ανά occupation,
    public List<OccupationAvgDto> occupationDifficultyByDept(int deptId) {
        String sql = """
    WITH scale AS (
      -- Αν τα skill_score είναι 0..100 -> 1.0, αλλιώς (0..10) -> 10.0
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 1.0 ELSE 10.0 END AS sf100
    ),
    -- Σχέση JobAd -> (question, skill)
    rel AS (
      SELECT ja.id AS job_ad_id, q.id AS qid, qsk.skill_id
      FROM job_ad ja
      JOIN jobad_department jd ON jd.jobad_id = ja.id
      JOIN interview i         ON i.id = ja.interview_id
      JOIN step st             ON st.interview_id = i.id
      JOIN question q          ON q.step_id = st.id
      JOIN question_skill qsk  ON qsk.question_id = q.id
      WHERE jd.department_id = :deptId
    ),
    -- Per-candidate per-jobad από skill_score μόνο για τα skills/ερωτήσεις του συγκεκριμένου JobAd
    cand_job AS (
      SELECT c.id AS cand_id, r.job_ad_id AS jid,
             AVG(ss.score) * (SELECT sf100 FROM scale) AS sc100
      FROM candidate c
      JOIN skill_score ss ON ss.candidate_id = c.id
      JOIN rel r          ON r.qid = ss.question_id AND r.skill_id = ss.skill_id
      WHERE c.job_ad_id = r.job_ad_id
      GROUP BY c.id, r.job_ad_id
    ),
    -- Μέσος ανά Job Ad (από τους υποψηφίους του)
    jobad_avg AS (
      SELECT jid AS job_ad_id, AVG(sc100) AS jobad_avg100
      FROM cand_job
      GROUP BY jid
    )
    -- Τελικό: μέσος των Job-Ad averages ανά occupation
    SELECT oc.title AS occupation, AVG(ja.jobad_avg100) AS avg_score
    FROM jobad_avg ja
    JOIN job_ad j        ON j.id = ja.job_ad_id
    JOIN occupation oc   ON oc.id = j.occupation_id
    JOIN jobad_department jd ON jd.jobad_id = j.id
    WHERE jd.department_id = :deptId
    GROUP BY oc.id, oc.title
    ORDER BY avg_score ASC
    """;
        return jdbc.query(sql, Map.of("deptId", deptId),
                (rs, i) -> new OccupationAvgDto(
                        rs.getString("occupation"),
                        rs.getObject("avg_score") == null ? 0.0 : rs.getDouble("avg_score")
                )
        );
    }

    /* ======================  OCCUPATION (within Dept)  ====================== */

    // Μετρά όλους τους υποψήφιους για το συγκεκριμένο department -> occupation
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

    // Μετρά τους υποψήφιους με συγκεκριμένο status στο συγκεκριμένο department & occupation.
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

    // Υπολογίζει τον Μ.Ο. υποψηφίων ανά αγγελία για το συγκεκριμένο department & occupation
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

    // Μ.Ο. τελικού score υποψηφίου σε ΚΛΙΜΑΚΑ 0–100 για dept+occupation (skills only)
    public List<Double> candidateAvgScoresByDeptOcc(int deptId, int occId) {
        String sql = """
    WITH scale AS (
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 1.0 ELSE 10.0 END AS sf100
    ),
    s AS (
      SELECT c.id AS cid, AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
      FROM candidate c
      JOIN job_ad ja           ON ja.id = c.job_ad_id
      JOIN jobad_department jd ON jd.jobad_id = ja.id
      JOIN skill_score ss      ON ss.candidate_id = c.id
      WHERE jd.department_id = :deptId AND ja.occupation_id = :occId
      GROUP BY c.id
    )
    SELECT avg100
    FROM s
    WHERE avg100 IS NOT NULL
    """;
        return jdbc.query(sql, Map.of("deptId", deptId, "occId", occId),
                (rs, i) -> rs.getDouble("avg100"));
    }

    // Λίστα με Μ.Ο. (0–100) υποψηφίων ανά αγγελία, ταξινομημένη αυξ.
    public List<JobAdAvgDto> jobAdDifficultyByDeptOcc(int deptId, int occId) {
        String sql = """
    WITH scale AS (
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 1.0 ELSE 10.0 END AS sf100
    ),
    cand_avg AS (
      SELECT
        c.id AS cand_id,
        c.job_ad_id,
        AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
      FROM candidate c
      JOIN job_ad ja           ON ja.id = c.job_ad_id
      JOIN jobad_department jd ON jd.jobad_id = ja.id
      JOIN skill_score ss      ON ss.candidate_id = c.id
      WHERE jd.department_id = :deptId AND ja.occupation_id = :occId
      GROUP BY c.id, c.job_ad_id
    )
    SELECT ja.title AS job_ad, AVG(ca.avg100) AS avg_score
    FROM cand_avg ca
    JOIN job_ad ja ON ja.id = ca.job_ad_id
    GROUP BY ja.id, ja.title
    ORDER BY avg_score ASC
    """;
        return jdbc.query(sql, Map.of("deptId", deptId, "occId", occId), AnalyticsMapper.JOBAD_AVG);
    }

    /* ======================  JOB AD  ====================== */

    // Μετρά όλους τους υποψήφιους για το συγκεκριμένο job ad
    public long countTotalCandidatesByJobAd(int jobAdId) {
        String sql = """
        SELECT COUNT(c.id)
        FROM candidate c
        WHERE c.job_ad_id = :jobAdId
        """;
        Long n = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId), Long.class);
        return n == null ? 0L : n;
    }

    // Κράτα αυτή τη μέθοδο αλλά κάν’ την case-insensitive
    public long countCandidatesByStatusJobAd(int jobAdId, String status) {
        String sql = """
        SELECT COUNT(c.id)
        FROM candidate c
        WHERE c.job_ad_id = :jobAdId
          AND LOWER(TRIM(c.status)) = LOWER(:status)
        """;
        Long n = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId, "status", status), Long.class);
        return n == null ? 0L : n;
    }

    // 1) Skills-only per candidate average (0–100)
    public List<Double> candidateAvgScoresByJobAd(int jobAdId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        )
        SELECT AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
        FROM candidate c
        LEFT JOIN skill_score ss ON ss.candidate_id = c.id
        WHERE c.job_ad_id = :jobAdId
        GROUP BY c.id
        HAVING COUNT(ss.score) > 0
    """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId), (rs, i) -> rs.getDouble("avg100"));
    }

    // 2) Bucketing 0–100 → 10 κάδοι με σωστά from/to
    public List<ScoreBucketDto> scoreDistributionFromScores(List<Double> scores) {
        long[] buckets = new long[10];
        for (Double s : scores) {
            if (s == null) continue;
            double v = Math.max(0, Math.min(100, s));
            int idx = (int)Math.floor(v / 10.0);
            if (idx > 9) idx = 9; // το 100 στο 90–100
            buckets[idx]++;
        }
        List<ScoreBucketDto> out = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            int from = i * 10;
            int to = (i == 9) ? 100 : (from + 9);
            out.add(new ScoreBucketDto(from, to, buckets[i]));
        }
        return out;
    }

    // 3) Χρησιμοποίησέ το στο service του JobAd
    public List<ScoreBucketDto> scoreDistributionByJobAd(int jobAdId) {
        List<Double> s = candidateAvgScoresByJobAd(jobAdId); // skills-only, 0–100
        return scoreDistributionFromScores(s);
    }

    // Υπολογίζει τον μέσο βαθμό ανά step της αγγελίας (χαμηλότερο = δυσκολότερο)
    public List<StepAvgDto> stepDifficultyByJobAd(int jobAdId) {
        String sql = """
        WITH scale AS (
          -- Αν οι βαθμοί είναι 0..100 -> 1.0, αλλιώς (0..10) -> 10.0
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        ),
        cand_q AS (
          SELECT
            st.id                 AS step_id,
            st.title              AS step,
            c.id                  AS cand_id,
            q.id                  AS question_id,
            AVG(ss.score) * (SELECT sf100 FROM scale) AS qscore100
          FROM candidate c
          JOIN job_ad ja            ON ja.id = c.job_ad_id
          JOIN step st              ON st.interview_id = ja.interview_id
          JOIN question q           ON q.step_id = st.id
          JOIN question_skill qsk   ON qsk.question_id = q.id
          JOIN skill_score ss       ON ss.candidate_id = c.id
                                   AND ss.skill_id     = qsk.skill_id
                                   AND ss.question_id  = q.id
          WHERE c.job_ad_id = :jobAdId
          GROUP BY st.id, st.title, c.id, q.id
        )
        SELECT step, AVG(qscore100) AS avg_score
        FROM cand_q
        GROUP BY step
        HAVING COUNT(qscore100) > 0
        ORDER BY step
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId), AnalyticsMapper.STEP_AVG);
    }

    // Υπολογίζει τον μέσο βαθμό ανά question της αγγελίας (χαμηλότερο = δυσκολότερο)
    public List<QuestionAvgDto> questionDifficultyByJobAd(int jobAdId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        ),
        cand_q AS (
          SELECT
            q.id    AS question_id,
            q.title AS question,
            c.id    AS cand_id,
            AVG(ss.score) * (SELECT sf100 FROM scale) AS qscore100
          FROM candidate c
          JOIN job_ad ja            ON ja.id = c.job_ad_id
          JOIN step st              ON st.interview_id = ja.interview_id
          JOIN question q           ON q.step_id = st.id
          JOIN question_skill qsk   ON qsk.question_id = q.id
          JOIN skill_score ss       ON ss.candidate_id = c.id
                                   AND ss.skill_id     = qsk.skill_id
                                   AND ss.question_id  = q.id
          WHERE c.job_ad_id = :jobAdId
          GROUP BY q.id, q.title, c.id
        )
        SELECT question, AVG(qscore100) AS avg_score
        FROM cand_q
        GROUP BY question
        HAVING COUNT(qscore100) > 0
        ORDER BY avg_score ASC, question
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId), AnalyticsMapper.QUESTION_AVG);
    }

    // Υπολογίζει τον μέσο βαθμό ανά skill της αγγελίας (χαμηλότερο = δυσκολότερο)
    public List<SkillAvgDto> skillDifficultyByJobAd(int jobAdId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        ),
        -- per-candidate per-skill (ίσο βάρος ανά υποψήφιο)
        cand_skill AS (
          SELECT
            qsk.skill_id,
            c.id AS cand_id,
            AVG(ss.score) * (SELECT sf100 FROM scale) AS score100
          FROM candidate c
          JOIN job_ad ja            ON ja.id = c.job_ad_id
          JOIN step st              ON st.interview_id = ja.interview_id
          JOIN question q           ON q.step_id = st.id
          JOIN question_skill qsk   ON qsk.question_id = q.id
          JOIN skill_score ss       ON ss.candidate_id = c.id
                                   AND ss.skill_id     = qsk.skill_id
                                   AND ss.question_id  = q.id
          WHERE c.job_ad_id = :jobAdId
          GROUP BY qsk.skill_id, c.id
        )
        SELECT sk.title AS skill, AVG(cs.score100) AS avg_score
        FROM cand_skill cs
        JOIN skill sk ON sk.id = cs.skill_id
        GROUP BY sk.id, sk.title
        ORDER BY avg_score ASC
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId), AnalyticsMapper.SKILL_AVG);
    }

    /* ======================  CANDIDATE  ====================== */

    // Επιστρέφει το συνολικό score ενός υποψηφίου
    public Double candidateOverallScore(int candidateId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        )
        SELECT COALESCE(AVG(ss.score) * (SELECT sf100 FROM scale), 0.0) AS overall
        FROM skill_score ss
        WHERE ss.candidate_id = :candId
    """;
        Double v = jdbc.queryForObject(sql, Map.of("candId", candidateId), Double.class);
        return v == null ? 0.0 : v;
    }

    // Επιστρέφει λίστα με μέσο όρο βαθμολογίας ανά ερώτηση για τον υποψήφιο
    public List<QuestionScoreDto> candidateQuestionScores(int candidateId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        )
        SELECT q.title AS question, AVG(ss.score) * (SELECT sf100 FROM scale) AS score
        FROM candidate c
        JOIN job_ad ja           ON ja.id = c.job_ad_id
        JOIN interview i         ON i.id = ja.interview_id
        JOIN step st             ON st.interview_id = i.id
        JOIN question q          ON q.step_id = st.id
        JOIN question_skill qsk  ON qsk.question_id = q.id
        JOIN skill_score ss      ON ss.candidate_id = c.id
                                 AND ss.skill_id     = qsk.skill_id
                                 AND ss.question_id  = q.id
        WHERE c.id = :candId
        GROUP BY q.title
        ORDER BY q.title
    """;
        return jdbc.query(sql, Map.of("candId", candidateId), AnalyticsMapper.QUESTION_SCORE);
    }

    // Επιστρέφει μέσο όρο βαθμολογίας ανά step για τον υποψήφιο
    public List<StepAvgDto> candidateStepScores(int candidateId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        ),
        q_avg AS (
          SELECT q.id AS qid, st.title AS step, AVG(ss.score) * (SELECT sf100 FROM scale) AS qscore
          FROM candidate c
          JOIN job_ad ja           ON ja.id = c.job_ad_id
          JOIN interview i         ON i.id = ja.interview_id
          JOIN step st             ON st.interview_id = i.id
          JOIN question q          ON q.step_id = st.id
          JOIN question_skill qsk  ON qsk.question_id = q.id
          JOIN skill_score ss      ON ss.candidate_id = c.id
                                   AND ss.skill_id     = qsk.skill_id
                                   AND ss.question_id  = q.id
          WHERE c.id = :candId
          GROUP BY q.id, st.title
        )
        SELECT step, AVG(qscore) AS avg_score
        FROM q_avg
        GROUP BY step
        ORDER BY step
    """;
        return jdbc.query(sql, Map.of("candId", candidateId), AnalyticsMapper.STEP_AVG);
    }

    // Επιστρέφει μέσο όρο βαθμολογίας ανά skill του υποψηφίου
    public List<SkillAvgDto> candidateSkillScores(int candidateId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        )
        SELECT s.title AS skill, AVG(ss.score) * (SELECT sf100 FROM scale) AS avg_score
        FROM skill_score ss
        JOIN skill s ON s.id = ss.skill_id
        WHERE ss.candidate_id = :candId
        GROUP BY s.title
        ORDER BY s.title
    """;
        return jdbc.query(sql, Map.of("candId", candidateId), AnalyticsMapper.SKILL_AVG);
    }

    // Επιστρέφει τα N ισχυρότερα skills του υποψηφίου
    public List<SkillAvgDto> topSkillsForCandidate(int candidateId, int limit) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        )
        SELECT s.title AS skill, AVG(ss.score) * (SELECT sf100 FROM scale) AS avg_score
        FROM skill_score ss
        JOIN skill s ON s.id = ss.skill_id
        WHERE ss.candidate_id = :candId
        GROUP BY s.title
        ORDER BY avg_score DESC
        LIMIT :limit
    """;
        return jdbc.query(sql, Map.of("candId", candidateId, "limit", limit), AnalyticsMapper.SKILL_AVG);
    }

    // Επιστρέφει τα N πιο αδύναμα skills του υποψηφίου
    public List<SkillAvgDto> weakSkillsForCandidate(int candidateId, int limit) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        )
        SELECT s.title AS skill, AVG(ss.score) * (SELECT sf100 FROM scale) AS avg_score
        FROM skill_score ss
        JOIN skill s ON s.id = ss.skill_id
        WHERE ss.candidate_id = :candId
        GROUP BY s.title
        ORDER BY avg_score ASC
        LIMIT :limit
    """;
        return jdbc.query(sql, Map.of("candId", candidateId, "limit", limit), AnalyticsMapper.SKILL_AVG);
    }

    // Επιστρέφει λίστα υποψηφίων για συγκεκριμένο Job Ad
    public List<CandidateLiteDto> candidatesByJobAd(int jobAdId) {
        String sql = """
        SELECT *
        FROM candidate c
        WHERE c.job_ad_id = :jobAdId
        ORDER BY c.id
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId), AnalyticsMapper.CANDIDATE_LITE);
    }

    // Παίρνει με ασφάλεια τιμή String από ResultSet για δεδομένη στήλη, επιστρέφοντας null σε SQLException.
    private static String getStringSafe(ResultSet rs, String col) {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }
    // Επιστρέφει την πρώτη μη-κενή/μη-λευκή συμβολοσειρά από τη λίστα τιμών.
    private static String firstNonBlank(String... vals) {
        if (vals == null) return null;
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }
    // Συνενώνει ονοματεπώνυμο (first + last) καθαρίζοντας κενά και επιστρέφει null αν το αποτέλεσμα είναι κενό.
    private static String joinNames(String a, String b) {
        String s = ((a == null ? "" : a.trim()) + " " + (b == null ? "" : b.trim())).trim();
        return s.isBlank() ? null : s;
    }

    /* ======================  STEPS  ====================== */

    // Επιστρέφει λίστα με όλα τα steps του Job Ad, με τη σειρά τους
    public List<StepLiteDto> stepsForJobAd(int jobAdId) {
        String sql = """
        SELECT st.id, st.title
        FROM job_ad ja
        JOIN interview i ON i.id = ja.interview_id
        JOIN step st     ON st.interview_id = i.id
        WHERE ja.id = :jobAdId
        ORDER BY COALESCE(st.position, 999), st.id
    """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId), AnalyticsMapper.STEP_LITE);
    }

    // Επιστρέφει τον μέσο όρο όλων των scores του συγκεκριμένου step, για το συγκεκριμένο job ad
    public Double avgStepScoreForJobAdStep(int jobAdId, int stepId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        ),
        step_questions AS (
          SELECT q.id AS qid
          FROM job_ad ja
          JOIN interview i ON i.id = ja.interview_id
          JOIN step st     ON st.interview_id = i.id AND st.id = :stepId
          JOIN question q  ON q.step_id = st.id
          WHERE ja.id = :jobAdId
        ),
        cand_avg AS (
          SELECT ss.candidate_id AS cid,
                 AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
          FROM skill_score ss
          JOIN step_questions sq ON sq.qid = ss.question_id
          JOIN candidate c        ON c.id = ss.candidate_id AND c.job_ad_id = :jobAdId
          GROUP BY ss.candidate_id
        )
        SELECT COALESCE(AVG(avg100), 0.0) AS avg_step FROM cand_avg
        """;
        Double v = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId, "stepId", stepId), Double.class);
        return v == null ? 0.0 : v;
    }

    // Επιστρέφει τον μέσο όρο (για το συγκεκριμένο step) ανά υποψήφιο του job ad – 0–100
    public List<Double> candidateStepAverages(int jobAdId, int stepId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        ),
        step_questions AS (
          SELECT q.id AS qid
          FROM job_ad ja
          JOIN interview i ON i.id = ja.interview_id
          JOIN step st     ON st.interview_id = i.id AND st.id = :stepId
          JOIN question q  ON q.step_id = st.id
          WHERE ja.id = :jobAdId
        ),
        cand_avg AS (
          SELECT ss.candidate_id AS cid,
                 AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
          FROM skill_score ss
          JOIN step_questions sq ON sq.qid = ss.question_id
          JOIN candidate c        ON c.id = ss.candidate_id AND c.job_ad_id = :jobAdId
          GROUP BY ss.candidate_id
        )
        SELECT avg100 FROM cand_avg
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "stepId", stepId),
                (rs, i) -> rs.getDouble("avg100"));
    }

    // Κατάταξη των questions του step (ευκολότερη -> δυσκολότερη) σε 0–100
    public List<QuestionAvgDto> questionRankingByStep(int jobAdId, int stepId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        )
        SELECT q.title AS question,
               AVG(ss.score) * (SELECT sf100 FROM scale) AS avg_score
        FROM job_ad ja
        JOIN interview i  ON i.id = ja.interview_id
        JOIN step st      ON st.interview_id = i.id AND st.id = :stepId
        JOIN question q   ON q.step_id = st.id
        JOIN question_skill qsk ON qsk.question_id = q.id
        JOIN candidate c  ON c.job_ad_id = ja.id
        JOIN skill_score ss ON ss.candidate_id = c.id
                           AND ss.skill_id     = qsk.skill_id
                           AND ss.question_id  = q.id
        WHERE ja.id = :jobAdId
        GROUP BY q.title
        ORDER BY avg_score DESC  -- μεγαλύτερος μέσος = ευκολότερη
    """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "stepId", stepId), AnalyticsMapper.QUESTION_AVG);
    }

    // Κατάταξη των skills του step (ευκολότερη -> δυσκολότερη) σε 0–100
    public List<SkillAvgDto> skillRankingByStep(int jobAdId, int stepId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        )
        SELECT s.title AS skill,
               AVG(ss.score) * (SELECT sf100 FROM scale) AS avg_score
        FROM job_ad ja
        JOIN interview i  ON i.id = ja.interview_id
        JOIN step st      ON st.interview_id = i.id AND st.id = :stepId
        JOIN question q   ON q.step_id = st.id
        JOIN question_skill qsk ON qsk.question_id = q.id
        JOIN skill s      ON s.id = qsk.skill_id
        JOIN candidate c  ON c.job_ad_id = ja.id
        JOIN skill_score ss ON ss.candidate_id = c.id
                           AND ss.skill_id     = qsk.skill_id
                           AND ss.question_id  = q.id
        WHERE ja.id = :jobAdId
        GROUP BY s.title
        ORDER BY avg_score DESC
    """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "stepId", stepId), AnalyticsMapper.SKILL_AVG);
    }

    // Buckets 0–9,10–19,...,90–100 (τελευταίος inclusive στο 100).
    public List<ScoreBucketDto> stepScoreDistribution(int jobAdId, int stepId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        ),
        step_questions AS (
          SELECT q.id AS qid
          FROM job_ad ja
          JOIN interview i ON i.id = ja.interview_id
          JOIN step st     ON st.interview_id = i.id AND st.id = :stepId
          JOIN question q  ON q.step_id = st.id
          WHERE ja.id = :jobAdId
        ),
        cand_avg AS (
          SELECT ss.candidate_id AS cid,
                 AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
          FROM skill_score ss
          JOIN step_questions sq ON sq.qid = ss.question_id
          JOIN candidate c        ON c.id = ss.candidate_id AND c.job_ad_id = :jobAdId
          GROUP BY ss.candidate_id
        ),
        buckets AS (
          SELECT 0 AS b UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
        )
        SELECT
          (b.b*10) AS from,
          CASE WHEN b.b=9 THEN 100 ELSE (b.b*10+9) END AS to,
          COUNT(ca.avg100) AS count
        FROM buckets b
        LEFT JOIN cand_avg ca
          ON (
               (b.b < 9  AND ca.avg100 BETWEEN b.b*10 AND b.b*10+9)
            OR (b.b = 9 AND ca.avg100 BETWEEN 90   AND 100)
          )
        GROUP BY b.b
        ORDER BY b.b
        """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "stepId", stepId),
                (rs, i) -> new ScoreBucketDto(rs.getInt("from"), rs.getInt("to"), rs.getLong("count")));
    }

    public long stepPassCount(int jobAdId, int stepId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        ),
        step_questions AS (
          SELECT q.id AS qid
          FROM job_ad ja
          JOIN interview i ON i.id = ja.interview_id
          JOIN step st     ON st.interview_id = i.id AND st.id = :stepId
          JOIN question q  ON q.step_id = st.id
          WHERE ja.id = :jobAdId
        ),
        cand_avg AS (
          SELECT ss.candidate_id AS cid,
                 AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
          FROM skill_score ss
          JOIN step_questions sq ON sq.qid = ss.question_id
          JOIN candidate c        ON c.id = ss.candidate_id AND c.job_ad_id = :jobAdId
          GROUP BY ss.candidate_id
        )
        SELECT COUNT(*) FROM cand_avg WHERE avg100 >= 50
        """;
        Long v = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId, "stepId", stepId), Long.class);
        return v == null ? 0L : v;
    }

    public double stepPassRate(int jobAdId, int stepId) {
        String sql = """
        WITH scale AS (
          SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                      THEN 1.0 ELSE 10.0 END AS sf100
        ),
        step_questions AS (
          SELECT q.id AS qid
          FROM job_ad ja
          JOIN interview i ON i.id = ja.interview_id
          JOIN step st     ON st.interview_id = i.id AND st.id = :stepId
          JOIN question q  ON q.step_id = st.id
          WHERE ja.id = :jobAdId
        ),
        cand_avg AS (
          SELECT ss.candidate_id AS cid,
                 AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
          FROM skill_score ss
          JOIN step_questions sq ON sq.qid = ss.question_id
          JOIN candidate c        ON c.id = ss.candidate_id AND c.job_ad_id = :jobAdId
          GROUP BY ss.candidate_id
        )
        SELECT CASE WHEN COUNT(*)=0 THEN 0.0
                    ELSE 100.0 * SUM(CASE WHEN avg100 >= 50 THEN 1 ELSE 0 END) / COUNT(*) END
        FROM cand_avg
        """;
        Double v = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId, "stepId", stepId), Double.class);
        return v == null ? 0.0 : v;
    }

    /* ======================  QUESTIONS  ====================== */

    // Επιστρέφει το μέσο score της συγκεκριμένης ερώτησης στο context ενός job ad (0–100),
    public Double avgScoreForQuestionInJobAd(int jobAdId, int questionId) {
        String sql = """
    WITH scale AS (
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 1.0 ELSE 10.0 END AS sf100
    ),
    cand_avg AS (   -- AVG ανά υποψήφιο για τη συγκεκριμένη ερώτηση
      SELECT ss.candidate_id AS cid,
             AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
      FROM candidate c
      JOIN job_ad ja           ON ja.id = c.job_ad_id
      JOIN question_skill qsk  ON qsk.question_id = :qid
      JOIN skill_score ss      ON ss.candidate_id = c.id
                              AND ss.skill_id     = qsk.skill_id
                              AND ss.question_id  = :qid
      WHERE ja.id = :jobAdId
      GROUP BY ss.candidate_id
    )
    SELECT COALESCE(AVG(avg100), 0.0) AS avg_q FROM cand_avg
    """;
        Double v = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId, "qid", questionId), Double.class);
        return v == null ? 0.0 : v;
    }

    //Επιστρέφει τον μέσο όρο της ερώτησης ανά υποψήφιο (0–100)
    public List<Double> candidateQuestionAverages(int jobAdId, int questionId) {
        String sql = """
    WITH scale AS (
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 1.0 ELSE 10.0 END AS sf100
    ),
    cand_avg AS (
      SELECT ss.candidate_id AS cid,
             AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
      FROM candidate c
      JOIN job_ad ja           ON ja.id = c.job_ad_id
      JOIN question_skill qsk  ON qsk.question_id = :qid
      JOIN skill_score ss      ON ss.candidate_id = c.id
                              AND ss.skill_id     = qsk.skill_id
                              AND ss.question_id  = :qid
      WHERE ja.id = :jobAdId
      GROUP BY ss.candidate_id
    )
    SELECT avg100 FROM cand_avg
    """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "qid", questionId),
                (rs, i) -> rs.getDouble("avg100"));
    }

    // Επιστρέφει Μ.Ο. ανά skill για την ερώτηση (0–100) σε αυτό το job ad.
    public List<SkillAvgDto> skillAveragesForQuestion(int jobAdId, int questionId) {
        String sql = """
    WITH scale AS (
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 1.0 ELSE 10.0 END AS sf100
    )
    SELECT s.title AS skill,
           AVG(ss.score) * (SELECT sf100 FROM scale) AS avg_score
    FROM job_ad ja
    JOIN candidate c        ON c.job_ad_id = ja.id
    JOIN question_skill qsk ON qsk.question_id = :qid
    JOIN skill s            ON s.id = qsk.skill_id
    JOIN skill_score ss     ON ss.candidate_id = c.id
                           AND ss.skill_id     = qsk.skill_id
                           AND ss.question_id  = :qid
    WHERE ja.id = :jobAdId
    GROUP BY s.id, s.title
    ORDER BY avg_score DESC
    """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "qid", questionId), AnalyticsMapper.SKILL_AVG);
    }

    // Buckets κατανομής 0–9,10–19,...,90–100 (τελευταίος inclusive στο 100) για τη συγκεκριμένη ερώτηση.
    public List<ScoreBucketDto> questionScoreDistribution(int jobAdId, int questionId) {
        String sql = """
    WITH scale AS (
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 1.0 ELSE 10.0 END AS sf100
    ),
    cand_avg AS (
      SELECT ss.candidate_id AS cid,
             AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
      FROM candidate c
      JOIN job_ad ja           ON ja.id = c.job_ad_id
      JOIN question_skill qsk  ON qsk.question_id = :qid
      JOIN skill_score ss      ON ss.candidate_id = c.id
                              AND ss.skill_id     = qsk.skill_id
                              AND ss.question_id  = :qid
      WHERE ja.id = :jobAdId
      GROUP BY ss.candidate_id
    ),
    buckets AS (
      SELECT 0 AS b UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
      UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
    )
    SELECT (b.b*10) AS "from",
           CASE WHEN b.b=9 THEN 100 ELSE (b.b*10+9) END AS "to",
           COUNT(ca.avg100) AS "count"
    FROM buckets b
    LEFT JOIN cand_avg ca
      ON (
           (b.b < 9  AND ca.avg100 BETWEEN b.b*10 AND b.b*10+9)
        OR (b.b = 9 AND ca.avg100 BETWEEN 90   AND 100)
      )
    GROUP BY b.b
    ORDER BY b.b
    """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "qid", questionId),
                (rs, i) -> new ScoreBucketDto(rs.getInt("from"), rs.getInt("to"), rs.getLong("count")));
    }

    // Πόσοι υποψήφιοι έχουν score ≥ 50% στη συγκεκριμένη ερώτηση (μόνο από skill_score).
    public long questionPassCount(int jobAdId, int questionId) {
        String sql = """
    WITH scale AS (
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 1.0 ELSE 10.0 END AS sf100
    ),
    cand_avg AS (
      SELECT ss.candidate_id AS cid,
             AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
      FROM candidate c
      JOIN job_ad ja           ON ja.id = c.job_ad_id
      JOIN question_skill qsk  ON qsk.question_id = :qid
      JOIN skill_score ss      ON ss.candidate_id = c.id
                              AND ss.skill_id     = qsk.skill_id
                              AND ss.question_id  = :qid
      WHERE ja.id = :jobAdId
      GROUP BY ss.candidate_id
    )
    SELECT COUNT(*) FROM cand_avg WHERE avg100 >= 50
    """;
        Long v = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId, "qid", questionId), Long.class);
        return v == null ? 0L : v;
    }

    // Ποσοστό υποψηφίων με score ≥ 50% στη συγκεκριμένη ερώτηση (0–100).
    public double questionPassRate(int jobAdId, int questionId) {
        String sql = """
    WITH scale AS (
      SELECT CASE WHEN (SELECT COALESCE(MAX(score),0) FROM skill_score) > 10
                  THEN 1.0 ELSE 10.0 END AS sf100
    ),
    cand_avg AS (
      SELECT ss.candidate_id AS cid,
             AVG(ss.score) * (SELECT sf100 FROM scale) AS avg100
      FROM candidate c
      JOIN job_ad ja           ON ja.id = c.job_ad_id
      JOIN question_skill qsk  ON qsk.question_id = :qid
      JOIN skill_score ss      ON ss.candidate_id = c.id
                              AND ss.skill_id     = qsk.skill_id
                              AND ss.question_id  = :qid
      WHERE ja.id = :jobAdId
      GROUP BY ss.candidate_id
    )
    SELECT CASE WHEN COUNT(*)=0 THEN 0.0
                ELSE 100.0 * SUM(CASE WHEN avg100 >= 50 THEN 1 ELSE 0 END) / COUNT(*) END
    FROM cand_avg
    """;
        Double v = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId, "qid", questionId), Double.class);
        return v == null ? 0.0 : v;
    }

    // Ερωτήσεις για συγκεκριμένο JobAd+Step, με σωστή σειρά.
    public List<QuestionLiteDto> questionsForJobAdStep(int jobAdId, int stepId) {
        String sql = """
    SELECT q.id, q.title
    FROM job_ad ja
    JOIN interview i ON i.id = ja.interview_id
    JOIN step st     ON st.interview_id = i.id AND st.id = :stepId
    JOIN question q  ON q.step_id = st.id
    WHERE ja.id = :jobAdId
    ORDER BY COALESCE(q.position, 999), q.id
    """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "stepId", stepId), AnalyticsMapper.QUESTION_LITE);
    }

    // Δεξιότητες που είναι δεμένες με συγκεκριμένη ερώτηση.
    public List<SkillLiteDto> skillsForQuestion(int questionId) {
        String sql = """
    SELECT s.id, s.title
    FROM question_skill qsk
    JOIN skill s ON s.id = qsk.skill_id
    WHERE qsk.question_id = :qid
    ORDER BY s.title
    """;
        return jdbc.query(sql, Map.of("qid", questionId), AnalyticsMapper.SKILL_LITE);
    }

    /* ======================  SKILLS  ====================== */

    // Επιστρέφει τον μέσο όρο του σκορ της δεξιότητας σε όλο το dataset (όλα τα question scores που την αφορούν)
    public Double avgScoreForSkill(int skillId) {
        String sql = """
    SELECT COALESCE(AVG(x.sc), 0.0) AS avg_skill
    FROM (
        -- QS που αφορούν τη δεξιότητα
        SELECT qs.score AS sc
        FROM question_score qs
        JOIN step_score sr        ON sr.id = qs.step_score_id
        JOIN interview_report ir  ON ir.id = sr.interview_report_id
        JOIN candidate c          ON c.interview_report_id = ir.id
        JOIN question_skill k     ON k.question_id = qs.question_id
        WHERE k.skill_id = :sid

        UNION ALL

        -- SS/10 ΜΟΝΟ όταν ΔΕΝ υπάρχει QS για το ίδιο (candidate, question)
        SELECT ss.score / 10.0 AS sc
        FROM skill_score ss
        JOIN question_skill k ON k.question_id = ss.question_id
        WHERE k.skill_id = :sid
          AND NOT EXISTS (
              SELECT 1
              FROM question_score qs
              JOIN step_score sr        ON sr.id = qs.step_score_id
              JOIN interview_report ir  ON ir.id = sr.interview_report_id
              JOIN candidate c          ON c.interview_report_id = ir.id
              WHERE qs.question_id = ss.question_id
                AND c.id          = ss.candidate_id
          )
    ) x
    """;
        Double v = jdbc.queryForObject(sql, Map.of("sid", skillId), Double.class);
        return v == null ? 0.0 : v;
    }

    // Επιστρέφει τον μέσο όρο της δεξιότητας ανά υποψήφιο (για pass rate & histogram)
    public java.util.List<Double> candidateSkillAverages(int skillId) {
        String sql = """
    WITH unioned AS (
        -- QS για το skill, με (candidate, question)
        SELECT c.id AS cid, qs.question_id AS qid, qs.score AS sc
        FROM question_score qs
        JOIN step_score sr        ON sr.id = qs.step_score_id
        JOIN interview_report ir  ON ir.id = sr.interview_report_id
        JOIN candidate c          ON c.interview_report_id = ir.id
        JOIN question_skill k     ON k.question_id = qs.question_id
        WHERE k.skill_id = :sid

        UNION ALL

        -- SS/10 ΜΟΝΟ όταν δεν υπάρχει QS για το ίδιο (cid,qid)
        SELECT ss.candidate_id AS cid, ss.question_id AS qid, ss.score/10.0 AS sc
        FROM skill_score ss
        JOIN question_skill k ON k.question_id = ss.question_id
        WHERE k.skill_id = :sid
          AND NOT EXISTS (
              SELECT 1
              FROM question_score qs
              JOIN step_score sr        ON sr.id = qs.step_score_id
              JOIN interview_report ir  ON ir.id = sr.interview_report_id
              JOIN candidate c          ON c.interview_report_id = ir.id
              WHERE qs.question_id = ss.question_id
                AND c.id          = ss.candidate_id
          )
    )
    SELECT AVG(sc) AS avg_per_candidate
    FROM unioned
    GROUP BY cid
    """;
        return jdbc.query(sql, Map.of("sid", skillId),
                (rs, i) -> rs.getObject(1) == null ? null : rs.getDouble(1));
    }

    // Μετρά προσλήψεις (Hired) ανά department
    public long countHiresByDept(int deptId) {
        String sql = """
        SELECT COUNT(DISTINCT c.id)
        FROM candidate c
        JOIN job_ad ja         ON c.job_ad_id = ja.id
        JOIN jobad_department jd ON ja.id = jd.jobad_id
        WHERE jd.department_id = :deptId
          AND c.status = 'Hired'
        """;
        Long n = jdbc.queryForObject(sql, Map.of("deptId", deptId), Long.class);
        return n == null ? 0L : n;
    }

    // Avg score για συγκεκριμένο skill ΜΕΣΑ σε (jobAd, question)
    public Double avgScoreForSkillInJobAdQuestion(int jobAdId, int questionId, int skillId) {
        String sql = """
        SELECT COALESCE(AVG(ss.score)/10.0, 0.0) AS avg10
        FROM skill_score ss
        JOIN candidate c ON c.id = ss.candidate_id
        WHERE c.job_ad_id = :jobAdId
          AND ss.question_id = :qid
          AND ss.skill_id = :sid
    """;
        Double v = jdbc.queryForObject(sql, Map.of("jobAdId", jobAdId, "qid", questionId, "sid", skillId), Double.class);
        return v == null ? 0.0 : v;
    }

    // Μ.Ο. ανά υποψήφιο (0–10) για το ίδιο context (για pass rate & histogram)
    public java.util.List<Double> candidateSkillAveragesInJobAdQuestion(int jobAdId, int questionId, int skillId) {
        String sql = """
        SELECT AVG(ss.score)/10.0 AS avg10
        FROM skill_score ss
        JOIN candidate c ON c.id = ss.candidate_id
        WHERE c.job_ad_id = :jobAdId
          AND ss.question_id = :qid
          AND ss.skill_id = :sid
        GROUP BY c.id
    """;
        return jdbc.query(sql, Map.of("jobAdId", jobAdId, "qid", questionId, "sid", skillId),
                (rs, i) -> rs.getObject(1) == null ? null : rs.getDouble(1));
    }

}

