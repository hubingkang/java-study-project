package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "demo")
public class HelloController {
    @RequestMapping(path = "index")
    public String index(){
        return "Hello Security";
    }
}
