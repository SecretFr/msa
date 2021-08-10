package com.example.secondservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/second-service")
public class SecondServiceController {
    // GET http://localhost:8081/first-service/welcome
    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome to the Second Service";
    }
}
