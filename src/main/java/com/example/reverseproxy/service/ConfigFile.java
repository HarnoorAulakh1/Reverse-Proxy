package com.example.reverseproxy.service;

import com.example.reverseproxy.config.Config;
import com.example.reverseproxy.models.ConfigFileModel;
import com.example.reverseproxy.models.Location;
import com.example.reverseproxy.models.Upstream;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ConfigFile {
    @Getter
    private ConfigFileModel configFileModel;
    @Getter
    private int port;
    @Getter
    private List<Location> locations;
    private HashMap<String, Upstream> map=new HashMap<>();
    private List<Upstream> upstreamGroups;


    ConfigFile(CheckServer checkServer,RedisService redisService) throws Exception{
        File file=new File("/Users/harnoorsinghaulakh/Desktop/config.json");
        ObjectMapper objectMapper=new ObjectMapper();
        configFileModel=objectMapper.readValue(file, ConfigFileModel.class);
        upstreamGroups=configFileModel.getServer().getUpstream();
        port=configFileModel.getServer().getListen();
        locations=configFileModel.getServer().getLocations();
        map=getUpstreams();
        try {
            for (Location location : locations) {
                if(!location.getRedirect().isEmpty() && location.getRedirect().charAt(0)=='$'){
                    if(!map.containsKey(port+":"+location.getRedirect().substring(1))) {
                        throw (new Exception("No Upstream group by the name " + location.getRedirect() + " found"));
                    }
                    Upstream upstreamGroup=map.get(port+":"+location.getRedirect().substring(1));
                    checkServer.check(upstreamGroup.getServers());
                    redisService.set("upstream:"+port+":"+location.getPrefix(),upstreamGroup,3600);
                }
                else{
                    checkServer.check(location.getRedirect());
                    redisService.set(port+":"+location.getPrefix(),location,3600);
                }
            }
        }
        catch(Exception e){
            throw (e);
        }
    }


    public HashMap<String,Upstream> getUpstreams(){
        if(upstreamGroups!=null)
            for (Upstream upstream : upstreamGroups)
                map.put(port+":"+upstream.getName(), upstream);
        return map;
    }
    public List<Upstream> getUpstream(){
        return configFileModel.getServer().getUpstream();
    }

    public boolean check(int port) throws IOException {
        return this.port == port;
    }

}
