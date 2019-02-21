package com.jobanalysis.job_analysis.controller;

import com.jobanalysis.job_analysis.entity.JobInfo;
import com.jobanalysis.job_analysis.service.IJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created By 朱立松 on 2019/2/19
 * 职位搜索的Controller
 */
@RestController
@RequestMapping("/jobinfo")
public class JobSearchController {

    @Autowired
    private IJobService jobService;

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public List<JobInfo> searchJobs(String jobname, String area, String edu){
        return jobService.search(jobname, area, edu);
    }

}
