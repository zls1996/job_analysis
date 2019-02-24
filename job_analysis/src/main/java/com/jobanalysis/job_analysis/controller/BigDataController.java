package com.jobanalysis.job_analysis.controller;

import com.jobanalysis.job_analysis.dto.CityJobClassSalaryDto;
import com.jobanalysis.job_analysis.dto.ProfeJob;
import com.jobanalysis.job_analysis.entity.ProfeArea;
import com.jobanalysis.job_analysis.dto.ProfessionDto;
import com.jobanalysis.job_analysis.service.IJobService;
import com.jobanalysis.job_analysis.util.HDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Created By 朱立松 on 2019/2/19
 */
@RestController
@RequestMapping("/")
public class BigDataController {

    @Autowired
    private IJobService jobService;

    @Autowired
    private HDFSUtil hdfsUtil;

    @RequestMapping("/test")
    public String test() throws IOException {
        hdfsUtil.ls("/jobs");
        return "ok";
    }

    /**
     * 行业占比分析
     * @return
     */
    @RequestMapping(value = "/proanaly", method = RequestMethod.GET)
    public List<ProfessionDto> professionSort(){
        return jobService.professionAnalysis();
    }

    /**
     * 职位区域分析
     * @return
     */
    @RequestMapping(value = "/professionareaanalysis", method = RequestMethod.GET)
    public List<ProfeJob> proAreaAnalysis(){
        return jobService.getProfessionJobArea();
    }

    /**
     * 薪资水平分析
     * @return
     */
    @RequestMapping(value = "/salaryanalysis", method = RequestMethod.POST)
    public List<CityJobClassSalaryDto> salaryAnalysis(@RequestBody String[] cities){
        return jobService.analysisSalary(cities);
    }


}
