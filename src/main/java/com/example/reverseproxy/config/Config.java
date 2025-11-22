package com.example.reverseproxy.config;

import com.example.reverseproxy.file.ConfigFile;
import com.example.reverseproxy.models.ConfigFileModel;
import com.example.reverseproxy.models.Location;
import com.example.reverseproxy.service.RedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Configuration
public class Config {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RedisTemplate<String,String> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String,String> ob1=new RedisTemplate<>();
        ob1.setConnectionFactory(redisConnectionFactory);
        ob1.setKeySerializer(new StringRedisSerializer());
        ob1.setValueSerializer(new StringRedisSerializer());
        return ob1;
    }

    @Bean
    public ConfigFileModel setFile(RedisService redisService) throws IOException {
        File file=new File("/Users/harnoorsinghaulakh/Desktop/config.json");
        ObjectMapper objectMapper=new ObjectMapper();
        ConfigFileModel configFileModel=objectMapper.readValue(file,ConfigFileModel.class);
        List<Location> locations=configFileModel.getServer().getLocations();

        for(Location location:locations){
            redisService.set(location.getPrefix(),location,3600);
        }
        System.out.println(redisService.get("/api/",Location.class));
        return configFileModel;
    }

}

