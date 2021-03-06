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
        // ?????? JWT ????????? ??????session ?????????????????????Spring Security?????????session???????????????????????????????????????????????????session??????
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/admin/category/all").authenticated()
                .antMatchers("/admin/**", "/reg").hasRole("???????????????") // admin/**???URL????????????????????????????????????????????????.hasAuthority()??????????????????????????????????????????ROLE_,??????.hasAuthority("ROLE_???????????????")
                .anyRequest().authenticated() // ????????????????????????????????????
                .and().formLogin().permitAll()
                .successHandler( new AuthenticationSuccessHandler() {
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                            response.setContentType("application/json;charset=utf-8");
                            PrintWriter out = response.getWriter();
                            out.write("{\"status\":\"success\",\"msg\":\"????????????\"}");
                            out.flush();
                            out.close();
                        }
                    }
                )
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        if (exception instanceof BadCredentialsException) {
                            // ????????????
                            log.info("[????????????] - ??????????????????1");

                        } else if (exception instanceof CredentialsExpiredException) {
                            // ????????????
                            log.info("[????????????] - ??????????????????");
                        } else if (exception instanceof DisabledException) {
                            // ???????????????
                            log.info("[????????????] - ???????????????");

                        } else if (exception instanceof LockedException) {
                            // ???????????????
                            log.info("[????????????] - ???????????????");

                        } else if (exception instanceof InternalAuthenticationServiceException) {
                            // ????????????
                            log.error("[????????????] - ????????????");
                        } else {
                            // ????????????
                            log.error("[????????????] - ????????????");
                        }

                        response.setContentType("application/json;charset=utf-8");
                        PrintWriter out = response.getWriter();
                        out.write("{\"status\":\"error\",\"msg\":\"????????????\"}");
                        out.flush();
                        out.close();
                    }
                })
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new LoginUnAuthHandler())
//                .loginProcessingUrl("/login") // ????????????
//                .usernameParameter("username").passwordParameter("password").permitAll()
                .and().logout().permitAll()
                .and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(new UnauthEntryPoint())   // ?????????????????????????????????
                                    .accessDeniedHandler(new LoginUnAuthHandler());  // ?????????????????????????????????
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
