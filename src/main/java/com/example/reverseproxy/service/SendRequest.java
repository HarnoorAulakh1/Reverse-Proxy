package com.example.reverseproxy.service;

import com.example.reverseproxy.config.CachedRequest;
import com.example.reverseproxy.models.Location;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class SendRequest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    public void redirect(CachedRequest cachedRequest, HttpServletResponse response) throws IOException {
        Location location=getLocation(cachedRequest.getRequestURI().trim());
        System.out.println("lcoation="+" "+location);
        if(location!=null && location.getRedirect()!=null)
            sendRequest(location.getRedirect(),cachedRequest,response);
        else
            response.setStatus(404);
    }

    public Location getLocation(String uri){
        if(!uri.isEmpty()) {
            System.out.println(uri);
            if(uri.length()>1)
                uri=uri.charAt(uri.length()-1)=='/'?uri:uri+"/";
            if(uri.length()>1) {
                for (int i = uri.length() - 1; i > 0; i--) {
                    if (uri.charAt(i) == '/') {
                        String uri1 = uri.substring(0, i);
                        if(redisService.hasKey("upstream:"+uri1)){
                            //return redisService.get(uri1, Upstream.class);
                        }
                        else if (redisService.hasKey(uri1))
                            return redisService.get(uri1, Location.class);
                        else if (redisService.hasKey(uri1 + "/*"))
                            return redisService.get(uri1 + "/*", Location.class);
                    }
                }
            }
            else{
                if (redisService.hasKey(uri))
                    return redisService.get(uri, Location.class);
                else if (redisService.hasKey(uri + "*"))
                    return redisService.get(uri + "*", Location.class);
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
