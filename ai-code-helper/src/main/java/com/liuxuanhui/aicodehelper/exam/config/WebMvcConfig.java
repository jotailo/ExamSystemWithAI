package com.liuxuanhui.aicodehelper.exam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将项目根目录下的 uploads/ 目录映射为 /files/** 访问路径
        Path absolutePath = Paths.get(uploadPath).toAbsolutePath();
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + absolutePath + File.separator);
    }
}
