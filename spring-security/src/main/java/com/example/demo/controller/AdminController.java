package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "admin")
public class AdminController {

    @RequestMapping("index")
    public String adminIndex() {
        return "admin Index";
    }

    @RequestMapping("createAdmin")
   public String createAdmin(){
        return "boss can create admin";
    }
}
