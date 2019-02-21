package com.jobanalysis.job_analysis.entity;

/**
 * Created By 朱立松 on 2019/2/19
 * 城市职位数目entity
 */
public class ProfeArea {

    //城市
    private String city;
    //计数
    private Integer count;

    public ProfeArea(String city, Integer count) {
        this.city = city;
        this.count = count;
    }

    public ProfeArea() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
