package com.jobanalysis.job_analysis.service.impl;

import com.jobanalysis.job_analysis.dao.JobDao;
import com.jobanalysis.job_analysis.dto.CityJobClassSalaryDto;
import com.jobanalysis.job_analysis.dto.ProfeJob;
import com.jobanalysis.job_analysis.dto.ProfessionDto;
import com.jobanalysis.job_analysis.entity.JobHdfs;
import com.jobanalysis.job_analysis.entity.JobInfo;
import com.jobanalysis.job_analysis.entity.JobSalary;
import com.jobanalysis.job_analysis.entity.ProfeArea;
import com.jobanalysis.job_analysis.mapreduce.areaSalary.JobSubmitter;
import com.jobanalysis.job_analysis.service.IJobService;
import com.jobanalysis.job_analysis.util.HDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By 朱立松 on 2019/2/19
 */
@Service
public class JobServiceImpl implements IJobService {

    @Autowired
    private HDFSUtil hdfsUtil;

    @Autowired
    private JobDao jobDao;

    @Autowired
    private JobSubmitter jobSubmitter;


    /**
     * 根据职位名称、地区和学历来筛选数据
     * @param jobname
     * @param area
     * @param edu
     * @return
     */
    @Override
    public List<JobInfo> search(String jobname, String area, String edu) {
        //得到所有职位分类的id
        List<Integer> professionList = jobDao.getProfessionIds();
        if(professionList != null && professionList.size() != 0){
            for(Integer sortId : professionList){
                //根据职业分类来获得所对应的hdfs路径
                List<JobHdfs> jobHdfses = jobDao.getJobHdfsByProfe(sortId);
                List<ProfeArea> list = new ArrayList<>();
                for(JobHdfs jobHdfs : jobHdfses){
                    List<JobInfo> jobList = hdfsUtil.getJobByNameAndAreaAndEdu(jobHdfs.getHdfsPath(), jobname, area, edu);
                    if(jobList != null && jobList.size() != 0){
                        return jobList;
                    }
                }



            }

        }
        return null;
    }

    /**
     * 职位分析
     * @return
     */
    @Override
    public List<ProfessionDto> professionAnalysis() {
        //得到professionList
        List<String> professionList = jobDao.getProfessionList();
        if(professionList != null){
            List<ProfessionDto> resultList = new ArrayList<>();
            professionList.forEach(profession -> {
                Integer professionId = jobDao.getJobIdByProfession(profession);

                //根据职业分类来获得所对应的hdfs路径
                List<JobHdfs> jobHdfses = jobDao.getJobHdfsByProfe(professionId);
                Integer totalCount = 0;
                for(JobHdfs jobHdfs : jobHdfses){
                    //获得一个hdfs文件中的记录条数
                    Integer count = hdfsUtil.getElementCount(jobHdfs.getHdfsPath());
                    totalCount += count;
                }
                resultList.add(new ProfessionDto(totalCount, profession));
            });
            return resultList;
        }
        return null;
    }

    /**
     * 通过职位分类来获得城市对应的职位数目
     * @return
     */
    @Override
    public List<ProfeJob> getProfessionJobArea() {
        //得到所有职位分类的id
        List<Integer> professionList = jobDao.getProfessionIds();
        List<ProfeJob> resultList = new ArrayList<>();
        if(professionList != null && professionList.size() != 0){
            for(Integer sortId : professionList){
                //根据id获取分类名
                String job_classify = jobDao.getJobClassById(sortId);

                //根据职业分类来获得所对应的hdfs路径
                List<JobHdfs> jobHdfses = jobDao.getJobHdfsByProfe(sortId);

                List<ProfeArea> list = new ArrayList<>();
                for(JobHdfs jobHdfs : jobHdfses){
                    //从hdfs中获取ProfeArea列表
                    List<ProfeArea> profeAreaList = hdfsUtil.sortJobCountByCity(jobHdfs.getHdfsPath());
                    //将两个list合并
                    list = mergeList(list, profeAreaList);
                }
                resultList.add(new ProfeJob(job_classify, list));


            }

        }
        return resultList;
    }

    /**
     * 合并两个list中的ProfeArea
     * @param list
     * @param profeAreaList
     * @return
     */
    private List<ProfeArea> mergeList(List<ProfeArea> list, List<ProfeArea> profeAreaList) {
        if(list == null || list.size() == 0){
            return profeAreaList;
        }

        if(profeAreaList == null || list.size() == 0){
            return list;
        }

        for(ProfeArea profeA :list){
            for(ProfeArea profeB : profeAreaList){
                if(profeA.getCity().equals(profeB.getCity())){
                    profeA.setCount(profeA.getCount()+profeB.getCount());
                }
            }
        }


        return list;


    }

    /**
     * 对特定城市的薪资水平分析
     * @return
     * @param cities
     */
    @Override
    public List<CityJobClassSalaryDto> analysisSalary(String[] cities) {
        String mapReducePath = "/salary_mapReduce";
        //得到所有职位分类的id
        List<Integer> professionList = jobDao.getProfessionIds();
        List<CityJobClassSalaryDto> resultList = new ArrayList<>();
        if(professionList != null && professionList.size() != 0){
            for(Integer sortId : professionList){
                //根据id获取对应hdfs路径
                String job_hdfs = jobDao.getHdfsPathById(sortId);
                List<JobSalary> jobSalaryList = new ArrayList<>();
                //如果存在该hdfs路径，表示已经进行过mapreduce计算
                String hdfsPath = mapReducePath+"/" + job_hdfs;
                if(! hdfsUtil.containsPath(hdfsPath)){
                    //未进行过hdfs运算，则进行mapreduce
                    jobSubmitter.createMapReduceJob(job_hdfs+"/*/part-r-00000",hdfsPath);
                }
                //根据hdfsPath来获得jobSalaryList
                jobSalaryList = hdfsUtil.getJobSalary(hdfsPath, cities);

                //获得职位分类
                String jobSort = jobDao.getJobClassById(sortId);

                resultList.add(new CityJobClassSalaryDto(jobSort, jobSalaryList));

            }

        }
        return resultList;
    }
}
