package com.example.demo.login.dao;

import com.example.demo.login.bean.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao extends CrudRepository<User,Long>
{
    public List<User> findByUsernameAndPassword(String name, String password);
}
