package com.example.reverseproxy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService  {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    public <T> T get(String key,Class<T> entityClass) {
        try {
            String ob = redisTemplate.opsForValue().get(key);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(ob.toString(), entityClass);
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public void set(String key,Object o,long ttl) {
        try{
            ObjectMapper mapper=new ObjectMapper();
            String json=mapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key,json,ttl, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
