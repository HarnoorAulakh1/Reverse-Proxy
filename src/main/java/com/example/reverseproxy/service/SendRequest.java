package com.example.reverseproxy.service;

import com.example.reverseproxy.config.CachedRequest;
import com.example.reverseproxy.models.Location;
import com.example.reverseproxy.models.Upstream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class SendRequest {

    private final RestTemplate restTemplate;

    private final RedisService redisService;
    private final RoundRobin roundRobin;

    SendRequest(RestTemplate restTemplate,RedisService redisService,RoundRobin roundRobin){
        this.redisService=redisService;
        this.restTemplate=restTemplate;
        this.roundRobin=roundRobin;
    }

    public void redirect(CachedRequest cachedRequest, HttpServletResponse response) throws IOException {
        int port=cachedRequest.getLocalPort();
        Location location=getLocation(cachedRequest.getRequestURI().trim(),port);
        if(location!=null && location.getRedirect()!=null)
            sendRequest(location.getRedirect(),cachedRequest,response);
        else
            response.setStatus(404);
    }

    public Location getLocation(String prefix,int port){
        if(!prefix.isEmpty()) {
            if(prefix.length()>1)
                prefix=prefix.charAt(prefix.length()-1)=='/'?prefix:prefix+"/";
            if(prefix.length()>1) {
                for (int i = prefix.length() - 1; i > 0; i--) {
                    if (prefix.charAt(i) == '/') {
                        String prefix1 = port+":"+prefix.substring(0, i),prefix2=prefix.substring(0, i);
                        if (redisService.hasKey(port+":"+prefix1) && i==prefix.length()-1) {
                            return redisService.get(port + ":" + prefix, Location.class);
                        }
                        else if(redisService.hasKey("upstream:"+prefix1) && i==prefix.length()-1){
                            String name=redisService.get("upstream:"+prefix1,Upstream.class).getName();
                            String key=roundRobin.getUpstreamUrl(port+":"+name);
                            return Location.builder().prefix(prefix2).redirect(key).build();
                        }
                        else if(redisService.hasKey("upstream:"+prefix1 + "/*")){
                            String name=redisService.get("upstream:"+prefix1 + "/*",Upstream.class).getName();
                            String key=roundRobin.getUpstreamUrl(port+":"+name);
                            return Location.builder().prefix(prefix2).redirect(key).build();
                        }
                        else if (redisService.hasKey(prefix1 + "/*"))
                            return redisService.get(prefix1 + "/*", Location.class);
                    }
                }
            }
            else{
                prefix=port+":"+prefix;
                if (redisService.hasKey(prefix))
                    return redisService.get(prefix, Location.class);
                else if (redisService.hasKey(prefix + "*"))
                    return redisService.get(prefix + "*", Location.class);
            }
        }
        return null;
    }

    public void sendRequest(String url, CachedRequest cachedRequest, HttpServletResponse response) throws IOException {
        if(url==null || url.isEmpty()) {
            response.setStatus(404);
            return;
        }
        HttpEntity<String> httpEntity = new HttpEntity<>(null, cachedRequest.getCachedHeaders());
        ResponseEntity<String> external=null;
        try {
            external = restTemplate.exchange(
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
            response.setStatus(500);
            response.getWriter().write(e.getMessage());
        }
    }
}
