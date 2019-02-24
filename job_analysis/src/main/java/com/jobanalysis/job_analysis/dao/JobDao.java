package com.jobanalysis.job_analysis.dao;

import com.jobanalysis.job_analysis.entity.JobHdfs;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created By 朱立松 on 2019/2/19
 */
@Repository
@Mapper
public interface JobDao {

    /**
     *得到职位分类的列表
     * @return
     */
    List<String> getProfessionList();

    /**
     * 得到所有职位的id
     * @return
     */
    List<Integer> getProfessionIds();

    /**
     * 根据职位分类id来获得所对应的JobHDFS
     * @param id
     * @return
     */
    List<JobHdfs> getJobHdfsByProfe(Integer id);

    /**
     * 根据职业来获得id
     * @param profession
     * @return
     */
    Integer getJobIdByProfession(String profession);

    /**
     * 根据id来获得职位分类名称
     * @param id
     * @return
     */
    String getJobClassById(Integer id);

    /**
     * 插入数据
     * @param jobHdfs
     */
    void insert(JobHdfs jobHdfs);

    /**
     * 根据id来获取对应职位分类的hdfs路径
     * @param id
     * @return
     */
    String getHdfsPathById(Integer id);
}
