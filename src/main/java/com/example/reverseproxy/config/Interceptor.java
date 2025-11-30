package com.example.reverseproxy.config;


import com.example.reverseproxy.service.ConfigFile;
import com.example.reverseproxy.service.SendRequest;
import jakarta.servlet.Filter ;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class Interceptor implements Filter {
    private final SendRequest sendRequest;
    private final ConfigFile configFile;
    private List<Class<?>> list;
    @Getter
    private CachedRequest cachedRequest;
    @Getter
    private CachedResponse cachedResponse;

    public Interceptor(SendRequest sendRequest, ConfigFile configFile) {
        this.sendRequest = sendRequest;
        this.configFile = configFile;
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
        int port=cachedRequest.getLocalPort();
        //System.out.println(cachedRequest.getRequestURI()+" "+ cachedRequest.getLocalPort());
        if(configFile.check(port)) {
            sendRequest.redirect(cachedRequest, response);
        }
        else{
            response.setStatus(404);
            response.getWriter().write(port+" not allowed");
        }

    }


}