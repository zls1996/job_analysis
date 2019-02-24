package com.jobanalysis.job_analysis.entity;

/**
 * Created By 朱立松 on 2019/2/23
 * 对应城市职位与平均薪资
 */
public class JobSalary {

    //城市
    private String city;
    //平均工资
    private Float avgSalary;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Float getAvgSalary() {
        return avgSalary;
    }

    public void setAvgSalary(Float avgSalary) {
        this.avgSalary = avgSalary;
    }

    public JobSalary(String city, Float avgSalary) {
        this.city = city;
        this.avgSalary = avgSalary;
    }

    public JobSalary() {
    }
}
