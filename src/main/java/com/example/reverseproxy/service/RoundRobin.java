package com.example.reverseproxy.service;

import com.example.reverseproxy.models.Upstream;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoundRobin {
    private HashMap<String, Queue<String>> current=new HashMap<>();
    private HashMap<String, Upstream> map=new HashMap<>();

    RoundRobin(ConfigFile configFile){
        map=configFile.getUpstreams();
        System.out.println(map);
        for(String st:map.keySet()){
            Upstream upstream=map.get(st);
            List<String> servers=upstream.getServers();
            current.put(st,new LinkedList<>());
            for(String server:servers){
                current.get(st).add(server);
            }
        }
        System.out.println(current);
    }

    public String getUpstreamUrl(String key){
        if(!map.containsKey(key))
            return null;
        String url=current.get(key).poll();
        current.get(key).add(url);
        return url;
    }
}
