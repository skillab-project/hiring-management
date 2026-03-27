package com.example.hiringProcess.Analytics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    private AnalyticsRepository repo;
    private AnalyticsService service;

    @BeforeEach
    void setUp() {
        repo = mock(AnalyticsRepository.class);
        service = new AnalyticsService(repo);
    }

    /* -------------------- Organization scope -------------------- */

    @Test
    void getOrganizationStats_shouldComputeRatesAndBuckets_0to10Scale() {
        int orgId = 1;

        when(repo.countTotalCandidatesByOrg(orgId)).thenReturn(10L);
        when(repo.countCandidatesByStatusOrg(orgId, "Approved")).thenReturn(3L);
        when(repo.countCandidatesByStatusOrg(orgId, "Rejected")).thenReturn(2L);
        when(repo.countCandidatesByStatusOrg(orgId, "Hired")).thenReturn(1L);

        when(repo.avgCandidatesPerJobAdOrg(orgId)).thenReturn(2.349); // -> 2.3

        when(repo.topSkillsByOrg(orgId, 5)).thenReturn(List.of());
        when(repo.weakestSkillsByOrg(orgId, 5)).thenReturn(List.of());

        // ⚠️ List.of() δεν δέχεται null -> Arrays.asList
        when(repo.candidateAvgScoresByOrg(orgId))
                .thenReturn(Arrays.asList(0.0, 0.1, 1.9, 5.0, 9.9, 10.0, null, -2.0, 12.0));

        OrganizationStatsDto dto = service.getOrganizationStats(orgId);

        assertEquals(30.0, dto.getApprovalRate(), 0.0001);
        assertEquals(20.0, dto.getRejectionRate(), 0.0001);
        assertEquals(10.0, dto.getHireRate(), 0.0001);

        assertEquals(1L, dto.getHireCount());
        assertEquals(10L, dto.getTotalCandidates());

        assertEquals(2.3, dto.getAvgCandidatesPerJobAd(), 0.0001);

        List<ScoreBucketDto> dist = dto.getScoreDistribution();
        assertEquals(10, dist.size());

        assertEquals(0, dist.get(0).getFrom());
        assertEquals(9, dist.get(0).getTo());
        assertEquals(3L, dist.get(0).getCount()); // 0.0,0.1,-2.0

        assertEquals(10, dist.get(1).getFrom());
        assertEquals(19, dist.get(1).getTo());
        assertEquals(1L, dist.get(1).getCount()); // 1.9

        assertEquals(50, dist.get(5).getFrom());
        assertEquals(59, dist.get(5).getTo());
        assertEquals(1L, dist.get(5).getCount()); // 5.0

        assertEquals(90, dist.get(9).getFrom());
        assertEquals(100, dist.get(9).getTo());
        assertEquals(3L, dist.get(9).getCount()); // 9.9, 10.0, 12.0

        verify(repo).topSkillsByOrg(orgId, 5);
        verify(repo).weakestSkillsByOrg(orgId, 5);
    }

    @Test
    void getOrganizationStats_totalZero_shouldReturnZeroRates() {
        int orgId = 7;

        when(repo.countTotalCandidatesByOrg(orgId)).thenReturn(0L);
        when(repo.countCandidatesByStatusOrg(orgId, "Approved")).thenReturn(0L);
        when(repo.countCandidatesByStatusOrg(orgId, "Rejected")).thenReturn(0L);
        when(repo.countCandidatesByStatusOrg(orgId, "Hired")).thenReturn(0L);

        when(repo.avgCandidatesPerJobAdOrg(orgId)).thenReturn(0.0);

        when(repo.topSkillsByOrg(orgId, 5)).thenReturn(List.of());
        when(repo.weakestSkillsByOrg(orgId, 5)).thenReturn(List.of());
        when(repo.candidateAvgScoresByOrg(orgId)).thenReturn(List.of());

        OrganizationStatsDto dto = service.getOrganizationStats(orgId);

        assertEquals(0.0, dto.getApprovalRate(), 0.0001);
        assertEquals(0.0, dto.getRejectionRate(), 0.0001);
        assertEquals(0.0, dto.getHireRate(), 0.0001);
        assertEquals(0L, dto.getTotalCandidates());
    }

    /* ------------------------ Job Ad scope ------------------------ */

    @Test
    void getJobAdStats_shouldDetect0to10ScaleAndConvertTo100Buckets() {
        int jobAdId = 11;

        when(repo.countTotalCandidatesByJobAd(jobAdId)).thenReturn(4L);
        when(repo.countCandidatesByStatusJobAd(jobAdId, "Approved")).thenReturn(2L);
        when(repo.countCandidatesByStatusJobAd(jobAdId, "Rejected")).thenReturn(1L);
        when(repo.countCandidatesByStatusJobAd(jobAdId, "Hired")).thenReturn(1L);

        when(repo.candidateAvgScoresByJobAd(jobAdId)).thenReturn(List.of(0.0, 5.0, 9.0, 10.0));

        when(repo.stepDifficultyByJobAd(jobAdId)).thenReturn(List.of());
        when(repo.questionDifficultyByJobAd(jobAdId)).thenReturn(List.of());
        when(repo.skillDifficultyByJobAd(jobAdId)).thenReturn(List.of());

        JobAdStatsDto dto = service.getJobAdStats(jobAdId);

        assertEquals(50.0, dto.getApprovalRate(), 0.0001);
        assertEquals(25.0, dto.getRejectionRate(), 0.0001);
        assertEquals(25.0, dto.getHireRate(), 0.0001);
        assertEquals(1L, dto.getHireCount());
        assertTrue(dto.isComplete());

        assertEquals(6.0, dto.getAvgCandidateScore(), 0.0001);

        List<ScoreBucketDto> dist = dto.getScoreDistribution();
        assertEquals(10, dist.size());

        assertEquals(1L, dist.get(0).getCount()); // 0
        assertEquals(1L, dist.get(5).getCount()); // 50

        // ✅ last bucket έχει 2: 90 και 100
        assertEquals(2L, dist.get(9).getCount());
    }

    @Test
    void getJobAdStats_shouldUseHundredScaleWhenMaxSeenGreaterThan10() {
        int jobAdId = 12;

        when(repo.countTotalCandidatesByJobAd(jobAdId)).thenReturn(3L);
        when(repo.countCandidatesByStatusJobAd(jobAdId, "Approved")).thenReturn(1L);
        when(repo.countCandidatesByStatusJobAd(jobAdId, "Rejected")).thenReturn(1L);
        when(repo.countCandidatesByStatusJobAd(jobAdId, "Hired")).thenReturn(0L);

        when(repo.candidateAvgScoresByJobAd(jobAdId)).thenReturn(List.of(10.0, 55.0, 80.0));

        when(repo.stepDifficultyByJobAd(jobAdId)).thenReturn(List.of());
        when(repo.questionDifficultyByJobAd(jobAdId)).thenReturn(List.of());
        when(repo.skillDifficultyByJobAd(jobAdId)).thenReturn(List.of());

        JobAdStatsDto dto = service.getJobAdStats(jobAdId);

        assertFalse(dto.isComplete());
        assertEquals(0L, dto.getHireCount());
        assertEquals(0.0, dto.getHireRate(), 0.0001);

        assertEquals(48.3, dto.getAvgCandidateScore(), 0.0001);

        List<ScoreBucketDto> dist = dto.getScoreDistribution();
        assertEquals(1L, dist.get(1).getCount()); // 10 -> 10-19
        assertEquals(1L, dist.get(5).getCount()); // 55 -> 50-59
        assertEquals(1L, dist.get(8).getCount()); // 80 -> 80-89
    }

    /* ------------------------ Step scope ------------------------ */

    @Test
    void getStepStats_shouldComputePassRateAndBucketsInclusive100() {
        int jobAdId = 5;
        int stepId = 2;

        when(repo.avgStepScoreForJobAdStep(jobAdId, stepId)).thenReturn(66.666); // -> 66.7

        // αν θες και null: Arrays.asList(..., null, ...)
        when(repo.candidateStepAverages(jobAdId, stepId))
                .thenReturn(Arrays.asList(49.9, 50.0, 100.0, -10.0, 10.0));

        when(repo.questionRankingByStep(jobAdId, stepId)).thenReturn(List.of());
        when(repo.skillRankingByStep(jobAdId, stepId)).thenReturn(List.of());

        StepStatsDto dto = service.getStepStats(jobAdId, stepId);

        // total = 5, passes = 2 -> 40.0
        assertEquals(40.0, dto.getPassRate(), 0.0001);
        assertEquals(2L, dto.getPassCount());

        assertEquals(66.7, dto.getAvgStepScore(), 0.0001);

        List<ScoreBucketDto> dist = dto.getScoreDistribution();
        assertEquals(1L, dist.get(0).getCount()); // -10 -> clamped 0
        assertEquals(1L, dist.get(1).getCount()); // 10
        assertEquals(1L, dist.get(4).getCount()); // 49.9
        assertEquals(1L, dist.get(5).getCount()); // 50
        assertEquals(1L, dist.get(9).getCount()); // 100
    }

    /* ------------------------ Skill scope ------------------------ */

    @Test
    void getSkillStats_shouldComputePassRateAndHistogramFrom0to10() {
        int skillId = 9;

        when(repo.avgScoreForSkill(skillId)).thenReturn(7.777); // -> 7.8

        // αν θες null: Arrays.asList(4.9, 5.0, 10.0, null)
        when(repo.candidateSkillAverages(skillId)).thenReturn(List.of(4.9, 5.0, 10.0));

        SkillStatsDto dto = service.getSkillStats(skillId);

        assertEquals(3L, dto.getTotalCount());
        assertEquals(2L, dto.getPassCount());
        assertEquals(66.7, dto.getPassRate(), 0.0001);

        assertEquals(7.8, dto.getAvgSkillScore(), 0.0001);

        List<ScoreBucketDto> dist = dto.getDistribution();
        assertEquals(1L, dist.get(4).getCount()); // 4.9 -> idx4
        assertEquals(1L, dist.get(5).getCount()); // 5.0 -> idx5
        assertEquals(1L, dist.get(9).getCount()); // 10.0 -> idx9
    }
}
