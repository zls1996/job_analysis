package com.jobanalysis.job_analysis.dto;


/**
 * Created By 朱立松 on 2019/2/16
 * springmvc不嗯能够将枚举转成json，只能转class对象为json
 */
public class LoginClass {

    private Boolean suc;

    private String msg;


    public LoginClass(Boolean suc, String msg) {
        this.suc = suc;
        this.msg = msg;
    }

    public LoginClass(LoginState loginState){
        this.suc = loginState.getSuc();
        this.msg = loginState.getMsg();
    }

    public Boolean getSuc() {
        return suc;
    }

    public void setSuc(Boolean suc) {
        this.suc = suc;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
