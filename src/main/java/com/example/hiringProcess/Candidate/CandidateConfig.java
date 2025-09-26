package com.example.hiringProcess.Candidate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CandidateConfig {
    @Bean(name = "candidateCommandLineRunner")
    CommandLineRunner commandLineRunner(CandidateRepository repository){
        return args -> {
            System.out.println("Saving candidates...");
            System.out.println("Candidates saved.");
        };
    }
}
