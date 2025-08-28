package com.example.hiringProcess.JobAd;

import com.example.hiringProcess.Department.Department;
import com.example.hiringProcess.Interview.Interview;
import com.example.hiringProcess.Organisation.Organisation;
import com.example.hiringProcess.Question.Question;
import com.example.hiringProcess.Step.Step;
import com.example.hiringProcess.Skill.Skill;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;


@Configuration
public class JobAdConfig {
    @Bean(name = "jobAdCommandLineRunner")
    CommandLineRunner commandLineRunner(JobAdRepository repository) {
        return args -> {
            System.out.println("Saving jobAds...");

//            Interview interview = new Interview();
//            Organisation org = new Organisation("Hype" ,"A good Organisation");
//            Department dep = new Department("Software Development" ,"Abu Dhabi","the best department");
//
//            JobAd jobAd1= new JobAd("title1","description1" , LocalDate.of(2012, 12, 12) ,"waiting", interview);
//
//            Step step1 = new Step("Technical Interview", "desc1");
//            Step step2 = new Step("HR Interview", "desc2");
//
//            // Πρόσθεσε τα steps στο interview
//            interview.addStep(step1);
//            interview.addStep(step2);
//
//            // Δημιουργία ερωτήσεων
//            Question q1 = new Question("What is polymorphism?");
//            Question q2 = new Question("Explain dependency injection.");
//            Question q3 = new Question("What is the difference between abstract classes and interfaces?");
//
//            // Δημιουργία skills
//            Skill s1 = new Skill("Polymorphism");
//            Skill s2 = new Skill("Dependency injection");
//            Skill s3 = new Skill("Difference between abstract classes and interfaces");
//
//            Skill s5 = new Skill("Polymorphism");
//            Skill s6 = new Skill("Dependency injection");
//            Skill s7 = new Skill("Difference between abstract classes and interfaces");


            //Δημιοργία score για τα skills
            //Cand_Score score1 = new Cand_Score(20);
            //Cand_Score score2 = new Cand_Score(30);

            //Δημιουργια Candidate
//            Candidate johny = new Candidate(
//                    "Johny");
//
//            Candidate jamal = new Candidate(
//                    "Jamal");


            // Προσθήκη των ερωτήσεων στο Step
//            step1.addQuestion(q1);
//            step1.addQuestion(q2);
//            step1.addQuestion(q3);

            // Προσθήκη των skill στο question
           // q1.addSkill(s1);
            //q2.addSkill(s2);
           // q3.addSkill(s3);

            //Προσθήκη score στα skill
//            s1.addcand_score(score1);
//            s2.addcand_score(score2);

            //Προσθηκη score στον Candidate
//            johny.addscore(score1);
//            jamal.addscore(score2);
//
//            jobAd1.setInterview(interview);
//
////            jobAd1.addCandidate(johny);
////            jobAd1.addCandidate(jamal);
//
////            jobAd1.getDepartment();
////            jobAd1.getDepartment().getOrganisation();
//
//            repository.save(jobAd1);
            System.out.println("JobAds saved.");
        };
    }
}