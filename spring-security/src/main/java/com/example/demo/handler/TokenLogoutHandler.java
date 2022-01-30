package com.example.demo.handler;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenLogoutHandler implements LogoutHandler {

    private RedisTemplate redisTemplate;

    public TokenLogoutHandler(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /** 退出前的操作，这里为添加Redis统计次数，实际为清除用户登录Token信息等操作,token可通过request的Header传递*/
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        redisTemplate.opsForValue().increment("loginout" + request.getParameter("loginname"), 1);
    }
}
