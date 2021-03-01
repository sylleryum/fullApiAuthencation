package com.sylleryum.sylleryum.controller;

import com.sylleryum.sylleryum.email.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    EmailServiceImpl emailService;

    @Autowired
    public TestController(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @GetMapping(value = {"/v1/", "/v1", "/", ""})
    ResponseEntity<?> root(Pageable pageable){

        return ResponseEntity.ok("test");
    }
}
