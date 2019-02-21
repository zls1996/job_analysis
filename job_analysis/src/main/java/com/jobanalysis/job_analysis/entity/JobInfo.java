package com.jobanalysis.job_analysis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created By 朱立松 on 2019/2/19
 * 职位信息
 */
public class JobInfo {
    private Integer id;

    //薪资区域
    private String salary;

    //职位名称
    private String jobName;
    //公司
    private String company;
    //地址
    private String location;
    //经验
    private String experience;
    //学历
    private String education;
    //职位描述
    @JsonIgnore
    private String jobDesc;

    public JobInfo(Integer id, String location, String company,String education,
              String jobDesc,  String jobName, String salary,
                     String experience) {
        this.id = id;
        this.salary = salary;
        this.jobName = jobName;
        this.company = company;
        this.location = location;
        this.experience = experience;
        this.education = education;
        this.jobDesc = jobDesc;
    }

    public JobInfo() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    @Override
    public String toString() {
        return "JobInfo{" +
                "id=" + id +
                ", salary='" + salary + '\'' +
                ", jobName='" + jobName + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                ", experience='" + experience + '\'' +
                ", education='" + education + '\'' +
                ", jobDesc='" + jobDesc + '\'' +
                '}';
    }
}
