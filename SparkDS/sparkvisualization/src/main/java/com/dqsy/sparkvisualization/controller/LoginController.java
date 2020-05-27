package com.dqsy.sparkvisualization.controller;

import com.dqsy.sparkvisualization.entity.User;
import com.dqsy.sparkvisualization.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    UserMapper userMapper;

    //进入登录页
    @RequestMapping("/")
    public String login1() {
        return "login";
    }

    //登录
    @RequestMapping("/user/login")
    public String register(@RequestParam("uname") String uname, @RequestParam("upassword") String upassword, Model model) {
        User user = userMapper.findByUserName(uname);
        if (uname.equals(user.getUserName()) && upassword.equals(user.getUserPassword())) {
            return "success";
        } else {
            model.addAttribute("msg", "用户名或密码错误！");
            return "login";
        }
    }

    @RequestMapping("/aduserlogin")
    public String aduserlogin() {
        return "aduserlogin";
    }

    @RequestMapping("/adClick/login")
    public String aduserlogin1(String uname, String upassword, Model model, HttpServletRequest request) {
        User user = userMapper.findByUserName(uname);
        if (uname.equals(user.getUserName()) && upassword.equals(user.getUserPassword())) {
            model.addAttribute("uid", user.getUserId());
            return "adClick";
        } else {
            model.addAttribute("msg", "用户名或密码错误！");
            return "aduserlogin";
        }
    }

}
