package com.sap.multidb;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan(basePackages = "com.sap.multidb")
//@EnableAutoConfiguration
@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {

}
