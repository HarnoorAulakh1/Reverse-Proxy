package com.example.reverseproxy.config;


import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CachedResponse extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream cachedContent = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    public CachedResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new CachedServletOutputStream(cachedContent, super.getOutputStream());
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(getOutputStream(), true, StandardCharsets.UTF_8);
        }
        return writer;
    }

    public byte[] getCachedBody() {
        return cachedContent.toByteArray();
    }

    private static class CachedServletOutputStream extends ServletOutputStream {

        private final ByteArrayOutputStream cachedContent;
        private final ServletOutputStream original;

        public CachedServletOutputStream(ByteArrayOutputStream cachedContent, ServletOutputStream original) {
            this.cachedContent = cachedContent;
            this.original = original;
        }

        @Override
        public boolean isReady() {
            return original.isReady();
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            original.setWriteListener(writeListener);
        }

        @Override
        public void write(int b) throws IOException {
            cachedContent.write(b);
            original.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            cachedContent.write(b, off, len);
            original.write(b, off, len);
        }
    }
}


