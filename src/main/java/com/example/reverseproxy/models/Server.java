package com.example.reverseproxy.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Server{
    @Getter
    private String listen;
    private String server_name;
    private List<Location> locations;
}
