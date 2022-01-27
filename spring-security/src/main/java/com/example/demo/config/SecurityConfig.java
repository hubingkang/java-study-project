package com.example.demo.config;

import com.example.demo.handler.TokenLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    PersistentTokenRepository tokenRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    // 自定义登录用户名和密码加密方式
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    // 自定义拦截页面
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        getHttp().rememberMe()
                .tokenRepository(tokenRepository) // 数据源
                .tokenValiditySeconds(20) // 设置 20 秒过期
                .userDetailsService(userDetailsService); // 指定 service 类

        getHttp().formLogin()                                   //进行自定义登录操作
                .loginPage("/login.html")                       //指定登录页
                .loginProcessingUrl("/user/login")              //登录访问路径(默认为该值)
                .defaultSuccessUrl("/demo/index").permitAll()   //认证成功后跳转路径(被successHandler覆盖后不生效)
                .usernameParameter("loginname")                 //表单自定义的登录名
                .passwordParameter("pwd")                       //表单自定义的的密码名

                .and()
                    .authorizeRequests().antMatchers("/", "/user/login") // 配置请求路径
                    .permitAll() //不需要认证即可访问
                    .antMatchers("/admin/index").hasAnyAuthority("admin,superadmin") //需要权限才能访问
                    .antMatchers("/admin/createAdmin").hasAnyRole("boss,shareholder") //需要角色才能访问
//                    .antMatchers("/admin/createAdmin").hasRole("boss") //需要角色才能访问
                .anyRequest().authenticated()                                   //其他请求需要认证
                .and().csrf().disable();                                         //关闭CSRF

        getHttp().exceptionHandling().accessDeniedPage("/noAuth");       //没有权限跳转的错误提示页面路径

        getHttp().logout().logoutUrl("/loginout")
                .addLogoutHandler(new TokenLogoutHandler(redisTemplate)) //指定退出处理器
                .logoutSuccessUrl("/login.html").permitAll(); // 注销
    }
}
