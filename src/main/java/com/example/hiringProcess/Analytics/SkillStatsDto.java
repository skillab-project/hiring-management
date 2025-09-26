package com.example.hiringProcess.Analytics;

import java.util.List;


public class SkillStatsDto {

    /** Μέση βαθμολογία δεξιότητας (0..10). */
    private double avgSkillScore;

    /** Ποσοστό περασμένων (0..100). */
    private double passRate;

    /** Απόλυτος αριθμός περασμένων (avg >= 5.0). */
    private long passCount;

    /** Συνολικοί υποψήφιοι που βαθμολογήθηκαν στο context. */
    private long totalCount;

    /** Ιστόγραμμα 0–100 (ανά 10άδες). */
    private List<ScoreBucketDto> distribution;

    public SkillStatsDto() {
    }

    /** Πλήρης constructor. */
    public SkillStatsDto(double avgSkillScore,
                         double passRate,
                         long passCount,
                         long totalCount,
                         List<ScoreBucketDto> distribution) {
        this.avgSkillScore = avgSkillScore;
        this.passRate = passRate;
        this.passCount = passCount;
        this.totalCount = totalCount;
        this.distribution = distribution;
    }

    /** Constructor για συμβατότητα με παλιό κώδικα (χωρίς counts). */
    public SkillStatsDto(double avgSkillScore,
                         double passRate,
                         List<ScoreBucketDto> distribution) {
        this(avgSkillScore, passRate, 0L, 0L, distribution);
    }

    // -------- Getters / Setters --------
    public double getAvgSkillScore() {
        return avgSkillScore;
    }

    public void setAvgSkillScore(double avgSkillScore) {
        this.avgSkillScore = avgSkillScore;
    }

    public double getPassRate() {
        return passRate;
    }

    public void setPassRate(double passRate) {
        this.passRate = passRate;
    }

    public long getPassCount() {
        return passCount;
    }

    public void setPassCount(long passCount) {
        this.passCount = passCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<ScoreBucketDto> getDistribution() {
        return distribution;
    }

    public void setDistribution(List<ScoreBucketDto> distribution) {
        this.distribution = distribution;
    }
}
