package com.example.hiringProcess.JobAd;

import com.example.hiringProcess.Interview.InterviewDetailsDTO;
import com.example.hiringProcess.Interview.InterviewService;
import com.example.hiringProcess.Organisation.OrganisationService;
import com.example.hiringProcess.Skill.SkillDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/jobAds")
public class JobAdController {

  private final JobAdService jobAdService;
    private final OrganisationService organizationService;
    private final InterviewService interviewService;

    @Autowired
    public JobAdController(JobAdService jobAdService, OrganisationService organizationService,
                           InterviewService interviewService) {
        this.organizationService = organizationService;
        this.jobAdService = jobAdService;
        this.interviewService = interviewService;
    }


    /* ===================== LIST / GET ONE ===================== */

    @GetMapping //OK
    public List<JobAdSummaryDTO> getJobAds(@RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        return jobAdService.getJobAdSummariesByOrg(orgId);
    }

    @GetMapping("/{jobAdId}") //OK
    public ResponseEntity<JobAd> getJobAd( @PathVariable Integer jobAdId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if (!jobAdService.existsByOrg(jobAdId, orgId)) {
            return ResponseEntity.notFound().build();
        }

        return jobAdService.getJobAdByOrg(orgId, jobAdId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());   
        }

    /* ===================== DETAILS (DTO) ===================== */

    // συμβατότητα με υπάρχον frontend: /jobAds/details?jobAdId=...
    @GetMapping("/details") //OK
    public ResponseEntity<JobAdDetailsDTO> getJobAdDetailsByQuery( @RequestParam("jobAdId") Integer jobAdId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

//        @PathVariable Integer orgId,
        if (!jobAdService.existsByOrg(jobAdId, orgId)) {
            return ResponseEntity.notFound().build();
        }

        return jobAdService.getJobAdDetails(jobAdId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // RESTful εναλλακτική: /{jobAdId}/details
    @GetMapping("/{jobAdId}/details") //OK
    public ResponseEntity<InterviewDetailsDTO> getJobAdDetailsByPath(@PathVariable Integer jobAdId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        if (!jobAdService.existsByOrg(jobAdId, orgId)) {
            return ResponseEntity.notFound().build();
        }

        InterviewDetailsDTO dto = interviewService.getInterviewDetailsByJobAd(jobAdId);
        return (dto != null) ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();

    }


    // Update μόνο description (skills πλέον ΔΕΝ αποθηκεύονται στο JobAd)
    @PutMapping("/{jobAdId}/details") //OK
    public ResponseEntity<Void> updateDetails( @PathVariable Integer jobAdId,
                                              @RequestBody JobAdUpdateDTO body, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);


        if (!jobAdService.existsByOrg(jobAdId, orgId)) {
            return ResponseEntity.notFound().build();
        }

        jobAdService.updateDetails(jobAdId, body);
        return ResponseEntity.noContent().build(); // 204
    }

    /* ===================== CREATE / UPDATE / DELETE ===================== */

    @PostMapping //OK
    public ResponseEntity<Void> addNewJobAd( @RequestBody JobAd jobAd, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        jobAdService.addNewJobAd(orgId, jobAd);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/by-names") //OK
    public ResponseEntity<JobAd> addNewJobAdByNames( @RequestBody JobAdCreateByNamesRequest req, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);

        JobAd created = jobAdService.addNewJobAdByNames(orgId, req);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{jobAdId}") // OK
    public void updateJobAd( @PathVariable Integer jobAdId, @RequestBody JobAd jobAd, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);


        if (!jobAdService.existsByOrg(jobAdId, orgId)) {
            return;
        }
        jobAdService.updateJobAd(jobAdId, jobAd);
    }

    @DeleteMapping("/{jobAdId}") //OK
    public ResponseEntity<Void> deleteJobAd( @PathVariable Integer jobAdId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);


        if (!jobAdService.existsByOrg(jobAdId, orgId)) {
            return ResponseEntity.notFound().build();
        }

        jobAdService.deleteJobAd(jobAdId);
        return ResponseEntity.noContent().build();
    }

    /* ===================== STATUS / PUBLISH ===================== */

    @PostMapping("/{jobAdId}/publish") //OK
    public ResponseEntity<Void> publish( @PathVariable Integer jobAdId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);


        if (!jobAdService.existsByOrg(jobAdId, orgId)) {
            return ResponseEntity.notFound().build();
        }

        jobAdService.publish(jobAdId);
        return ResponseEntity.noContent().build();
    }

    /* ===================== SKILLS (ΜΟΝΟ από interview) ===================== */

    @GetMapping("/{jobAdId}/interview-skills") //OK
    public ResponseEntity<List<SkillDTO>> getInterviewSkills( @PathVariable Integer jobAdId, @RequestHeader(value = "X-User-Organization", required = true) String headerOrgName) {
        Integer orgId = organizationService.getIdByName(headerOrgName);


        if (!jobAdService.existsByOrg(jobAdId, orgId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(jobAdService.getSkillsFromInterview(jobAdId));
    }
}
