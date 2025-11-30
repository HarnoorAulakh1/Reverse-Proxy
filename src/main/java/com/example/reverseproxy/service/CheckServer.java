package com.example.reverseproxy.service;

import com.example.reverseproxy.models.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Exchanger;

@Service
public class CheckServer {
    @Autowired
    private RestTemplate restTemplate;
    public void check(String url){
        try {
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), String.class);
        }
        catch(Exception e){
            throw (e);
        }
    }
    public void check(List<String> url){
        try {
            for (String s : url)
                restTemplate.exchange(s, HttpMethod.GET, new HttpEntity<>(null), String.class);
        }
        catch(Exception e){
            throw (e);
        }
    }
}
