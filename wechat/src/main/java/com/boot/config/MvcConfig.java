package com.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
@Primary
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    //视图控制器
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/toregister").setViewName("register");
    }


    @Override
    //静态资源映射
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //朋友圈得图片要使用绝对路径
        registry.addResourceHandler("/flage/**").addResourceLocations("file:F:/XXXXX/");
    }


}
