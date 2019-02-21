package com.jobanalysis.job_analysis.service.impl;

import com.jobanalysis.job_analysis.dao.JobDao;
import com.jobanalysis.job_analysis.dto.ProfeJob;
import com.jobanalysis.job_analysis.dto.ProfessionDto;
import com.jobanalysis.job_analysis.entity.JobHdfs;
import com.jobanalysis.job_analysis.entity.JobInfo;
import com.jobanalysis.job_analysis.entity.ProfeArea;
import com.jobanalysis.job_analysis.service.IJobService;
import com.jobanalysis.job_analysis.util.HDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created By 朱立松 on 2019/2/19
 */
@Service
public class JobServiceImpl implements IJobService {

    @Autowired
    private HDFSUtil hdfsUtil;

    @Autowired
    private JobDao jobDao;


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
}
