package com.sylleryum.sylleryum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylleryum.sylleryum.exception.ResourceAlreadyExistsException;
import com.sylleryum.sylleryum.exception.ResourceNotFoundException;
import com.sylleryum.sylleryum.config.ResponseTokenBuilder;
import com.sylleryum.sylleryum.service.ApiUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@SpringBootTest
class SylleryumApplicationTests {

    //TODO remoooooooooooooooooove
    String loginkey = "e3b71a09-443b-43ef-8c23-24f5ed6a4f1f-7c85d03d-75d2-40ab-aec2-881475e10d33";
    String confirmationkey = "67364653-62cb-4307-b0d7-3008e79752d3-be3bac56-1902-41a5-802b-ff963405d215";


    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VydG9rZW4iLCJyb2xlIjoiUk9MRV9VU0VSIiwiZXhwIjo0NzM3MjM2NDAwfQ.pKWK1-MHDonPDPY2VJRggzTJXgNu-T4n6w3qS8KqiIVtdHr6Hu71BXHYAnrRvuKlnOKNy9J3TO0_i88qQT60dw";
    @Autowired
    ApiUserService apiUserService;
    @Autowired
    ResponseTokenBuilder responseTokenBuilder;


    @Test
    void contextLoads() throws JsonProcessingException, ResourceNotFoundException, ResourceAlreadyExistsException {

        Map testMap = Map.of("k1","v1","k2","v2");
        String json = new ObjectMapper().writeValueAsString(testMap);
//        String test = ControllerReturnStrings.RESET_ENABLE_TOKEN("test");
        System.out.println();

    }


}
