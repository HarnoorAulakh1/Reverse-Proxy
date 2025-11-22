package com.example.reverseproxy.file;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ConfigFile {

    @Autowired
    private ObjectMapper objectMapper;

    public JsonNode getRoot(String fileUrl) throws IOException {
        File file=new File(fileUrl);
        return objectMapper.readTree(file);
    }

}
