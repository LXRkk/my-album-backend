package com.lxrkk.myalbumbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.lxrkk.myalbumbackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class MyAlbumBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyAlbumBackendApplication.class, args);
    }

}
