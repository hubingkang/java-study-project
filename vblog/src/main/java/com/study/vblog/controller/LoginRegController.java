package com.study.vblog.controller;

import com.study.vblog.bean.RespBean;
import com.study.vblog.bean.User;
import com.study.vblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class LoginRegController {

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 如果自动跳转到这个页面，说明用户未登录，返回相应的提示即可
     * <p>
     * 如果要支持表单登录，可以在这个方法中判断请求的类型，进而决定返回JSON还是HTML页面
     *
     * @return
     */
    @RequestMapping("/login")
    public RespBean loginPage() {
        return new RespBean("error", "尚未登录，请登录!");
    }

//    @PostMapping("/login")
//    public User login() {
//         调用业务层执行登录操作
//        return userService.login(user);
//    }

    @PostMapping("/register")
    public RespBean register(User user) {
        // 调用加密器将前端传递过来的密码进行加密
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 将用户实体对象添加到数据库
        int result = userService.register(user);
        if (result == 0) {
            // 成功
            return new RespBean("success", "注册成功!");
        } else if (result == 1) {
            return new RespBean("error", "用户名重复，注册失败!");
        } else {
            //失败
            return new RespBean("error", "注册失败!");
        }
    }
}
