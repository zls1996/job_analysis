package com.jobanalysis.job_analysis.dto;

import com.jobanalysis.job_analysis.entity.ProfeArea;

import java.util.List;

/**
 * Created By 朱立松 on 2019/2/19
 * 城市职位分析DTO
 */
public class ProfeJob {

    //职位分类
    private String jobClass;

    //城市职位
    private List<ProfeArea> profeAreaList;

    public ProfeJob(String jobClass, List<ProfeArea> profeAreaList) {
        this.jobClass = jobClass;
        this.profeAreaList = profeAreaList;
    }

    public ProfeJob() {
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public List<ProfeArea> getProfeAreaList() {
        return profeAreaList;
    }

    public void setProfeAreaList(List<ProfeArea> profeAreaList) {
        this.profeAreaList = profeAreaList;
    }
}
