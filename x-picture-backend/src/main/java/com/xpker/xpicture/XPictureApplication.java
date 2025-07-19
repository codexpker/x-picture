package com.xpker.xpicture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.xpker.xpicture.mapper")
@SpringBootApplication
@EnableAsync
@EnableAspectJAutoProxy(exposeProxy = true)
public class XPictureApplication {

    public static void main(String[] args) {
        SpringApplication.run(XPictureApplication.class, args);
    }

}
