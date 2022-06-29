package com.softj.itple.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix="file")
@Data
public class FileStorageProperties {
    private String uploadDir;
}