package com.study.vblog.config;

import com.study.vblog.handler.LoginUnAuthHandler;
import com.study.vblog.handler.UnauthEntryPoint;
import com.study.vblog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.security.auth.login.AccountExpiredException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(password());
//        auth.userDetailsService(userService).passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        // 采用 JWT 第一步 禁用session 这里的禁用是指Spring Security不采用session机制了，不代表你禁用掉了整个系统的session功能
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/admin/category/all").authenticated()
                .antMatchers("/admin/**", "/reg").hasRole("超级管理员") // admin/**的URL都需要有超级管理员角色，如果使用.hasAuthority()方法来配置，需要在参数中加上ROLE_,如下.hasAuthority("ROLE_超级管理员")
                .anyRequest().authenticated() // 其他的路径都需要登录授权
                .and().formLogin().permitAll()
                .successHandler( new AuthenticationSuccessHandler() {
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                            response.setContentType("application/json;charset=utf-8");
                            PrintWriter out = response.getWriter();
                            out.write("{\"status\":\"success\",\"msg\":\"登录成功\"}");
                            out.flush();
                            out.close();
                        }
                    }
                )
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        if (exception instanceof BadCredentialsException) {
                            // 密码错误
                            log.info("[登录失败] - 用户密码错误1");

                        } else if (exception instanceof CredentialsExpiredException) {
                            // 密码过期
                            log.info("[登录失败] - 用户密码过期");
                        } else if (exception instanceof DisabledException) {
                            // 用户被禁用
                            log.info("[登录失败] - 用户被禁用");

                        } else if (exception instanceof LockedException) {
                            // 用户被锁定
                            log.info("[登录失败] - 用户被锁定");

                        } else if (exception instanceof InternalAuthenticationServiceException) {
                            // 内部错误
                            log.error("[登录失败] - 内部错误");
                        } else {
                            // 其他错误
                            log.error("[登录失败] - 其他错误");
                        }

                        response.setContentType("application/json;charset=utf-8");
                        PrintWriter out = response.getWriter();
                        out.write("{\"status\":\"error\",\"msg\":\"登录失败\"}");
                        out.flush();
                        out.close();
                    }
                })
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new LoginUnAuthHandler())
//                .loginProcessingUrl("/login") // 登录接口
//                .usernameParameter("username").passwordParameter("password").permitAll()
                .and().logout().permitAll()
                .and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(new UnauthEntryPoint())   // 匿名用户没有权限处理器
                                    .accessDeniedHandler(new LoginUnAuthHandler());  // 认证用户没有权限处理器
//                                    .accessDeniedHandler(getAccessDeniedHandler());

    }

    @Bean
    public PasswordEncoder password(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/blogimg/**","/index.html","/static/**");
    }

    @Bean
    AccessDeniedHandler getAccessDeniedHandler() {
        return new AuthenticationAccessDeniedHandler();
    }
}
