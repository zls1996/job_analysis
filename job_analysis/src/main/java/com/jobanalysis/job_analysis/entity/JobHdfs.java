package com.jobanalysis.job_analysis.entity;

/**
 * Created By 朱立松 on 2019/2/19
 */
public class JobHdfs {
    private Integer id;

    //职位名称
    private String jobName;

    //对应hdfs文件路径
    private String hdfsPath;

    //爬虫来源
    private String spiderSource;

    //职位分类
    private Integer jobClassify;

    public JobHdfs(Integer id, String jobName, String hdfsPath, String spiderSource, Integer jobClassify) {
        this.id = id;
        this.jobName = jobName;
        this.hdfsPath = hdfsPath;
        this.spiderSource = spiderSource;
        this.jobClassify = jobClassify;
    }

    public JobHdfs(String jobName, String hdfsPath, String spiderSource, Integer jobClassify) {
        this.jobName = jobName;
        this.hdfsPath = hdfsPath;
        this.spiderSource = spiderSource;
        this.jobClassify = jobClassify;
    }

    public JobHdfs() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }

    public String getSpiderSource() {
        return spiderSource;
    }

    public void setSpiderSource(String spiderSource) {
        this.spiderSource = spiderSource;
    }

    public Integer getJobClassify() {
        return jobClassify;
    }

    public void setJobClassify(Integer jobClassify) {
        this.jobClassify = jobClassify;
    }
}
