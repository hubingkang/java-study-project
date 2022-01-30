package com.study.vblog.handler;

import com.alibaba.fastjson.JSON;
//import com.dmbjz.utils.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/*已登录用户无权限处理器*/
public class LoginUnAuthHandler implements AccessDeniedHandler {


    private Logger logger = LoggerFactory.getLogger(getClass());

//    private TokenManager tokenManager;
//
//
//    public LoginUnAuthHandler(TokenManager tokenManager) {
//        this.tokenManager = tokenManager;
//    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Map<String,Object> restMap = new HashMap<>(1);
        String token = request.getParameter("token");
        if(token != null) {
//            String username = tokenManager.getUserInfoFromToken(token);      //从token获取用户名
//            restMap.put("name",username);
            restMap.put("code","没有权限");
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(JSON.toJSONString(restMap));

    }


}

