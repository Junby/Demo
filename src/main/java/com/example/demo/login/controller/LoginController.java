package com.example.demo.login.controller;

import com.example.demo.login.bean.User;
import com.example.demo.login.config.WebSecurityConfig;
import com.example.demo.login.service.LoginService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private LoginService service;

    @GetMapping("/")
    public String index(@SessionAttribute(WebSecurityConfig.SESSION_KEY) String username, Model model) {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(WebSecurityConfig.SESSION_KEY);
        return "redirect:/login";
    }

    @PostMapping("/loginVerify")
    public String loginVerify(String username, String password, HttpSession session) {
        User user = new User();
        user.setUserName(username);
        user.setPassword(password);

        boolean verify = service.verifyLogin(user);
        if (verify) {
            session.setAttribute(WebSecurityConfig.SESSION_KEY, username);
            return "index";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/test")
    public ModelAndView getJson(ModelMap model)
    {
        model.put("test", "test");
        return new ModelAndView(new MappingJackson2JsonView(), model);
    }

//    @PostMapping("/loginVerify")
//    public String loginVerify(String username,String password){
//        try {

//            System.out.println("---------------------" + username + "-----------" + password);
//            UsernamePasswordToken token = new UsernamePasswordToken(username,password);
//            SecurityUtils.getSubject().login(token);
//            return "index";
//        } catch (Exception e){
//            e.printStackTrace();
//            return "login";
//        }
//    }
}
