package com.liuweiwei.shiro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created with IntelliJ IDEA
 *
 * @author liuweiwei 505831001@qq.com
 * @since 2018-04-06
 */
@SpringBootApplication
@MapperScan(value = "com.howie.shiro.mapper")
public class ShiroApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShiroApplication.class, args);
    }
}
