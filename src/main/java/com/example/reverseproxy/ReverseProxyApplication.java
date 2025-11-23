package com.example.reverseproxy;

import com.example.reverseproxy.config.CachedRequest;
import com.example.reverseproxy.models.Data1;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@SpringBootApplication
@RestController
public class ReverseProxyApplication {


    public static void main(String[] args) {
        SpringApplication.run(ReverseProxyApplication.class, args);
    }

    @GetMapping
    public String send(){
        return "Hello Jackson";
    }

    @PostMapping("/data")
    public String send(@RequestBody Data1 data, CachedRequest request){
        return "Hello Proxy";
    }
}
