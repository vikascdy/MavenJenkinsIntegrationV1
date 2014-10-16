package com.edifecs.core.configuration;

import java.io.InputStream;

public class MetadataStream {

    private String contentRepositoryPath;
    
    private InputStream inputStream;

    public String getContentRepositoryPath() {
        return contentRepositoryPath;
    }

    public void setContentRepositoryPath(String contentRepositoryPath) {
        this.contentRepositoryPath = contentRepositoryPath;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
