package com.jobanalysis.job_analysis.dto;

import com.jobanalysis.job_analysis.entity.JobSalary;

import java.util.List;

/**
 * Created By 朱立松 on 2019/2/23
 * 城市水平薪资分析
 */
public class CityJobClassSalaryDto {

    //职位类别
    private String  jobClass;

    //对应城市与职位列表
    List<JobSalary> jobSalaryList;

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public List<JobSalary> getJobSalaryList() {
        return jobSalaryList;
    }

    public void setJobSalaryList(List<JobSalary> jobSalaryList) {
        this.jobSalaryList = jobSalaryList;
    }

    public CityJobClassSalaryDto(String jobClass, List<JobSalary> jobSalaryList) {
        this.jobClass = jobClass;
        this.jobSalaryList = jobSalaryList;
    }

    public CityJobClassSalaryDto() {
    }
}
