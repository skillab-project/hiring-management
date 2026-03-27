package com.example.hiringProcess.Skill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
//@RequestMapping(path = "api/v1/skill")
public class SkillController {

    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping(path = "/skills")
    public List<Skill> getSkills() {
        return skillService.getSkills();
    }

    @GetMapping(path = "/skill")
    public Optional<Skill> getSkill(@RequestParam Integer skillId) {
        return skillService.getSkill(skillId);
    }

}
