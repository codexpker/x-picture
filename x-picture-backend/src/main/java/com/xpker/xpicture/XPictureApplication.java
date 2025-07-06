package com.xpker.xpicture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.xpker.xpicture.mapper")
@SpringBootApplication
public class XPictureApplication {

    public static void main(String[] args) {
        SpringApplication.run(XPictureApplication.class, args);
    }

}
