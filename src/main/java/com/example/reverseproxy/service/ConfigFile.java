package com.example.reverseproxy.service;

import com.example.reverseproxy.config.Config;
import com.example.reverseproxy.models.ConfigFileModel;
import com.example.reverseproxy.models.Location;
import com.example.reverseproxy.models.Upstream;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Component
public class ConfigFile {
    @Getter
    private ConfigFileModel configFileModel;
    @Getter
    private int port;
    @Getter
    private List<Location> locations;
    HashMap<String, Upstream> map=new HashMap<>();
    Upstream[] upstreamGroups;

    ConfigFile(CheckServer checkServer,RedisService redisService) throws Exception {
        File file=new File("/Users/harnoorsinghaulakh/Desktop/config.json");
        ObjectMapper objectMapper=new ObjectMapper();
        configFileModel=objectMapper.readValue(file,ConfigFileModel.class);
        Upstream[] upstreamGroups=configFileModel.getServer().getUpstream();
        port=configFileModel.getServer().getListen();
        locations=configFileModel.getServer().getLocations();
        try {
            for (Location location : locations) {
                if(!location.getRedirect().isEmpty() && location.getRedirect().charAt(0)=='$'){
                    if(!map.containsKey(location.getRedirect())) {
                        throw (new Exception("No Upstream group by the name " + location.getRedirect() + "found"));
                    }
                    Upstream upstreamGroup=map.get(port+":"+location.getRedirect().substring(1));
                    checkServer.check(upstreamGroup.getServers());
                    redisService.set(port+":"+"upstream:"+location.getPrefix(),upstreamGroup,3600);
                }
                else{
                    checkServer.check(location.getRedirect());
                }
                redisService.set(location.getPrefix(),location,3600);
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
    public Upstream[] getUpstream(){
        return configFileModel.getServer().getUpstream();
    }

}
