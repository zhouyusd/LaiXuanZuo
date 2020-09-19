package com.beier116.xuanzuo.controller;

import com.beier116.xuanzuo.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping
    public String userList() {
        log.error("users");
        return "users";
    }

//    @PostMapping
//    public String login(
//            @Param("username") String username,
//            @Param("password") String password) {
//        return username + "---" + password;
//    }
//
//    @PostMapping("/insert")
//    public Object addUser(@RequestBody User user) {
//        user.setRegisterDate(new Date());
//        Boolean flag = userService.insertUser(user);
//        return flag ? user : "添加失败";
//    }
}
