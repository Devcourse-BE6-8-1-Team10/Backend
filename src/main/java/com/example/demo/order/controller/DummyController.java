package com.example.demo.order.controller;

import com.example.demo.global.rsData.RsData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {

    @GetMapping("/dummy")
    public RsData<String> dummy() {
        return RsData.of(200, "success", null);
    }

    @GetMapping("/")
    public String home() {
        return "반가워요";
    }
}