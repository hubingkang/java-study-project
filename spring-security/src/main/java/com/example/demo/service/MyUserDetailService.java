package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.SecurityUser;
import com.example.demo.entity.Users;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        List<GrantedAuthority> authsList = AuthorityUtils.commaSeparatedStringToAuthorityList("role"); //权限，框架自带的值，实际为表内配置
//        return new User("demo", new BCryptPasswordEncoder().encode("demo"), authsList);
        // 查询数据库
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        Users users = userMapper.selectOne(wrapper);

        // 判断
        if (users == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

//        List<GrantedAuthority> authsList = AuthorityUtils.commaSeparatedStringToAuthorityList("admin,ROLE_worker,ROLE_javase,worker:java:create,worker:java:del"); // 设置用户具有 admin 权限
//        // 返回数据库的 users 对象
//        return new User(users.getUsername(), new BCryptPasswordEncoder().encode(users.getPassword()), authsList);
        /*自定义用户登录验证Filter需要返回String的权限list*/
        users.setPassword(new BCryptPasswordEncoder().encode(users.getPassword()));
        List<String> authList = Arrays.asList("admin","ROLE_worker","ROLE_javase","worker:java:create","worker:java:del");
        SecurityUser securityUser = new SecurityUser();
        securityUser.setCurrentUserInfo(users);
        securityUser.setPermissionValueList(authList);
        return securityUser;
    }
}
