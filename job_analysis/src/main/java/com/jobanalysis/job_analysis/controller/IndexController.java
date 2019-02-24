package com.jobanalysis.job_analysis.controller;


import com.jobanalysis.job_analysis.dto.LoginClass;
import com.jobanalysis.job_analysis.dto.LoginState;
import com.jobanalysis.job_analysis.entity.User;
import com.jobanalysis.job_analysis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class IndexController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param username
     * @param password
     * @param response
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginClass login(String username, String password, HttpServletResponse response) {
        LoginState loginState = userService.findByNameAndPassword(username, password, response);

        return new LoginClass(loginState);
    }

    /**
     * 注册
     * @param request
     * @param username
     * @param password
     * @param response
     * @return
     */
    @RequestMapping("/uregister")
    public LoginClass register(HttpServletRequest request,String username, String password, HttpServletResponse response) {
        LoginClass loginClass = userService.save(new User(username, password));
        return loginClass;
    }


}
