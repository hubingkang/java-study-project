package com.example.demo.filter;

import com.example.demo.utils.TokenManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/*自定义授权过滤器*/
public class UserAuthFilter extends BasicAuthenticationFilter {


    private TokenManager tokenManager;

    private RedisTemplate redisTemplate;

    public UserAuthFilter(AuthenticationManager authenticationManager,TokenManager tokenManager, RedisTemplate redisTemplate) {
        super(authenticationManager);
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    /*返回用户权限给SpringSecurity*/
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        UsernamePasswordAuthenticationToken authRequest = getAuthentication(request);     //获取当前认证成功用户权限信息
        //判断如果有权限信息，放到权限上下文中
        if(authRequest != null) {
            SecurityContextHolder.getContext().setAuthentication(authRequest);
        }
        chain.doFilter(request,response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String token = request.getParameter("token");                         //从header获取token
        if(token != null) {
            String username = tokenManager.getUserInfoFromToken(token);      //从token获取用户名
            List<String> authList = (List<String>)redisTemplate.opsForValue().get(username);  //从redis获取对应权限列表

            /*转为SpringSecurity需要的权限类型*/
            Collection<GrantedAuthority> authority = new ArrayList<>();
            for(String permissionValue : authList) {
                SimpleGrantedAuthority auth = new SimpleGrantedAuthority(permissionValue);
                authority.add(auth);
            }
            return new UsernamePasswordAuthenticationToken(username,token,authority);
        }
        return null;
    }
}
