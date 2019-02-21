package com.jobanalysis.job_analysis.dao;


import com.jobanalysis.job_analysis.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserDao {

    User findByNameAndPassword(String userName, String password);

    User findByName(String userName);

    void save(User user);

}