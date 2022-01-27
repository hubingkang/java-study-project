package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
public class BrowserSecurityConfig {

    @Autowired
    DataSource dataSource;

    // 记住登录
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {

        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();  // 赋值数据源
        jdbcTokenRepository.setDataSource(dataSource); // 设置数据源
//        jdbcTokenRepository.setCreateTableOnStartup(true); // 启动创建表 persistent_logins ，创建成功后注释掉
        return jdbcTokenRepository;
    }
}
