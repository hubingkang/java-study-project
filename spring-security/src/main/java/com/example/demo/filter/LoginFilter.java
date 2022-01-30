package com.example.demo.filter;

import com.example.demo.entity.SecurityUser;
import com.example.demo.utils.TokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private TokenManager tokenManager;
    private RedisTemplate redisTemplate;
    private AuthenticationManager authenticationManager; // 权限管理工具

    // 创建构造器
    public LoginFilter(AuthenticationManager authenticationManager, TokenManager tokenManager, RedisTemplate redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
        this.tokenManager = tokenManager;
        this.setPostOnly(false);
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/user/login","POST")); //自定义设置登录路径和登录访问方式（默认为/user/login，这里手动写默认值）
    }

    /*自定义验证方法，用户登录类必须为实现UserDetails接口 */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String loginname = request.getParameter("loginname");
        String pwd = request.getParameter("pwd");
        logger.info("自定义验证中获取的登录页信息:"+loginname+"  "+pwd);
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginname,pwd, new ArrayList<>())       //占位权限参数，实际权限值从自定义的用户登录来源（MyUserDetailsService）返回
        );
    }

    /*认证成功执行的方法*/
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        SecurityUser securityUser = (SecurityUser) authResult.getPrincipal();                            //获取登录用户信息,在自定义用户登录数据来源需要返回该类型才不报错！
        String token = tokenManager.createToken(securityUser.getCurrentUserInfo().getUsername());        //根据用户名生成token
        redisTemplate.opsForValue().set(securityUser.getCurrentUserInfo().getUsername(),securityUser.getPermissionValueList());  //权限信息保存到Redis中

        /*返回Token*/
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            mapper.writeValue(response.getWriter(),token);
            logger.info("认证成功,Token为:"+token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*认证失败执行的方法*/
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        logger.warn("认证失败啦!"+failed.getLocalizedMessage());
    }
}
