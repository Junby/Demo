package com.example.demo.login.service;

import com.example.demo.login.bean.User;
import com.example.demo.login.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginService
{
    @Autowired
    private UserDao dao;

    public boolean verifyLogin(User user)
    {
        List<User> userList = dao.findByUsernameAndPassword(user.getUserName(), user.getPassword());
        return userList.size()>0;
    }

    public List<User> getUserByNameAndPassword(String username, String password)
    {
        List<User> userList = dao.findByUsernameAndPassword(username, password);
        return userList;
    }
}
