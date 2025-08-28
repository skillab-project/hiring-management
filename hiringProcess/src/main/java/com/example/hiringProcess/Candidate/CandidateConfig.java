package com.example.hiringProcess.Candidate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CandidateConfig {
    @Bean(name = "candidateCommandLineRunner")
    CommandLineRunner commandLineRunner(CandidateRepository repository){
        System.out.println("HIIIIIIIIIIIIIIII");
        return args -> {
            System.out.println("Saving candidates...");
//           Candidate johny = new Candidate(
//                   "Johny");
//
//            Candidate jamal = new Candidate(
//                    "Jamal");
            //Δημιοργία score για τα skills
//            Cand_Score score1 = new Cand_Score(80);
//            Cand_Score score2 = new Cand_Score(30);
//
//            //Προσθηκη score στον Candidate
//            johny.addscore(score1);
//            jamal.addscore(score2);
//
//
//
//
//            repository.saveAll(List.of(johny, jamal));


            System.out.println("Candidates saved.");
        };
    }
}
