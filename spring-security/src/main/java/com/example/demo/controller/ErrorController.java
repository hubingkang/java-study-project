package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping(path = "/noAuth")
    public String noAuthError(){
        System.out.println("进了ErrorController-403");
        return "Error403";
    }
}
