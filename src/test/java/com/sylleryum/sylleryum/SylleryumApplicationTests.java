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
