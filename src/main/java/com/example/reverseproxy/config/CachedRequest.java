package com.example.reverseproxy.config;


import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

import java.io.*;
import java.util.Enumeration;

public class CachedRequest extends HttpServletRequestWrapper {

    @Getter
    private final byte[] cachedBody;
    @Getter
    private final HttpHeaders cachedHeaders;

    public CachedRequest(HttpServletRequest request) throws IOException {
        super(request);
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = requestInputStream.readAllBytes();
        this.cachedHeaders =extractHeaders(request);
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }
            @Override
            public boolean isReady() {
                return true;
            }
            @Override
            public void setReadListener(ReadListener readListener) {}
        };
    }

    private HttpHeaders extractHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            Enumeration<String> values = request.getHeaders(name);

            while (values.hasMoreElements()) {
                headers.add(name, values.nextElement());
            }
        }
        //headers.set("host", "localhost:4000");
        return headers;
    }


    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

}
