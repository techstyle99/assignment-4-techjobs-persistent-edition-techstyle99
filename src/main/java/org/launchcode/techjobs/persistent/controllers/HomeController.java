package org.launchcode.techjobs.persistent.controllers;

import org.launchcode.techjobs.persistent.models.Employer;
import org.launchcode.techjobs.persistent.models.Job;
import org.launchcode.techjobs.persistent.models.Skill;
import org.launchcode.techjobs.persistent.models.data.EmployerRepository;
import org.launchcode.techjobs.persistent.models.data.JobRepository;
import org.launchcode.techjobs.persistent.models.data.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by LaunchCode
 */
@Controller
public class HomeController {

    @Autowired
    EmployerRepository employerRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    SkillRepository skillRepository;

    @RequestMapping("")
    public String index(Model model) {
        model.addAttribute("title", "My Jobs");
        model.addAttribute("jobs", jobRepository.findAll());
        return "index";
    }

    @GetMapping("add")
    public String displayAddJobForm(Model model) {

        model.addAttribute("title", "Add Job");
        model.addAttribute("job", new Job());

        List<Skill> allSkills = (List <Skill>) skillRepository.findAll();
        model.addAttribute("allSkills", allSkills);

        List<Employer> allEmployers = (List<Employer>) employerRepository.findAll();
        model.addAttribute("allEmployers", allEmployers);

        return "add";
    }

    @PostMapping("add")
    public String processAddJobForm(
            @ModelAttribute @Valid Job newJob,
            Errors errors,
            Model model,
            @RequestParam int employerId,
            @RequestParam List<Integer> skills) {

        // validation
        if (skills.isEmpty())  {
            errors.rejectValue("skills", "skills.invalidskills",
                    "At least one skill must be chosen.");
        }

        if (employerId == 0) {
            errors.rejectValue("employer", "employer.invalidemployer",
                    "An employer must be chosen.");
        }

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Job");

            List<Skill> allSkills = (List<Skill>) skillRepository.findAll();
            model.addAttribute("allSkills", allSkills);

            List<Employer> allEmployers = (List<Employer>) employerRepository.findAll();
            model.addAttribute("allEmployers", allEmployers);

            model.addAttribute("employerId", employerId);

            return "add";
        }

        Employer employer = employerRepository.findById(employerId).orElse(new Employer());

        List<Skill> skillObjs = (List<Skill>) skillRepository.findAllById(skills);

        newJob.setSkills((List<Skill>) skillObjs);
        newJob.setEmployer(employer);
        jobRepository.save(newJob);

        model.addAttribute("job", newJob);
        model.addAttribute("title", "Job: " + newJob.getName());
        return "view";

    }

    @GetMapping("view/{jobId}")
    public String displayViewJob(Model model, @PathVariable int jobId) {
        model.addAttribute("job", jobRepository.findById(jobId).get());
        return "view";
    }

    @GetMapping("list-jobs")
    public String displayListJobs(Model model) {
        model.addAttribute("jobs", jobRepository.findAll());
        return "list-jobs";
    }

}
