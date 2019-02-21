package com.jobanalysis.job_analysis.dto;

/**
 * Created By 朱立松 on 2019/2/19
 * 职位分析
 */
public class ProfessionDto {
    private Integer value;
    private String name;

    public ProfessionDto(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public ProfessionDto() {
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProfessionDto{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}
