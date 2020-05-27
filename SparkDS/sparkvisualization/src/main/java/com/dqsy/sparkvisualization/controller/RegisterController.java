package com.dqsy.sparkvisualization.controller;

import com.dqsy.sparkvisualization.entity.User;
import com.dqsy.sparkvisualization.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class RegisterController {

    @Autowired
    UserMapper userMapper;

    //进入注册页
    @RequestMapping("/register")
    public String registerPage() {
        return "register";

    }

    //注册
    @RequestMapping("/user/register")
    public String register(@RequestParam("uname") String uname, @RequestParam("password") String password, Map<String, Object> map, Model model) {

        User user = userMapper.findByUserName(uname);
        if (user == null) {
            User user1 = new User();
            user1.setUserName(uname);
            user1.setUserPassword(password);
            userMapper.insert(user1);
            return "success1";
        } else {
            model.addAttribute("msg", "用户名已存在，重新注册！");
            return "register";
        }

    }

}
