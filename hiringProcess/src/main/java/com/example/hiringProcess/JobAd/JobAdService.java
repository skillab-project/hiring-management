package com.example.hiringProcess.JobAd;

import com.example.hiringProcess.Department.Department;
import com.example.hiringProcess.Department.DepartmentRepository;
import com.example.hiringProcess.Interview.Interview;
import com.example.hiringProcess.Occupation.Occupation;
import com.example.hiringProcess.Occupation.OccupationRepository;
import com.example.hiringProcess.Skill.SkillDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobAdService {

    private final JobAdRepository jobAdRepository;
    private final JobAdMapper jobAdMapper;
    private final DepartmentRepository departmentRepository;
    private final OccupationRepository occupationRepository;

    @Autowired
    public JobAdService(JobAdRepository jobAdRepository,
                        JobAdMapper jobAdMapper,
                        DepartmentRepository departmentRepository,
                        OccupationRepository occupationRepository) {
        this.jobAdRepository = jobAdRepository;
        this.jobAdMapper = jobAdMapper;
        this.departmentRepository = departmentRepository;
        this.occupationRepository = occupationRepository;
    }

    public List<JobAd> getJobAds() {
        return jobAdRepository.findAll();
    }

    public Optional<JobAd> getJobAd(Integer jobAdId) {
        return jobAdRepository.findById(jobAdId);
    }

    public List<JobAdSummaryDTO> getJobAdSummaries() {
        return jobAdRepository.findAll().stream()
                .map(jobAdMapper::jobAdToSummaryDTO)
                .collect(Collectors.toList());
    }

    public Optional<JobAdDetailsDTO> getJobAdDetails(Integer jobAdId) {
        return jobAdRepository.findById(jobAdId)
                .map(jobAdMapper::jobAdToDetailsDTO);
    }

    @Transactional
    public void addNewJobAd(JobAd jobAd) {
        if (jobAd.getInterview() == null) {
            jobAd.setInterview(new Interview());
        }
        jobAdRepository.save(jobAd);
    }

    @Transactional
    public JobAd addNewJobAdByNames(JobAdCreateByNamesRequest req) {
        Department dept = departmentRepository.findByName(req.getDepartmentName())
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setName(req.getDepartmentName());
                    return departmentRepository.save(d);
                });

        Occupation occ = occupationRepository.findOccupationByTitle(req.getOccupationTitle())
                .orElseGet(() -> {
                    Occupation o = new Occupation();
                    o.setTitle(req.getOccupationTitle());
                    return occupationRepository.save(o);
                });

        JobAd ja = new JobAd();
        ja.setTitle(req.getTitle());
        ja.setDescription(req.getDescription());
        ja.setStatus(req.getStatus());
        ja.setPublishDate(req.getPublishDate());
        ja.setOccupation(occ);
        ja.setDepartments(Set.of(dept));
        ja.setInterview(new Interview()); // create empty interview

        return jobAdRepository.save(ja);
    }

    @Transactional
    public void deleteJobAd(Integer id) {
        JobAd ja = jobAdRepository.findById(id).orElseThrow();

        // 1) καθάρισε M2M
        if (ja.getDepartments() != null) ja.getDepartments().clear();

        // 2) σπάσε 1–1 ώστε να λειτουργήσει το orphanRemoval στο Interview
        if (ja.getInterview() != null) {
            ja.setInterview(null);
        }

        // 3) καθάρισε candidates (orphanRemoval)
        if (ja.getCandidates() != null) {
            ja.getCandidates().clear();
        }

        // 4) καθάρισε και από Occupation για να μην “κρατιέται” από τον parent
        if (ja.getOccupation() != null) {
            ja.setOccupation(null); // job_ad.occupation_id -> NULL πριν το delete
        }

        // 5) διαγραφή
        jobAdRepository.delete(ja);
    }

    @Transactional
    public void updateJobAd(Integer jobAdId, JobAd updatedJobAd) {
        updatedJobAd.setId(jobAdId);
        if (updatedJobAd.getInterview() == null) {
            updatedJobAd.setInterview(new Interview());
        }
        jobAdRepository.save(updatedJobAd);
    }

    @Transactional
    public JobAd updateDetails(Integer jobAdId, JobAdUpdateDTO dto) {
        JobAd ja = jobAdRepository.findById(jobAdId).orElseThrow();

        // ΜΟΝΟ description – skills πλέον ΔΕΝ αποθηκεύονται στο JobAd
        if (dto.getDescription() != null) {
            ja.setDescription(dto.getDescription());
        }
        return jobAdRepository.save(ja);
    }

    @Transactional
    public JobAd publish(Integer jobAdId) {
        JobAd ja = jobAdRepository.findById(jobAdId).orElseThrow();
        ja.setStatus("Published");
        if (ja.getPublishDate() == null) {
            ja.setPublishDate(LocalDate.now());
        }
        return jobAdRepository.save(ja);
    }

    public List<SkillDTO> getSkillsFromInterview(Integer jobAdId) {
        JobAd jobAd = jobAdRepository.findById(jobAdId)
                .orElseThrow(() -> new RuntimeException("JobAd not found"));

        Interview interview = jobAd.getInterview();
        if (interview == null) return Collections.emptyList();

        return interview.getSteps().stream()
                .flatMap(step -> step.getQuestions().stream())
                .flatMap(question -> question.getSkills().stream())
                .distinct()
                .map(skill -> new SkillDTO(skill.getId(), skill.getTitle()))
                .toList();
    }
}
