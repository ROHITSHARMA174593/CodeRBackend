package com.code.codeR.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestDeploymentController {
    @GetMapping("/")
    public String testDeployment() {
        return "Deployment Successful"; 
    }

    @GetMapping("/api/rohit")
    public String rohit() {
        return "Hello This is Apache JMeter Testing Response";
    }
    @GetMapping("/api/rohit/abc")
    public String rohit2() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        return list.stream().toList().toString();
    }
}
