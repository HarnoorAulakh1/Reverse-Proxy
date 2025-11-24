package com.example.reverseproxy.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Upstream {
    private String name;
    private String[] servers;
}
