package com.example.reverseproxy.config;

import com.example.reverseproxy.models.ConfigFileModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.io.IOException;

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
    public TomcatServletWebServerFactory servletContainer() throws IOException {
        File file=new File("/Users/harnoorsinghaulakh/Desktop/config.json");
        ObjectMapper objectMapper=new ObjectMapper();
        ConfigFileModel configFileModel=objectMapper.readValue(file,ConfigFileModel.class);
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();

        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(configFileModel.getServer().getListen());

        factory.addAdditionalTomcatConnectors(connector);
        return factory;
    }

}

