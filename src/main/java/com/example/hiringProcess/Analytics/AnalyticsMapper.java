package com.example.hiringProcess.Analytics;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class AnalyticsMapper {

    private AnalyticsMapper() {}

    /* -------- helpers (ασφαλή gets) -------- */
    private static String s(ResultSet rs, String col) throws SQLException {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }
    private static Double d(ResultSet rs, String col) throws SQLException {
        try { Object o = rs.getObject(col); return (o == null) ? null : rs.getDouble(col); }
        catch (SQLException e) { return null; }
    }
    private static Integer i(ResultSet rs, String col) throws SQLException {
        try { Object o = rs.getObject(col); return (o == null) ? null : rs.getInt(col); }
        catch (SQLException e) { return null; }
    }
    private static Long l(ResultSet rs, String col) throws SQLException {
        try { Object o = rs.getObject(col); return (o == null) ? null : rs.getLong(col); }
        catch (SQLException e) { return null; }
    }

    /* -------- DTO mappers -------- */

    public static final RowMapper<SkillAvgDto> SKILL_AVG =
            (rs, n) -> new SkillAvgDto(
                    s(rs, "skill"),
                    d(rs, "avg_score") == null ? 0.0 : d(rs, "avg_score")
            );

    public static final RowMapper<StepAvgDto> STEP_AVG =
            (rs, n) -> new StepAvgDto(
                    s(rs, "step"),
                    d(rs, "avg_score") == null ? 0.0 : d(rs, "avg_score")
            );

    public static final RowMapper<QuestionAvgDto> QUESTION_AVG =
            (rs, n) -> new QuestionAvgDto(
                    s(rs, "question"),
                    d(rs, "avg_score") == null ? 0.0 : d(rs, "avg_score")
            );

    public static final RowMapper<QuestionScoreDto> QUESTION_SCORE =
            (rs, n) -> new QuestionScoreDto(
                    s(rs, "question"),
                    d(rs, "score") == null ? 0.0 : d(rs, "score")
            );

    public static final RowMapper<CandidateLiteDto> CANDIDATE_LITE =
            (rs, n) -> {
                int id = rs.getInt("id");
                String status = s(rs,"status");
                String fullName = firstNonBlank(
                        s(rs, "full_name"),
                        s(rs, "fullname"),
                        s(rs, "name"),
                        joinNames(s(rs, "first_name"), s(rs, "last_name")),
                        joinNames(s(rs, "firstName"), s(rs, "lastName"))
                );
                if (fullName == null || fullName.isBlank()) fullName = "Candidate " + id;
                return new CandidateLiteDto(id, fullName, status);
            };

    public static final RowMapper<BucketDto> BUCKET =
            (rs, n) -> new BucketDto(
                    i(rs, "from_val"),
                    i(rs, "to_val"),
                    l(rs, "cnt") == null ? 0L : l(rs, "cnt")
            );

    public static final RowMapper<JobAdAvgDto> JOBAD_AVG =
            (rs, n) -> new JobAdAvgDto(
                    s(rs, "job_ad"),
                    d(rs, "avg_score") == null ? 0.0 : d(rs, "avg_score")
            );

    public static final RowMapper<StepLiteDto> STEP_LITE =
            (rs, n) -> new StepLiteDto(
                    rs.getInt("id"),
                    s(rs, "title")
            );

    public static final RowMapper<QuestionLiteDto> QUESTION_LITE =
            (rs, n) -> new QuestionLiteDto(
                    rs.getInt("id"),
                    s(rs, "title")
            );

    public static final RowMapper<SkillLiteDto> SKILL_LITE =
            (rs, n) -> new SkillLiteDto(
                    rs.getInt("id"),
                    s(rs, "title")
            );

    /* -------- local helpers -------- */
    private static String firstNonBlank(String... vals) {
        if (vals == null) return null;
        for (String v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }
    private static String joinNames(String a, String b) {
        String s = ((a == null ? "" : a.trim()) + " " + (b == null ? "" : b.trim())).trim();
        return s.isBlank() ? null : s;
    }
}
