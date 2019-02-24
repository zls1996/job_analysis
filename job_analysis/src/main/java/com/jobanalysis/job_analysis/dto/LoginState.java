package com.jobanalysis.job_analysis.dto;

/**
 * 用户登录的几个状态
 */
public enum LoginState {
    NO_SUCH_USER(false,"没有该用户"),
    WRONG_LOGIN_INFO(false, "用户名或密码错误"),
    CORRECT_INFO(true, "输入正确"),
    WRONG_MANAGER_TYPE(false, "管理员类型错误");

    private final Boolean suc;

    private final String msg;



    LoginState(Boolean suc, String msg) {
        this.msg = msg;
        this.suc = suc;
    }


    public Boolean getSuc() {
        return suc;
    }

    public String getMsg() {
        return msg;
    }


    @Override
    public String toString() {
        return "LoginState{" +
                "suc=" + suc +
                ", msg='" + msg + '\'' +
                '}';
    }}
