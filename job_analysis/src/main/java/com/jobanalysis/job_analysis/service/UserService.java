package com.jobanalysis.job_analysis.service;


import com.jobanalysis.job_analysis.dao.UserDao;
import com.jobanalysis.job_analysis.dto.LoginClass;
import com.jobanalysis.job_analysis.dto.LoginState;
import com.jobanalysis.job_analysis.entity.User;
import com.jobanalysis.job_analysis.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public LoginState findByNameAndPassword(String username, String password, HttpServletResponse response) {
        User user = userDao.findByName(username);
        if(user == null){
            return LoginState.NO_SUCH_USER;
        }
        //密码不匹配，则输出密码错误
        if(!user.getPassword().equals(password)){
            return LoginState.WRONG_LOGIN_INFO;
        }
        CookieUtil.generateCookieForUser(username, response);
        return LoginState.CORRECT_INFO;

    }

    public User findByName(String userName) {
        return userDao.findByName(userName);
    }

    public LoginClass save(User user) {
        userDao.save(user);
        return new LoginClass(true, "注册成功");
    }

}
