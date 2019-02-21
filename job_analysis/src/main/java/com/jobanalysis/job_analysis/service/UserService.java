package com.jobanalysis.job_analysis.service;


import com.jobanalysis.job_analysis.dao.UserDao;
import com.jobanalysis.job_analysis.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User findByNameAndPassword(String userName, String password) {
        return userDao.findByNameAndPassword(userName, password);
    }

    public User findByName(String userName) {
        return userDao.findByName(userName);
    }

    public void save(User user) {
        userDao.save(user);
    }

}
