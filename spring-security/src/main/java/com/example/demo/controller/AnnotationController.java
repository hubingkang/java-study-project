package com.example.demo.controller;

import com.example.demo.entity.SendInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "department")
public class AnnotationController {

    private static Logger logger = LoggerFactory.getLogger(AnnotationController.class);

    /*指定需要 javaee 或 javase 角色*/
    @RequestMapping("java")
    @Secured({"ROLE_javaee","ROLE_javase"})
    public String JavaDepartment(){
        logger.info("该账号具有JavaSe或JavaEE权限....");
        return "Java角色账号访问";
    }

    /*指定需要 worker:java:create 权限*/
    @RequestMapping("javaInfoCreate")
    @PreAuthorize("hasAuthority('worker:java:create')")
    public String preAuth(){
        logger.info("创建方法执行了....");
        return "Java信息创建方法执行前验证权限";
    }

    /*PreAuthorize访问前的权限验证注解讲解*/
    @RequestMapping("PreAuthorize1")
    @PreAuthorize("hasRole('ROLE_javase')")
    public String PrehasRole(){
        return "Java信息创建方法执行前验证角色,ROLE_javase";
    }

    @RequestMapping("PrehasAnyRole")
    @PreAuthorize("hasAnyRole({'ROLE_javaee','ROLE_javase'})")
    public String PrehasAnyRole(){
        return "Java信息创建方法执行前验证角色,含有ROLE_javaee或ROLE_javase";
    }

    @PreAuthorize("isRememberMe()")
    @RequestMapping("PerisRememberMe")
    public String PerisRememberMe(){
        return "只有勾选记住我才能访问";
    }

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping("PerisFullyAuthenticated")
    public String PerisFullyAuthenticated(){
        return "已认证且非记住我用户允许访问";
    }

    /*指定需要 worker:java:delete 或 worker:java:del 权限 */
    @RequestMapping("javaInfoDelete")
    @PostAuthorize("hasAnyAuthority({'worker:java:delete','worker:java:del'})")
    public String postAuth(){
        logger.info("删除方法执行了....");
        return "Java信息删除方法执行后验证权限";
    }

    /* 在方法执行后对传递的数据进行过滤，只留下 name 为 dmbjz 的数据，filterObject相当于遍历中的当前数据值，返回值必须是集合 */
    @RequestMapping("postfilter")
    @PostFilter("filterObject.name == 'demo'")
    public List<SendInfo> postInfoFilter(){

        List<SendInfo> sendInfoList = new ArrayList<>();
        sendInfoList.add(new SendInfo("xiaoming","123"));
        sendInfoList.add(new SendInfo("xiaoqian","456"));
        sendInfoList.add(new SendInfo("demo","789"));
        sendInfoList.forEach(System.out::println);
        return sendInfoList;

    }

    /* 在方法执行前对传递的数据进行过滤，只留下 name 为 dmbjz 的数据，filterObject相当于遍历中的当前数据值，返回值必须是集合 */
    @RequestMapping("prefilter")
    @PreFilter("filterObject.name == 'demo'")
    public List<SendInfo> preInfoFilter(@RequestBody List<SendInfo> sendInfo){

        sendInfo.forEach(System.out::println);
        return sendInfo;

    }
}

