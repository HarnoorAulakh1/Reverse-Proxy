package com.example.reverseproxy.config;


import com.example.reverseproxy.file.ConfigFile;
import com.example.reverseproxy.models.ConfigFileModel;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.Filter ;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class Interceptor implements Filter {

    private final RestTemplate restTemplate;
    private final ConfigFileModel configFileModel;
    private List<Class<?>> list;
    @Getter
    private CachedRequest cachedRequest;
    @Getter
    private CachedResponse cachedResponse;

    public Interceptor(RestTemplate restTemplate,ConfigFileModel configFileModel) {
        this.restTemplate = restTemplate;
        this.configFileModel=configFileModel;
    }

    @Override
    public void doFilter(
            ServletRequest req,
            ServletResponse res,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        cachedRequest=new CachedRequest(request);
        cachedResponse=new CachedResponse(response);

        System.out.println(cachedRequest.getCachedHeaders());
        System.out.println(cachedRequest.getRequestURI());
    }


}