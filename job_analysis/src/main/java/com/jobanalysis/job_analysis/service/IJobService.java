package com.jobanalysis.job_analysis.service;

import com.jobanalysis.job_analysis.dto.CityJobClassSalaryDto;
import com.jobanalysis.job_analysis.dto.ProfeJob;
import com.jobanalysis.job_analysis.dto.ProfessionDto;
import com.jobanalysis.job_analysis.entity.JobInfo;

import java.util.List;

/**
 * Created By 朱立松 on 2019/2/19
 */
public interface IJobService {
    /**
     * 职位搜索
     * @param jobname
     * @param area
     * @param edu
     * @return
     */
    List<JobInfo> search(String jobname, String area, String edu);

    List<ProfessionDto> professionAnalysis();

    /**
     * 图表接口
     * 通过职位分类来获得城市对应的职位数目
     * @return
     */
    List<ProfeJob> getProfessionJobArea();

    /**
     * 薪资水平分析
     * @return
     * @param cities
     */
    List<CityJobClassSalaryDto> analysisSalary(String[] cities);
}
