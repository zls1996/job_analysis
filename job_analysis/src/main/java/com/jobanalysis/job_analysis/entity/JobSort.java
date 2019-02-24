package com.jobanalysis.job_analysis.entity;

/**
 * Created By 朱立松 on 2019/2/24
 * 对应职位分类
 */
public class JobSort {
    private Integer id;

    //职位分类
    private String jobClassify;

    //对应hdfs分类路径
    private String hdfsPath;

    public JobSort(String jobClassify, String hdfsPath) {
        this.jobClassify = jobClassify;
        this.hdfsPath = hdfsPath;
    }

    public JobSort() {
    }

    public Integer getId() {
        return id;
    }


    public String getJobClassify() {
        return jobClassify;
    }

    public void setJobClassify(String jobClassify) {
        this.jobClassify = jobClassify;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }

    @Override
    public String toString() {
        return "JobSort{" +
                "id=" + id +
                ", jobClassify='" + jobClassify + '\'' +
                ", hdfsPath='" + hdfsPath + '\'' +
                '}';
    }
}
