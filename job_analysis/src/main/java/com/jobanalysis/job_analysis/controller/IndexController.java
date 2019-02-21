package com.jobanalysis.job_analysis.controller;


import com.jobanalysis.job_analysis.entity.User;
import com.jobanalysis.job_analysis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Controller
public class IndexController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String index() {
        return "login";
    }


    @RequestMapping("/register")
    public String register() {
        return "register";
    }

    /*
    @RequestMapping("/login")
    public String login() {
        return "login";
    }
    */

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(String username, String password) {
        User user = userService.findByNameAndPassword(username, password);
        String str = "";
        if (user != null) {
            str = "index";
        } else {
            str = "login";
        }
        return str;
    }

    @RequestMapping("/uregister")
    public String register(HttpServletRequest request) {
        User user;
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");
        String str = "";
        if (password.equals(password2)) {
            user = userService.findByNameAndPassword(username, password);
            if (user == null) {
                User userOne = new User();
                userOne.setUserName(username);
                userOne.setPassword(password);
                userService.save(userOne);
                str="login";
            } else {
                str = "register";
            }
        }
        return str;

    }


}
