package com.example.reverseproxy.service;

import com.example.reverseproxy.config.CachedRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SendRequest {

    @Autowired
    private RestTemplate restTemplate;

    public void sendRequest(String url, CachedRequest cachedRequest, HttpServletResponse response){
        HttpEntity<String> httpEntity = new HttpEntity<>(null, cachedRequest.getCachedHeaders());
        try {

            ResponseEntity<String> external = restTemplate.exchange(
                    url,
                    HttpMethod.valueOf(cachedRequest.getMethod()),
                    httpEntity,
                    String.class
            );
            response.setStatus(external.getStatusCodeValue());
            response.getWriter().write(external.getBody());
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
