package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.imooc.mapper"})
@ComponentScan(basePackages = {"com.imooc","org.n3r.idworker"})
@EnableConfigurationProperties
public class ImoocMuxinNettyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImoocMuxinNettyApplication.class, args);
    }

}
