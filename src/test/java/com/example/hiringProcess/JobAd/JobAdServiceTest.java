package com.example.hiringProcess.JobAd;

import com.example.hiringProcess.Candidate.Candidate;
import com.example.hiringProcess.Department.Department;
import com.example.hiringProcess.Department.DepartmentRepository;
import com.example.hiringProcess.Interview.Interview;
import com.example.hiringProcess.Occupation.Occupation;
import com.example.hiringProcess.Occupation.OccupationRepository;
import com.example.hiringProcess.Skill.SkillDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobAdServiceTest {

    @Mock JobAdRepository jobAdRepository;
    @Mock JobAdMapper jobAdMapper;
    @Mock DepartmentRepository departmentRepository;
    @Mock OccupationRepository occupationRepository;

    @InjectMocks JobAdService jobAdService;

    // -------------------------
    // getJobAds / getJobAd
    // -------------------------

    @Test
    void getJobAds_returnsAllFromRepository() {
        List<JobAd> ads = List.of(new JobAd(), new JobAd());
        when(jobAdRepository.findAll()).thenReturn(ads);

        List<JobAd> result = jobAdService.getJobAds();

        assertSame(ads, result);
        verify(jobAdRepository).findAll();
    }

    @Test
    void getJobAd_returnsOptionalFromRepository() {
        JobAd ja = new JobAd();
        when(jobAdRepository.findById(7)).thenReturn(Optional.of(ja));

        Optional<JobAd> result = jobAdService.getJobAd(7);

        assertTrue(result.isPresent());
        assertSame(ja, result.get());
        verify(jobAdRepository).findById(7);
    }

    // -------------------------
    // getJobAdSummaries / getJobAdDetails
    // -------------------------

    @Test
    void getJobAdSummaries_mapsAllUsingMapper() {
        JobAd a = new JobAd();
        JobAd b = new JobAd();
        when(jobAdRepository.findAll()).thenReturn(List.of(a, b));

        JobAdSummaryDTO dtoA = mock(JobAdSummaryDTO.class);
        JobAdSummaryDTO dtoB = mock(JobAdSummaryDTO.class);
        when(jobAdMapper.jobAdToSummaryDTO(a)).thenReturn(dtoA);
        when(jobAdMapper.jobAdToSummaryDTO(b)).thenReturn(dtoB);

        List<JobAdSummaryDTO> result = jobAdService.getJobAdSummaries();

        assertEquals(2, result.size());
        assertSame(dtoA, result.get(0));
        assertSame(dtoB, result.get(1));
        verify(jobAdMapper).jobAdToSummaryDTO(a);
        verify(jobAdMapper).jobAdToSummaryDTO(b);
    }

    @Test
    void getJobAdDetails_whenNotFound_returnsEmpty() {
        when(jobAdRepository.findById(123)).thenReturn(Optional.empty());

        Optional<JobAdDetailsDTO> result = jobAdService.getJobAdDetails(123);

        assertTrue(result.isEmpty());
        verify(jobAdRepository).findById(123);
        verifyNoInteractions(jobAdMapper);
    }

    @Test
    void getJobAdDetails_whenFound_mapsToDetailsDTO() {
        JobAd ja = new JobAd();
        when(jobAdRepository.findById(10)).thenReturn(Optional.of(ja));

        JobAdDetailsDTO detailsDTO = mock(JobAdDetailsDTO.class);
        when(jobAdMapper.jobAdToDetailsDTO(ja)).thenReturn(detailsDTO);

        Optional<JobAdDetailsDTO> result = jobAdService.getJobAdDetails(10);

        assertTrue(result.isPresent());
        assertSame(detailsDTO, result.get());
        verify(jobAdMapper).jobAdToDetailsDTO(ja);
    }

    // -------------------------
    // addNewJobAd / updateJobAd / updateDetails / publish
    // -------------------------

    @Test
    void addNewJobAd_setsInterviewWhenNull() {
        JobAd ja = new JobAd();
        ja.setInterview(null);

        jobAdService.addNewJobAd(1, ja);

        assertNotNull(ja.getInterview(), "Interview should be set when null");
        verify(jobAdRepository).save(ja);
    }

    @Test
    void addNewJobAd_doesNotOverrideExistingInterview() {
        JobAd ja = new JobAd();
        Interview existing = new Interview();
        ja.setInterview(existing);

        jobAdService.addNewJobAd(1, ja);

        assertSame(existing, ja.getInterview());
        verify(jobAdRepository).save(ja);
    }

    @Test
    void updateJobAd_setsIdAndInterviewWhenNull() {
        JobAd updated = new JobAd();
        updated.setInterview(null);

        jobAdService.updateJobAd(55, updated);

        assertEquals(55, updated.getId());
        assertNotNull(updated.getInterview());
        verify(jobAdRepository).save(updated);
    }

    @Test
    void updateDetails_updatesDescriptionOnlyWhenProvided() {
        JobAd ja = new JobAd();
        ja.setDescription("old");

        JobAdUpdateDTO dto = new JobAdUpdateDTO();
        dto.setDescription("new");

        when(jobAdRepository.findById(1)).thenReturn(Optional.of(ja));
        when(jobAdRepository.save(any(JobAd.class))).thenAnswer(inv -> inv.getArgument(0));

        JobAd saved = jobAdService.updateDetails(1, dto);

        assertEquals("new", saved.getDescription());
        verify(jobAdRepository).save(ja);
    }

    @Test
    void updateDetails_whenDescriptionNull_doesNotChange() {
        JobAd ja = new JobAd();
        ja.setDescription("old");

        JobAdUpdateDTO dto = new JobAdUpdateDTO();
        dto.setDescription(null);

        when(jobAdRepository.findById(1)).thenReturn(Optional.of(ja));
        when(jobAdRepository.save(any(JobAd.class))).thenAnswer(inv -> inv.getArgument(0));

        JobAd saved = jobAdService.updateDetails(1, dto);

        assertEquals("old", saved.getDescription());
        verify(jobAdRepository).save(ja);
    }

    @Test
    void publish_setsStatusAndDateIfMissing() {
        JobAd ja = new JobAd();
        ja.setStatus("Draft");
        ja.setPublishDate(null);

        when(jobAdRepository.findById(9)).thenReturn(Optional.of(ja));
        when(jobAdRepository.save(any(JobAd.class))).thenAnswer(inv -> inv.getArgument(0));

        JobAd saved = jobAdService.publish(9);

        assertEquals("Published", saved.getStatus());
        assertNotNull(saved.getPublishDate(), "publishDate should be set when missing");
        verify(jobAdRepository).save(ja);
    }

    @Test
    void publish_doesNotOverrideExistingPublishDate() {
        JobAd ja = new JobAd();
        LocalDate existingDate = LocalDate.of(2020, 1, 1);
        ja.setPublishDate(existingDate);

        when(jobAdRepository.findById(9)).thenReturn(Optional.of(ja));
        when(jobAdRepository.save(any(JobAd.class))).thenAnswer(inv -> inv.getArgument(0));

        JobAd saved = jobAdService.publish(9);

        assertEquals("Published", saved.getStatus());
        assertEquals(existingDate, saved.getPublishDate(), "existing publishDate must remain unchanged");
        verify(jobAdRepository).save(ja);
    }

//    // -------------------------
//    // addNewJobAdByNames
//    // -------------------------
//
//    @Test
//    void addNewJobAdByNames_whenDeptAndOccExist_doesNotCreateNewOnes() {
//        Department dept = new Department();
//        dept.setName("IT");
//        when(departmentRepository.findByName("IT")).thenReturn(Optional.of(dept));
//
//        Occupation occ = new Occupation();
//        occ.setTitle("Developer");
//        when(occupationRepository.findOccupationByTitle("Developer")).thenReturn(Optional.of(occ));
//
//        JobAdCreateByNamesRequest req = new JobAdCreateByNamesRequest();
//        req.setDepartmentName("IT");
//        req.setOccupationTitle("Developer");
//        req.setTitle("Java Dev");
//        req.setDescription("Desc");
//        req.setStatus("Draft");
//        req.setPublishDate(LocalDate.of(2024, 1, 1));
//
//        // capture what gets saved
//        ArgumentCaptor<JobAd> captor = ArgumentCaptor.forClass(JobAd.class);
//        when(jobAdRepository.save(any(JobAd.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        JobAd saved = jobAdService.addNewJobAdByNames(1, req);
//
//        verify(departmentRepository, never()).save(any(Department.class));
//        verify(occupationRepository, never()).save(any(Occupation.class));
//
//        verify(jobAdRepository).save(captor.capture());
//        JobAd toSave = captor.getValue();
//
//        assertEquals("Java Dev", toSave.getTitle());
//        assertEquals("Desc", toSave.getDescription());
//        assertEquals("Draft", toSave.getStatus());
//        assertEquals(LocalDate.of(2024, 1, 1), toSave.getPublishDate());
//
//        assertSame(occ, toSave.getOccupation());
//        assertNotNull(toSave.getDepartments());
//        assertTrue(toSave.getDepartments().contains(dept));
//
//        assertNotNull(toSave.getInterview(), "Interview must be created");
//        assertSame(toSave, saved);
//    }
//
//    @Test
//    void addNewJobAdByNames_whenDeptMissing_createsAndSavesDept() {
//        when(departmentRepository.findByName("HR")).thenReturn(Optional.empty());
//
//        Department savedDept = new Department();
//        savedDept.setName("HR");
//        when(departmentRepository.save(any(Department.class))).thenReturn(savedDept);
//
//        Occupation occ = new Occupation();
//        occ.setTitle("Recruiter");
//        when(occupationRepository.findOccupationByTitle("Recruiter")).thenReturn(Optional.of(occ));
//
//        JobAdCreateByNamesRequest req = new JobAdCreateByNamesRequest();
//        req.setDepartmentName("HR");
//        req.setOccupationTitle("Recruiter");
//        req.setTitle("Junior Recruiter");
//        req.setDescription("Desc");
//        req.setStatus("Draft");
//
//        when(jobAdRepository.save(any(JobAd.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        JobAd result = jobAdService.addNewJobAdByNames(1, req);
//
//        verify(departmentRepository).save(any(Department.class));
//        verify(jobAdRepository).save(any(JobAd.class));
//
//        assertNotNull(result.getDepartments());
//        assertTrue(result.getDepartments().contains(savedDept));
//    }
//
//    @Test
//    void addNewJobAdByNames_whenOccMissing_createsAndSavesOccupation() {
//        Department dept = new Department();
//        dept.setName("Sales");
//        when(departmentRepository.findByName("Sales")).thenReturn(Optional.of(dept));
//
//        when(occupationRepository.findOccupationByTitle("Salesman")).thenReturn(Optional.empty());
//        Occupation savedOcc = new Occupation();
//        savedOcc.setTitle("Salesman");
//        when(occupationRepository.save(any(Occupation.class))).thenReturn(savedOcc);
//
//        JobAdCreateByNamesRequest req = new JobAdCreateByNamesRequest();
//        req.setDepartmentName("Sales");
//        req.setOccupationTitle("Salesman");
//        req.setTitle("Sales role");
//        req.setDescription("Desc");
//        req.setStatus("Draft");
//
//        when(jobAdRepository.save(any(JobAd.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        JobAd result = jobAdService.addNewJobAdByNames(1, req);
//
//        verify(occupationRepository).save(any(Occupation.class));
//        assertSame(savedOcc, result.getOccupation());
//        assertTrue(result.getDepartments().contains(dept));
//    }

    // -------------------------
    // deleteJobAd
    // -------------------------

    @Test
    void deleteJobAd_clearsRelationsAndDeletes() {
        JobAd ja = new JobAd();

        // departments
        Department d = new Department();
        Set<Department> depts = new HashSet<>();
        depts.add(d);
        ja.setDepartments(depts);

        // interview
        ja.setInterview(new Interview());

        // occupation
        Occupation occ = new Occupation();
        ja.setOccupation(occ);

        // candidates: αν το entity σου έχει Set<Candidate> με setter, αυτό δουλεύει.
        // Αν ΔΕΝ έχει setter, απλά αφαίρεσε αυτές τις 2 γραμμές (το υπόλοιπο test μένει ίδιο).
        try {
            ja.setCandidates(new ArrayList<Candidate>());
        } catch (Throwable ignored) {
            // some projects don't expose setter; then we just skip candidates part
        }

        when(jobAdRepository.findById(77)).thenReturn(Optional.of(ja));

        jobAdService.deleteJobAd(77);

        assertTrue(ja.getDepartments() == null || ja.getDepartments().isEmpty());
        assertNull(ja.getInterview());
        assertNull(ja.getOccupation());

        verify(jobAdRepository).delete(ja);
    }

    @Test
    void deleteJobAd_whenNotFound_throws() {
        when(jobAdRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> jobAdService.deleteJobAd(999));
        verify(jobAdRepository, never()).delete(any());
    }

    // -------------------------
    // getSkillsFromInterview
    // -------------------------
    // ⚠️ Εδώ ίσως χρειαστεί να διορθώσεις imports για Step/Question/Skill
    // ανάλογα με τα packages στο project σου.

    @Test
    void getSkillsFromInterview_whenInterviewNull_returnsEmptyList() {
        JobAd ja = new JobAd();
        ja.setInterview(null);

        when(jobAdRepository.findById(5)).thenReturn(Optional.of(ja));

        List<SkillDTO> result = jobAdService.getSkillsFromInterview(5);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getSkillsFromInterview_whenNotFound_throwsRuntimeException() {
        when(jobAdRepository.findById(5)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> jobAdService.getSkillsFromInterview(5));
        assertTrue(ex.getMessage().toLowerCase().contains("jobad"));
    }

    // Αν θες DISTINCT + mapping test:
    // Θα χρειαστεί να φέρεις τους σωστούς τύπους (Step, Question, Skill) με auto-import.
    //
    // Ξε-σχολίασε το παρακάτω test αφού το IntelliJ σου κάνει resolve τα imports.

    /*
    @Test
    void getSkillsFromInterview_flattensDistinctAndMapsToDTO() {
        // Arrange: mock nested interview structure
        JobAd ja = new JobAd();

        Interview interview = mock(Interview.class);

        // άλλαξε αυτά τα types με τα πραγματικά σου:
        Step step = mock(Step.class);
        Question q1 = mock(Question.class);
        Question q2 = mock(Question.class);
        Skill s1 = mock(Skill.class);
        Skill s1dup = s1; // same instance to test distinct
        Skill s2 = mock(Skill.class);

        when(s1.getId()).thenReturn(1);
        when(s1.getTitle()).thenReturn("Java");
        when(s2.getId()).thenReturn(2);
        when(s2.getTitle()).thenReturn("SQL");

        when(q1.getSkills()).thenReturn(List.of(s1, s2));
        when(q2.getSkills()).thenReturn(List.of(s1dup));

        when(step.getQuestions()).thenReturn(List.of(q1, q2));
        when(interview.getSteps()).thenReturn(List.of(step));

        ja.setInterview(interview);
        when(jobAdRepository.findById(1)).thenReturn(Optional.of(ja));

        // Act
        List<SkillDTO> result = jobAdService.getSkillsFromInterview(1);

        // Assert
        assertEquals(2, result.size()); // distinct
        assertTrue(result.stream().anyMatch(x -> x.getId() == 1 && x.getTitle().equals("Java")));
        assertTrue(result.stream().anyMatch(x -> x.getId() == 2 && x.getTitle().equals("SQL")));
    }
    */
}
